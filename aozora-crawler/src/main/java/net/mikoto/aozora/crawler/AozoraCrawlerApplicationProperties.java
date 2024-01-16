package net.mikoto.aozora.crawler;

import net.mikoto.aozora.crawler.model.DynamicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mikoto
 * &#064;date 2024/1/6
 * Create for aozora
 */
@Configuration
public class AozoraCrawlerApplicationProperties {
    public static final DynamicConfig dynamicConfig = new DynamicConfig();

    @Bean
    public DynamicConfig dynamicConfig() {
        return dynamicConfig;
    }
}
