package net.mikoto.aozora.forward.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.SneakyThrows;
import net.mikoto.aozora.forward.service.ArtworkService;
import net.mikoto.aozora.forward.service.AvailableRoute;
import net.mikoto.aozora.model.Artwork;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringJoiner;

/**
 * @author mikoto
 * &#064;date 2023/11/28
 * Create for aozora
 */
@Service
public class ArtworkServiceImpl implements ArtworkService {

    /**
     * Pixiv usual date format.
     * <p>
     * e.g.
     * 2020-03-05T01:23:36+00:00
     * 2021-07-15T15:48:17+00:00
     */
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    private static final String PIXIV_IMAGE_URL = "https://i.pximg.net";

    @Override
    public Object parseRawData(@NotNull AvailableRoute availableRoute, JSONObject rawData) {
        switch (availableRoute) {
            case Artwork -> {
                return parseToArtwork(rawData);
            }
            default -> throw new RuntimeException("Unavailable route");
        }
    }

    @SneakyThrows
    private Artwork parseToArtwork(JSONObject rawData) {
        if (rawData.getBooleanValue("error")) {
            return null;
        }

        Artwork artwork = new Artwork();
        JSONObject artworkBodyRawData = rawData.getJSONObject("body");

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
        com.alibaba.fastjson2.JSONObject urls = artworkBodyRawData.getJSONObject("urls");
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
        JSONObject seriesJson = rawData.getJSONObject("seriesNavData");

        artwork.setHasSeries(seriesJson != null);
        if (!artwork.isHasSeries()) {
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

        return artwork;
    }
}
