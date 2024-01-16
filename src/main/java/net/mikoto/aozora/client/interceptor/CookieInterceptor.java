package net.mikoto.aozora.client.interceptor;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.Interceptor;
import lombok.SneakyThrows;
import net.mikoto.aozora.AozoraApplicationProperties;
import net.mikoto.aozora.model.AozoraConfig;
import net.mikoto.aozora.model.DynamicConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.yaml.snakeyaml.Yaml;

import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

/**
 * @author mikoto
 * &#064;date 2024/1/2
 * Create for aozora
 *
 * todo: 与AozoraApplicationProperties的强耦合
 */
public class CookieInterceptor<T> implements Interceptor<T> {
    int sessionIdCount = 0;

    @Override
    public boolean beforeExecute(@NotNull ForestRequest request) {
        request.addHeader("Cookie",
                "PHPSESSID=" + AozoraApplicationProperties.dynamicConfig.getCookies()[sessionIdCount]);
        nextSessionId();
        return true;
    }

    private void nextSessionId() {
        sessionIdCount++;
        if (sessionIdCount >= AozoraApplicationProperties.dynamicConfig.getCookies().length) {
            sessionIdCount = 0;
        }
    }
}
