package com.cc.proxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Minimal SpringSecurity configurations
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// @formatter:off
		http.httpBasic().disable() //no Http basic login
		    .csrf().disable()	   //no CSRF token
		    .formLogin().disable() //no Form Login
		    .logout().disable()    //no logout
		    .sessionManagement()
		    	.sessionCreationPolicy(SessionCreationPolicy.STATELESS) //no session
		    .and()
		    	.authorizeHttpRequests()
		    		.antMatchers("/resources/**", "/themes/**", "/error/**").permitAll();
		// @formatter:on
		return http.build();
	}

}
