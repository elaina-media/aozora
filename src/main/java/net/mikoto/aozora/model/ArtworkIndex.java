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
@Table("artwork_index")
public class ArtworkIndex {
    @Id
    private Integer artworkIndexId;
    private int artworkId;
    private Artwork.Grading grading;
    private int bookmarkCount;
    private int likeCount;
    private int viewCount;

    public void setKey(@NotNull String key) {
        this.artworkIndexId = getId(key);
    }

    public static int getId(@NotNull String key) {
         return HashUtil.bkdrHash(key);
    }
}
