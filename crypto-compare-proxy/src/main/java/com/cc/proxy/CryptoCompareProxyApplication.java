package com.cc.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource({ "classpath:/application.properties", "classpath:/rest.properties", "classpath:/cache.properties", "classpath:admin-key.properties" })
public class CryptoCompareProxyApplication {

	/**
	 * The main entry point into this spring boot application
	 * @param args the array of string arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(CryptoCompareProxyApplication.class, args);
	}

}
