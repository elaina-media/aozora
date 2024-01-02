package net.mikoto.aozora.model.index;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import net.mikoto.aozora.model.Artwork;

/**
 * @author mikoto
 * &#064;date 2023/12/30
 * Create for aozora
 */
@Data
public class ArtworkIndex {
    @TableId
    private Long key;
    private Integer artworkId;
    private int viewCount;
    private int likeCount;
    private int bookmarkCount;
    private Artwork.Grading grading;
}
