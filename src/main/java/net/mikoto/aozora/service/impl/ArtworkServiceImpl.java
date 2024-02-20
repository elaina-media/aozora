package net.mikoto.aozora.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.CacheableServiceImpl;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.mikoto.aozora.client.PixivClient;
import net.mikoto.aozora.mapper.ArtworkMapper;
import net.mikoto.aozora.model.AozoraConfig;
import net.mikoto.aozora.model.Artwork;
import net.mikoto.aozora.model.ExtensionTag;
import net.mikoto.aozora.service.ArtworkService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static net.mikoto.aozora.model.table.Tables.ARTWORK;

/**
 * @author mikoto
 * &#064;date 2023/11/16
 * Create for aozora
 */
@Log
@Component
@CacheConfig(cacheNames = "artwork")
public class ArtworkServiceImpl
        extends CacheableServiceImpl<ArtworkMapper, Artwork>
        implements ArtworkService {
    // Beans
    private final PixivClient pixivClient;
    private final AozoraConfig defaultConfig;
    private static int sessionIdCount = 0;
    private static int getCount = 0;
    @Getter @Setter
    private int cachedArtworksCount;


    // Dynamic configs
    private String[] cookies;
    private String cpsVersion;
    @Autowired
    public ArtworkServiceImpl(PixivClient pixivClient, AozoraConfig defaultConfig) {
        this.pixivClient = pixivClient;
        this.defaultConfig = defaultConfig;
    }

    @SneakyThrows
    @Override
    public Artwork getRemoteArtwork(int artworkId) {
        // 计数器刷新&归零&切换SessionId
        getCount++;
        if (getCount >= 5) {
            getCount = 0;
            sessionIdCount++;
            if (sessionIdCount >= defaultConfig.getPhpSessionId().length) {
                sessionIdCount = 0;
            }
        }

        Artwork artwork;
        try {
            artwork = getRemoteArtwork(artworkId, defaultConfig.getPhpSessionId()[sessionIdCount]);
        } catch (ForestNetworkException e) {
            if (e.getStatusCode() == 404) {
                return null;
            } else if (e.getStatusCode() == 429) {
                log.info(
                        "The session id temporary unavailable: " +
                                defaultConfig.getPhpSessionId()[sessionIdCount] +
                                " at artwork id :" +
                                artworkId
                );
                sessionIdCount++;
                if (sessionIdCount >= defaultConfig.getPhpSessionId().length) {
                    sessionIdCount = 0;
                }
                Thread.sleep(60000);
                log.info("Restart patch with: " + defaultConfig.getPhpSessionId()[sessionIdCount]);
                artwork = getRemoteArtwork(artworkId, defaultConfig.getPhpSessionId()[sessionIdCount]);
            } else {
                throw e;
            }
        }

        return artwork;
    }

    @Override
    public byte[] getImage(String url) {
        return pixivClient.getImage(url);
    }

    @Override
    public ExtensionTag[] getExtensionTags(String tag, String lang) {
        String rawData = pixivClient.getCps(tag, lang);
        JSONObject cps = JSON.parseObject(rawData);
        JSONArray tags = cps.getJSONArray("candidates");
        ExtensionTag[] extensionTags = new ExtensionTag[tags.size()];

        for (int i = 0; i < tags.size(); i++) {
            JSONObject tagJson = tags.getJSONObject(i);
            ExtensionTag extensionTag = new ExtensionTag();
            if (tagJson.getString("type").equals("tag_translation")) {
                extensionTag.setTagName(tagJson.getString("tag_name"));
                extensionTag.setTagName(tagJson.getString("tag_translation"));
            } else if (tagJson.getString("type").equals("prefix")) {
                extensionTag.setTagName(tagJson.getString("tag_name"));
            }
            extensionTags[i] = extensionTag;
        }
        return extensionTags;
    }

    @SneakyThrows
    @Override
    public Artwork getRemoteArtwork(int artworkId, String sessionId) {
        String rawData = pixivClient.getArtwork(artworkId);
        JSONObject artworkRawData = JSON.parseObject(rawData);


        return Artwork.parseFromRawJson(artworkRawData);
    }

    private void nextSessionId(@NotNull AozoraConfig config) {
        sessionIdCount++;
        if (sessionIdCount >= config.getPhpSessionId().length) {
            sessionIdCount = 0;
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean remove(QueryWrapper query) {
        return super.remove(query);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        return super.removeByIds(ids);
    }

    // 根据查询条件更新时，实体类主键可能为 null。
    @Override
    @CacheEvict(allEntries = true)
    public boolean update(Artwork entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Artwork entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<Artwork> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public Artwork getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public Artwork getOne(QueryWrapper query) {
        return super.getOne(query);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public <R> R getOneAs(QueryWrapper query, Class<R> asType) {
        return super.getOneAs(query, asType);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public List<Artwork> list(QueryWrapper query) {
        return super.list(query);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public <R> List<R> listAs(QueryWrapper query, Class<R> asType) {
        return super.listAs(query, asType);
    }

    // 无法通过注解进行缓存操作
    @Override
    @Deprecated
    public List<Artwork> listByIds(Collection<? extends Serializable> ids) {
        return super.listByIds(ids);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public long count(QueryWrapper query) {
        query.select(ARTWORK.ARTWORK_ID);
        return super.count(query);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #page.getPageSize() + ':' + #page.getPageNumber() + ':' + #query.toSQL()")
    public <R> Page<R> pageAs(Page<R> page, QueryWrapper query, Class<R> asType) {
        return super.pageAs(page, query, asType);
    }
}
