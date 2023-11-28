package net.mikoto.aozora.model;

import lombok.Data;
import lombok.Getter;

import java.util.Date;

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
public class Artwork {
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
}

