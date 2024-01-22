package net.mikoto.aozora.model;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("`artwork_index`")
public class ArtworkIndex {
    @TableId
    private Long artworkIndexId;
    private Integer artworkId;
    private Artwork.Grading grading;
    private int bookmarkCount;
    private int likeCount;
    private int viewCount;

    public void setKey(@NotNull String key) {
        StringBuilder sbu = new StringBuilder();
        char[] chars = SecureUtil.md5(key).toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(i != chars.length - 1)
            {
                sbu.append((int)chars[i]);
            }
            else {
                sbu.append((int)chars[i]);
            }
        }
        if (sbu.length() > 18) {
            String divNum = 1 + "0".repeat(Math.max(0, sbu.length() - 18));
            BigDecimal result = NumberUtil.div(sbu.toString(), divNum);
            this.artworkIndexId = result.toBigInteger().longValue();
            return;
        }
        this.artworkIndexId = Long.valueOf(sbu.toString());
    }
}
