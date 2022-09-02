package com.cc.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource({ "classpath:/application.properties", "classpath:/rest.properties", "classpath:/cache.properties", "classpath:admin-key.properties" })
public class CryptoCompareProxyApp extends SpringBootServletInitializer {

	/**
	 * The main entry point into this spring boot application
	 * @param args the array of string arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(CryptoCompareProxyApp.class, args);
	}

}
