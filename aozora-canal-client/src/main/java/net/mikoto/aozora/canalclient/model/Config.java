package net.mikoto.aozora.canalclient.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author mikoto
 * &#064;date 2024/3/23
 * Create for aozora
 */
@Configuration
@ConfigurationProperties(prefix = "aozora.canal")
@Data
public class Config {
    String host;
    int port;
    String database;
    String destinations;
    String username = "";
    String password = "";
}
