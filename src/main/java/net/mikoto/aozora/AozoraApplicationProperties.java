package net.mikoto.aozora;

import net.mikoto.aozora.model.DynamicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mikoto
 * &#064;date 2024/3/28
 * Create for aozora
 */
@Configuration
public class AozoraApplicationProperties {
    public static final DynamicConfig dynamicConfig = new DynamicConfig();

    @Bean
    public DynamicConfig getDynamicConfig() {
        return dynamicConfig;
    }
}
