package net.mikoto.aozora.canalclient;

import net.mikoto.aozora.canalclient.client.ElasticSearchClient;
import net.mikoto.aozora.canalclient.client.support.BeginningSupport;
import net.mikoto.aozora.canalclient.client.support.ElasticSearchSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author mikoto
 * &#064;date 2024/1/14
 * Create for aozora
 */
@Configuration
public class AozoraCanalClientConfiguration {
    @Bean
    public ElasticSearchSupport elasticSearchSupport(ElasticSearchClient elasticSearchClient) {
        return new ElasticSearchSupport(null, elasticSearchClient);
    }

    @Bean
    public BeginningSupport beginningSupport(ElasticSearchSupport elasticSearchSupport) {
        return new BeginningSupport(elasticSearchSupport);
    }
}
