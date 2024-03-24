package net.mikoto.aozora.canalclient;

import com.dtflys.forest.springboot.annotation.ForestScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ForestScan("net.mikoto.aozora.canalclient.client")
public class AozoraCanalClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(AozoraCanalClientApplication.class, args);
    }

}
