package com.a503.churros;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ChurrosApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChurrosApplication.class, args);
	}

}
