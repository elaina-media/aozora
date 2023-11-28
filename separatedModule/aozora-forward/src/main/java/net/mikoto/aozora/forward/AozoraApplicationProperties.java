package net.mikoto.aozora.forward;

import com.dtflys.forest.Forest;
import com.dtflys.forest.config.ForestConfiguration;
import net.mikoto.aozora.client.PixivClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mikoto
 * &#064;date 2023/11/16
 * Create for aozora
 */
@Configuration
public class AozoraApplicationProperties {
    @Bean
    public PixivClient getPixivClient() {
        ForestConfiguration configuration = Forest.config();
        configuration.setLogEnabled(false);
        return Forest.client(PixivClient.class);
    }
}
