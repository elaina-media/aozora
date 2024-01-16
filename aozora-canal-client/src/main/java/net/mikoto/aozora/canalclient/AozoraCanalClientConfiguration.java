package net.mikoto.aozora.canalclient;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

/**
 * @author mikoto
 * &#064;date 2024/1/14
 * Create for aozora
 */
@Configuration
public class AozoraCanalClientConfiguration {
    @Bean
    public CanalConnector canalConnector() {
        return CanalConnectors.newSingleConnector(
                new InetSocketAddress("192.168.123.90", 11111),
                "aozora", "", ""
        );
    }
}
