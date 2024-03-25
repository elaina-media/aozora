package net.mikoto.aozora.crawler;

import com.dtflys.forest.springboot.annotation.ForestScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ForestScan("net.mikoto.aozora.crawler.client")
@MapperScan("net.mikoto.aozora.crawler.mapper")
public class AozoraCrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AozoraCrawlerApplication.class, args);
    }

}
