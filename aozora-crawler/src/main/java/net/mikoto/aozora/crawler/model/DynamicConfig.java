package net.mikoto.aozora.crawler.model;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import lombok.Data;

/**
 * @author mikoto
 * &#064;date 2024/1/2
 * Create for aozora
 */

@Data
@NacosConfigurationProperties(
        dataId = "forward_client",
        type = ConfigType.JSON,
        autoRefreshed = true
)
public class DynamicConfig {
    String[] cookies;

    String cpsVersion;
}
