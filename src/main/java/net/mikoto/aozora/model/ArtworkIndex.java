package net.mikoto.aozora.model;

import cn.hutool.core.util.HashUtil;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

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
