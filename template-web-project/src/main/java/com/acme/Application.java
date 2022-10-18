package com.acme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	/**
	 * The main entry point into this spring boot application
	 * @param args the array of string arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
