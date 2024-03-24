package net.mikoto.aozora.canalclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author mikoto
 * &#064;date 2023/12/30
 * Create for aozora
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkIndex {
    String tags;
    String artworkTitle;
    String authorName;
    long authorId;
    long seriesId;
    int grading;
    int bookmarkCount;
    int likeCount;
    int viewCount;
    boolean isAi;
    boolean isManga;
    long artworkId;
}
