package com.cc.proxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
		registry.addResourceHandler("/resources/**").addResourceLocations("classpath:static/");
	}

	/**
	 * adds automatic controllers for corresponding templates, so that redirecting
	 * to index.html, does not require an entire stand alone controller class.
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index");
		registry.addViewController("/about").setViewName("about");
		registry.addViewController("/contact").setViewName("contact");
	}

	/**
	 * defines a rest template bean.
	 *
	 * @return the default spring boot rest template object.
	 */
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

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
		    .sessionManagement()
		    	.sessionCreationPolicy(SessionCreationPolicy.STATELESS) //no session
		    .and()
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
}
