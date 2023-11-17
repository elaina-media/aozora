package net.mikoto.aozora.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtflys.forest.exceptions.ForestNetworkException;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import net.mikoto.aozora.client.PixivClient;
import net.mikoto.aozora.mapper.ArtworkMapper;
import net.mikoto.aozora.model.Artwork;
import net.mikoto.aozora.service.ArtworkService;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
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
    /**
     * Pixiv usual date format.
     * <p>
     * e.g.
     * 2020-03-05T01:23:36+00:00
     * 2021-07-15T15:48:17+00:00
     */
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    private static final String PIXIV_IMAGE_URL = "https://i.pximg.net";
    @Autowired
    public ArtworkServiceImpl(PixivClient pixivClient) {
        this.pixivClient = pixivClient;
    }

    @Override
    public Artwork getRemoteArtwork(int artworkId) {
        String rawData;
        try {
            rawData = pixivClient.getArtwork(artworkId, "PHPSESSID=85113018_Sov80xYOtoZIsKB2D8aUv36bxD9pljL3");
        } catch (ForestNetworkException e) {
            if (e.getStatusCode() == 404) {
                return null;
            } else {
                throw e;
            }
        }
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
        artwork.setLikeCount(artworkBodyRawData.getIntValue("viewCount"));
        Date createTime, updateTime;
        try {
            createTime = DATE_FORMAT.parse(artworkBodyRawData.getString("createDate"));
            updateTime = DATE_FORMAT.parse(artworkBodyRawData.getString("uploadDate"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        artwork.setCreateTime(createTime);
        artwork.setUpdateTime(updateTime);
        artwork.setPatchTime(new Date());
        // 链接
        artwork.setIllustUrlMini(artworkBodyRawData.getJSONObject("urls").getString("mini").replace(PIXIV_IMAGE_URL, ""));
        artwork.setIllustUrlOriginal(artworkBodyRawData.getJSONObject("urls").getString("original").replace(PIXIV_IMAGE_URL, ""));
        artwork.setIllustUrlRegular(artworkBodyRawData.getJSONObject("urls").getString("regular").replace(PIXIV_IMAGE_URL, ""));
        artwork.setIllustUrlThumb(artworkBodyRawData.getJSONObject("urls").getString("thumb").replace(PIXIV_IMAGE_URL, ""));
        artwork.setIllustUrlSmall(artworkBodyRawData.getJSONObject("urls").getString("small").replace(PIXIV_IMAGE_URL, ""));

        // 标签及年龄分级
        Artwork.Grading grading = Artwork.Grading.General;

        StringJoiner tags = new StringJoiner(";");
        for (int i = 0;
             i <
                     artworkBodyRawData
                             .getJSONObject("tags")
                             .getJSONArray("tags")
                             .size();
             i++) {
            String tag =
                    artworkBodyRawData.getJSONObject("tags")
                            .getJSONArray("tags")
                            .getJSONObject(i)
                            .getString("tag");
            if ("R-18".equals(tag)) {
                grading = Artwork.Grading.R18;
            } else if ("R-18G".equals(tag)) {
                grading = Artwork.Grading.R18G;
            }

            tags.add(tag);
        }
        artwork.setGrading(grading);
        artwork.setTags(tags.toString());

        // 处理系列作品数据
        JSONObject seriesJson = artworkRawData.getJSONObject("seriesNavData");

        artwork.setHasSeries(seriesJson != null);
        if (!artwork.isHasSeries()) {
            log.info("[Aozora] Get Artwork: " + artwork.getArtworkId());
            log.info("[Aozora] ArtworkJson: " + JSONObject.toJSONString(artwork));
            log.info("[Aozora] ArtworkTableId: " + (artwork.getArtworkId() % 100));
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

        log.info("[Aozora] Get Artwork: " + artwork.getArtworkId());
        log.info("[Aozora] ArtworkJson: " + JSONObject.toJSONString(artwork));
        log.info("[Aozora] ArtworkTableId: " + (artwork.getArtworkId() % 100));
        return artwork;
    }
}
