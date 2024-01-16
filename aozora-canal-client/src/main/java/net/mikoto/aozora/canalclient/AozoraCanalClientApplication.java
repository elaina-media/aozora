package net.mikoto.aozora.canalclient;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("net.mikoto.aozora.canalclient.mapper")
public class AozoraCanalClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(AozoraCanalClientApplication.class, args);
    }

}
