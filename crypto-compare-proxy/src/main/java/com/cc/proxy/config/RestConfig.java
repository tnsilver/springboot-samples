package com.cc.proxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * This configuration class initializes a {@link RestTemplate} bean.
 */
@Configuration
public class RestConfig {

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
