package net.mikoto.aozora.client.interceptor;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.Interceptor;
import net.mikoto.aozora.model.DynamicConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author mikoto
 * &#064;date 2024/1/2
 * Create for aozora
 */
@Component
public class CookieInterceptor<T> implements Interceptor<T> {
    int sessionIdCount = 0;
    @Resource
    private DynamicConfig dynamicConfig;

    @Override
    public boolean beforeExecute(@NotNull ForestRequest request) {
        request.addHeader("Cookie",
                "PHPSESSID=" + dynamicConfig.getCookies()[sessionIdCount]);
        nextSessionId();
        return true;
    }

    private void nextSessionId() {
        sessionIdCount++;
        if (sessionIdCount >= dynamicConfig.getCookies().length) {
            sessionIdCount = 0;
        }
    }
}
