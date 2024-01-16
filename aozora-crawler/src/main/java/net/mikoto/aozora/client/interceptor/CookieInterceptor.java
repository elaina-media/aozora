package net.mikoto.aozora.client.interceptor;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.Interceptor;
import net.mikoto.aozora.crawler.AozoraCrawlerApplicationProperties;
import org.jetbrains.annotations.NotNull;

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
                "PHPSESSID=" + AozoraCrawlerApplicationProperties.dynamicConfig.getCookies()[sessionIdCount]);
        nextSessionId();
        return true;
    }

    private void nextSessionId() {
        sessionIdCount++;
        if (sessionIdCount >= AozoraCrawlerApplicationProperties.dynamicConfig.getCookies().length) {
            sessionIdCount = 0;
        }
    }
}
