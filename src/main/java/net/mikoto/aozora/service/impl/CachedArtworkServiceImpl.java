package net.mikoto.aozora.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.java.Log;
import net.mikoto.aozora.client.PixivClient;
import net.mikoto.aozora.model.Artwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
@Service("CachedArtworkService")
@CacheConfig(cacheNames = "artwork")
public class CachedArtworkServiceImpl
        extends ArtworkServiceImpl {
    @Autowired
    public CachedArtworkServiceImpl(PixivClient pixivClient) {
        super(pixivClient);
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
