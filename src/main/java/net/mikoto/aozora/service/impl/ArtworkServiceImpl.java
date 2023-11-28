package net.mikoto.aozora.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtflys.forest.exceptions.ForestNetworkException;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.mikoto.aozora.client.PixivClient;
import net.mikoto.aozora.mapper.ArtworkMapper;
import net.mikoto.aozora.model.AozoraConfig;
import net.mikoto.aozora.model.Artwork;
import net.mikoto.aozora.service.ArtworkService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringJoiner;

/**
 * @author mikoto
 * &#064;date 2023/11/16
 * Create for aozora
 */
@Component
@Log
public class ArtworkServiceImpl extends ServiceImpl<ArtworkMapper, Artwork> implements ArtworkService {
    private final PixivClient pixivClient;
    private final AozoraConfig defaultConfig;
    /**
     * Pixiv usual date format.
     * <p>
     * e.g.
     * 2020-03-05T01:23:36+00:00
     * 2021-07-15T15:48:17+00:00
     */
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    private static final String PIXIV_IMAGE_URL = "https://i.pximg.net";
    private static int sessionIdCount = 0;
    private static int getCount = 0;
    @Setter
    @Getter
    private int cachedArtworksCount;
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
            artwork = getRemoteArtwork(artworkId, defaultConfig.getPhpSessionId()[sessionIdCount], defaultConfig.isLog());
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
                artwork = getRemoteArtwork(artworkId, defaultConfig.getPhpSessionId()[sessionIdCount], defaultConfig.isLog());
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

    @SneakyThrows
    @Override
    public Artwork getRemoteArtwork(int artworkId, String sessionId, boolean isLog) {
        String rawData = pixivClient.getArtwork(artworkId, "PHPSESSID=" + sessionId);
        JSONObject artworkRawData = JSON.parseObject(rawData);

        if (artworkRawData.getBooleanValue("error")) {
            return null;
        }

        Artwork artwork = new Artwork();
        JSONObject artworkBodyRawData = artworkRawData.getJSONObject("body");

        // 基础数据
        artwork.setArtworkId(Integer.parseInt(artworkBodyRawData.getString("illustId")));
        artwork.setArtworkTitle(artworkBodyRawData.getString("illustTitle"));
        artwork.setAuthorId(Integer.parseInt(artworkBodyRawData.getString("userId")));
        artwork.setAuthorName(artworkBodyRawData.getString("userName"));
        artwork.setDescription(artworkBodyRawData.getString("description"));
        artwork.setPageCount(artworkBodyRawData.getIntValue("pageCount"));
        artwork.setBookmarkCount(artworkBodyRawData.getIntValue("bookmarkCount"));
        artwork.setLikeCount(artworkBodyRawData.getIntValue("likeCount"));
        artwork.setViewCount(artworkBodyRawData.getIntValue("viewCount"));
        // 时间
        Date createTime = DATE_FORMAT.parse(artworkBodyRawData.getString("createDate"));
        Date updateTime = DATE_FORMAT.parse(artworkBodyRawData.getString("uploadDate"));
        artwork.setCreateTime(createTime);
        artwork.setUpdateTime(updateTime);
        artwork.setPatchTime(new Date());
        // 链接
        JSONObject urls = artworkBodyRawData.getJSONObject("urls");
        artwork.setIllustUrlMini(urls.getString("mini").replace(PIXIV_IMAGE_URL, ""));
        artwork.setIllustUrlOriginal(urls.getString("original").replace(PIXIV_IMAGE_URL, ""));
        artwork.setIllustUrlRegular(urls.getString("regular").replace(PIXIV_IMAGE_URL, ""));
        artwork.setIllustUrlThumb(urls.getString("thumb").replace(PIXIV_IMAGE_URL, ""));
        artwork.setIllustUrlSmall(urls.getString("small").replace(PIXIV_IMAGE_URL, ""));
        // 年龄分级
        Artwork.Grading grading = Artwork.Grading.General;
        // 标签
        StringJoiner tags = new StringJoiner(";");
        JSONArray tagsArray = artworkBodyRawData
                .getJSONObject("tags")
                .getJSONArray("tags");
        for (int i = 0; i < tagsArray.size(); i++) {
            String tag = tagsArray.getJSONObject(i).getString("tag");
            // 年龄分级判断
            if ("R-18".equals(tag)) {
                grading = Artwork.Grading.R18;
            } else if ("R-18G".equals(tag)) {
                grading = Artwork.Grading.R18G;
            }

            // 标签收集
            tags.add(tag);
        }
        artwork.setGrading(grading);
        artwork.setTags(tags.toString());

        // 系列作品数据
        JSONObject seriesJson = artworkRawData.getJSONObject("seriesNavData");

        artwork.setHasSeries(seriesJson != null);
        if (!artwork.isHasSeries()) {
            if (isLog) {
                doLogArtwork(artwork);
            }
            return artwork;
        }

        assert seriesJson != null;
        artwork.setSeriesId(Integer.parseInt(seriesJson.getString("seriesId")));
        artwork.setSeriesOrder(seriesJson.getIntValue("order"));

        JSONObject previousJson = seriesJson.getJSONObject("prev");
        if (previousJson != null) {
            artwork.setPreviousArtworkId(Integer.parseInt(previousJson.getString("id")));
            artwork.setPreviousArtworkTitle(previousJson.getString("title"));
        }

        JSONObject nextJson = seriesJson.getJSONObject("next");
        if (nextJson != null) {
            artwork.setNextArtworkId(Integer.parseInt(nextJson.getString("id")));
            artwork.setNextArtworkTitle(nextJson.getString("title"));
        }

        if (isLog) {
            doLogArtwork(artwork);
        }
        return artwork;
    }

    private void doLogArtwork(@NotNull Artwork artwork) {
        log.info("[Aozora] Get Artwork: " + artwork.getArtworkId());
        log.info("[Aozora] ArtworkJson: " + JSONObject.toJSONString(artwork));
        log.info("[Aozora] ArtworkTableId: " + (artwork.getArtworkId() % 100));
    }

    private void nextSessionId(@NotNull AozoraConfig config) {
        sessionIdCount++;
        if (sessionIdCount >= config.getPhpSessionId().length) {
            sessionIdCount = 0;
        }
    }
}
