package net.mikoto.aozora;

import com.dtflys.forest.springboot.annotation.ForestScan;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import net.mikoto.aozora.interceptor.ArtworkImageFilter;
import net.mikoto.aozora.interceptor.CorsFilter;
import org.jetbrains.annotations.NotNull;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@SpringBootApplication
@MapperScan("net.mikoto.aozora.mapper")
@ForestScan("net.mikoto.aozora.client")
@Configuration
@EnableCaching
public class AozoraApplication implements WebMvcConfigurer {
	@Resource
	private ArtworkImageFilter artworkImageFilter;
	@Resource
	private CorsFilter corsFilter;

	@Override
	public void addInterceptors(@NotNull InterceptorRegistry registry) {
		registry.addInterceptor(artworkImageFilter)
				.addPathPatterns("/pixiv/artwork/getImage/**");

		registry.addInterceptor(corsFilter)
				.addPathPatterns("/**");
	}

	public static void main(String[] args) {
		SpringApplication.run(AozoraApplication.class, args);

		// MyBatis-Flex sql audit enable
		AuditManager.setAuditEnable(true);
		AuditManager.setMessageCollector(new ConsoleMessageCollector());
	}
}
