package net.mikoto.aozora;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.dtflys.forest.Forest;
import com.dtflys.forest.config.ForestConfiguration;
import net.mikoto.aozora.client.PixivClient;
import net.mikoto.aozora.model.DynamicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mikoto
 * &#064;date 2023/11/16
 * Create for aozora
 */
@Configuration
public class AozoraApplicationProperties {
    public static DynamicConfig dynamicConfig = new DynamicConfig();
    @Bean
    public PixivClient getPixivClient() {
        ForestConfiguration configuration = Forest.config();
        configuration.setLogEnabled(false);
        return Forest.client(PixivClient.class);
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    public DynamicConfig dynamicConfig() {
        return dynamicConfig;
    }
}
