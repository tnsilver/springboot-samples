package com.tnsilver.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AppConfig implements WebMvcConfigurer {

	/**
	 * allows html templates to refer to "/static/**" as "/resources/**" in links.
	 *
	 * for example:
	 *
	 * <link rel="stylesheet" href="../static/css/custom.css" th:href=
	 * "@{/resources/css/custom.css}" />
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**")
				.addResourceLocations("classpath:static/")
				.setCachePeriod(3600);
	}

	/**
	 * adds automatic controllers for corresponding templates, so that redirecting
	 * to index.html, does not require an entire stand alone controller class.
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("simple");
		registry.addViewController("/medium").setViewName("medium");
		registry.addViewController("/complex").setViewName("complex");
	}

	/**
	 * defines a rest template bean.
	 *
	 * @return the default spring boot rest template object.
	 */
	/*
	 * @Bean RestTemplate restTemplate() { return new RestTemplate(); }
	 */

	@Bean
	RestTemplate restTemplate(ProtobufHttpMessageConverter hmc) {
		return new RestTemplate(Arrays.asList(hmc, new MappingJackson2HttpMessageConverter()));
	}

	/*
	 * @Bean RestTemplate restTemplate() { return new RestTemplateBuilder()
	 * .messageConverters(new MappingJackson2HttpMessageConverter(objectMapper()),
	 * new ProtobufHttpMessageConverter()) .build(); }
	 */

	/**
	 * Security configurations basically disables all spring security automatic
	 * mechanisms. This is done so that 'curl' commands can be issued to test the
	 * application's REST API endpoints.
	 *
	 * @param http the HttpSecurity builder.
	 * @return HttpSecurity configurations
	 * @throws Exception if anything goes wrong
	 */
	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// @formatter:off
		http.httpBasic().disable() //no Http basic login
		    .cors().disable()      //no CORS
		    .csrf().disable()	   //no CSRF _csrf meta content
		    .formLogin().disable() //no Form Login
		    .logout().disable()    //no logout
		    //.headers(headers -> headers.cacheControl(cache -> cache.disable()))
		    //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
		    //.and()
		    	.authorizeHttpRequests()
		    		.antMatchers("/resources/**", "/themes/**", "/error/**").permitAll()
		    /**
		     * allow firefox web developer to display responsive layouts
		     * by disabling header X-Frame-Options and configuring header
		     * Content-Security-Policy to allow for localhost
		     */
		    /*
			.and().headers().frameOptions().disable()
			.and().headers().contentSecurityPolicy("http://localhost*")
			*/
			;
		// @formatter:on
		return http.build();
	}

	@Bean
	ProtobufHttpMessageConverter protobufHttpMessageConverter() {
		return new ProtobufHttpMessageConverter();
	}
}
