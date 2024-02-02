package net.mikoto.aozora.client.interceptor;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.Interceptor;
import net.mikoto.aozora.model.DynamicConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author mikoto
 * &#064;date 2024/1/7
 * Create for aozora
 */
@Component
public class CpsVersionInterceptor<T> implements Interceptor<T> {
    @Resource
    private DynamicConfig dynamicConfig;

    @Override
    public boolean beforeExecute(ForestRequest request) {
        request.addQuery("version", dynamicConfig.getCpsVersion());
        return Interceptor.super.beforeExecute(request);
    }
}
