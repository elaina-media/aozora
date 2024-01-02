package net.mikoto.aozora.model.index;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author mikoto
 * &#064;date 2023/12/30
 * Create for aozora
 */
@Data
public class TagIndex {
    @TableId
    private Long key;
    private String tagName;
}
