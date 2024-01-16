package net.mikoto.aozora.model;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.util.Properties;

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
