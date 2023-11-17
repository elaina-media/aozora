package net.mikoto.aozora;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("net.mikoto.aozora.mapper")
public class AozoraApplication {

	public static void main(String[] args) {
		SpringApplication.run(AozoraApplication.class, args);
	}

}
