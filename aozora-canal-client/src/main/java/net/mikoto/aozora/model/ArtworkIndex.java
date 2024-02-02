package net.mikoto.aozora.model;

import cn.hutool.core.util.HashUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.crypto.SecureUtil;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * @author mikoto
 * &#064;date 2023/12/30
 * Create for aozora
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "artwork_index")
public class ArtworkIndex {
    @Id
    private int artworkIndexId;
    private Integer artworkId;
    private Artwork.Grading grading;
    private int bookmarkCount;
    private int likeCount;
    private int viewCount;

    public void setKey(@NotNull String key) {
        this.artworkIndexId = HashUtil.bkdrHash(key);
    }
}
