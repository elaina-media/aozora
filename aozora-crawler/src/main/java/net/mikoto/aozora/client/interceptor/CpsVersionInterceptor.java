package net.mikoto.aozora.client.interceptor;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.Interceptor;
import net.mikoto.aozora.crawler.AozoraCrawlerApplicationProperties;

/**
 * @author mikoto
 * &#064;date 2024/1/7
 * Create for aozora
 *
 * todo: 与AozoraApplicationProperties的强耦合
 */
public class CpsVersionInterceptor<T> implements Interceptor<T> {
    @Override
    public boolean beforeExecute(ForestRequest request) {
        request.addQuery("version", AozoraCrawlerApplicationProperties.dynamicConfig.getCpsVersion());
        return Interceptor.super.beforeExecute(request);
    }
}
