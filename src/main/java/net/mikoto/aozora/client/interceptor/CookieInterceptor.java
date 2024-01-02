package net.mikoto.aozora.client.interceptor;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.Interceptor;
import lombok.SneakyThrows;
import net.mikoto.aozora.model.AozoraConfig;
import net.mikoto.aozora.model.DynamicConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.yaml.snakeyaml.Yaml;

import java.util.Properties;

/**
 * @author mikoto
 * &#064;date 2024/1/2
 * Create for aozora
 */
public class CookieInterceptor<T> implements Interceptor<T> {
    @Value("nacos.config.server-addr")
    String serverAddr;
    @Value("nacos.config.data-id")
    String dataId;
    @Value("nacos.config.group")
    String group;
    String[] cookies;
    int sessionIdCount = 0;

    @SneakyThrows
    public CookieInterceptor() {
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        ConfigService configService = NacosFactory.createConfigService(properties);
        String content = configService.getConfig(dataId, group, 5000);
        Yaml yaml = new Yaml();
        DynamicConfig dynamicConfig = yaml.load(content);
        cookies = dynamicConfig.getCookies();
    }
    @Override
    public boolean beforeExecute(ForestRequest request) {
        request.addHeader("Cookie", "PHPSESSID=" + cookies[sessionIdCount]);
        nextSessionId();
        return true;
    }

    private void nextSessionId() {
        sessionIdCount++;
        if (sessionIdCount >= cookies.length) {
            sessionIdCount = 0;
        }
    }
}
