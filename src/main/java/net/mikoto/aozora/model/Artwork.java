package net.mikoto.aozora.model;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringJoiner;

/**
 * @author mikoto
 * Created at 21:30:24, 2021/9/19
 *
 * sql:
 * create table pixiv.artwork
 * (
 *     pk_artwork_id          bigint unsigned not null
 *         primary key,
 *     artwork_title          varchar(32)     not null,
 *     author_id              bigint unsigned not null,
 *     author_name            varchar(15)     not null,
 *     has_series             tinyint(1)      not null,
 *     description            varchar(3000)   null,
 *     illust_url_small       varchar(80)     not null,
 *     illust_url_original    varchar(60)     not null,
 *     illust_url_mini        varchar(70)     not null,
 *     illust_url_thumb       varchar(80)     not null,
 *     illust_url_regular     varchar(60)     not null,
 *     page_count             int             not null,
 *     bookmark_count         int             not null,
 *     like_count             int             not null,
 *     view_count             int             not null,
 *     grading                tinyint(1)      not null,
 *     tags                   varchar(350)    not null,
 *     create_time            datetime        not null,
 *     update_time            datetime        not null,
 *     patch_time             datetime        not null,
 *     series_id              bigint unsigned not null,
 *     series_order           int             not null,
 *     next_artwork_id        bigint unsigned null,
 *     next_artwork_title     varchar(32)     null,
 *     previous_artwork_id    bigint unsigned null,
 *     previous_artwork_title varchar(32)     null,
 *     constraint pk_artwork_id
 *         unique (pk_artwork_id)
 * );
 */
@Data
@Table("artwork")
public class Artwork {
    @Id
    private Integer artworkId;
    private String artworkTitle;
    private int authorId;
    private String authorName;
    private boolean hasSeries;
    private String description;
    private String illustUrlSmall;
    private String illustUrlOriginal;
    private String illustUrlMini;
    private String illustUrlThumb;
    private String illustUrlRegular;
    private int pageCount;
    private int bookmarkCount;
    private int likeCount;
    private int viewCount;
    private Grading grading;
    private String tags;
    private Date createTime;
    private Date updateTime;
    private Date patchTime;
    private int seriesId = 0;
    private int seriesOrder = 0;
    private int nextArtworkId = 0;
    private String nextArtworkTitle = null;
    private int previousArtworkId = 0;
    private String previousArtworkTitle = null;
    private boolean isAi;
    private boolean isManga;

    @Getter
    public enum Grading {
        /**
         * The general grade is for all people.
         */
        General(0),

        /**
         * The R-18 grade is for a person who is over 18 years old.
         */
        R18(1),

        /**
         * The R-18G grade is for a person who is over 18 years old and have good mental ability.
         */
        R18G(2);

        private final int grading;

        Grading(int grading) {
            this.grading = grading;
        }

        public static Grading getGrading(int i) {
            for (Grading g : Grading.values()) {
                if (g.getGrading() == i) {
                    return g;
                }
            }
            throw new RuntimeException("Unsupported grading");
        }
    }


    /**
     * Pixiv usual date format.
     * <p>
     * e.g.
     * 2020-03-05T01:23:36+00:00
     * 2021-07-15T15:48:17+00:00
     */
    @Column(ignore = true)
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    @Column(ignore = true)
    private static final String PIXIV_IMAGE_URL = "https://i.pximg.net";

    @SneakyThrows
    public static Artwork parseFromRawJson(JSONObject artworkRawData) {
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
        Grading grading = Grading.General;
        // 标签
        StringJoiner tags = new StringJoiner(";");
        JSONArray tagsArray = artworkBodyRawData
                .getJSONObject("tags")
                .getJSONArray("tags");
        for (int i = 0; i < tagsArray.size(); i++) {
            String tag = tagsArray.getJSONObject(i).getString("tag");
            // 年龄分级判断
            if ("R-18".equals(tag)) {
                grading = Grading.R18;
            } else if ("R-18G".equals(tag)) {
                grading = Grading.R18G;
            }

            // 标签收集
            tags.add(tag);
        }
        artwork.setGrading(grading);
        artwork.setTags(tags.toString());

        // AI判断、漫画判断
        artwork.setAi(2 == artworkBodyRawData.getIntValue("aiType"));
        artwork.setManga(artwork.getTags().contains("漫画"));
        // 系列作品数据
        JSONObject seriesJson = artworkRawData.getJSONObject("seriesNavData");

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

    @Contract("_, _ -> param2")
    @SneakyThrows
    public static Artwork[] orderingArtwork(@NotNull String field, Artwork @NotNull [] artworks) {
        Method orderingColumnGetMethod = Artwork.class.getMethod("get" + field.substring(0, 1).toUpperCase() + field.substring(1));

        int i, j;
        int flag;
        for (i = artworks.length - 1; i > 0; i--) {
            flag = 0;
            for (j = 0; j < i; j++) {
                if ((int) orderingColumnGetMethod.invoke(artworks[j]) <
                                (int) orderingColumnGetMethod.invoke(artworks[j + 1])) {
                    Artwork tmp = artworks[j];
                    artworks[j] = artworks[j + 1];
                    artworks[j + 1] = tmp;
                    flag = 1;
                }
            }
            if (flag == 0)
                break;
        }

        return artworks;
    }
}

