package com.cc.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource({ "classpath:/application.properties", "classpath:/rest.properties", "classpath:/cache.properties", "classpath:admin-key.properties" })
public class CryptoCompareApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoCompareApplication.class, args);
	}

}
