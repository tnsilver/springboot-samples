/*
 * File: SecurityConfig.java
 * Creation Date: Jul 20, 2021
 *
 * Copyright (c) 2021 T.N.Silverman - all rights reserved
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses  this file to you under the Apache License, Version
 * 2.0 (the "License");  you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tnsilver.contacts.config;

import static org.apache.hc.core5.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.hc.core5.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.hc.core5.http.HttpStatus.SC_UNAUTHORIZED;
import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, jsr250Enabled = true, securedEnabled = true)
@Order(SecurityProperties.BASIC_AUTH_ORDER)
@Slf4j
public class SecurityConfig {

	@Value("${spring.security.user.name}") String username;
	@Value("${spring.security.user.password}") String password;
	@Value("#{'${spring.profiles.active}'.split(',')}") private List<String> profiles;

	public static String[] UNSECURED_RESOURCES = { "/login", "/webjars/**", "/resources/**", "/favicon.ico", "/h2-console", "/h2-console/**", "/themes/**"};

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		boolean test = profiles.contains("test"), dev = profiles.contains("dev");
		if (test || dev) {
            http.csrf()
            		.ignoringRequestMatchers(toH2Console())
            	.and()
            		.authorizeHttpRequests().requestMatchers(toH2Console()).permitAll()
            	.and()
            		.headers()
            			.frameOptions()
            				.sameOrigin();
		}
        http.headers()
        			.cacheControl().disable()
          		.and()
          			.authorizeHttpRequests().requestMatchers(UNSECURED_RESOURCES).permitAll()
          		.and()
          			.requestCache().requestCache(new HttpSessionRequestCache())
          		.and()
          			.authorizeHttpRequests()
          				.requestMatchers("/html/contact/**", "/jsp/contact/**").hasAnyRole("USER")
                        .requestMatchers(HttpMethod.PATCH, "/api/contacts*", "/api/contacts/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/contacts*", "/api/contacts/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/contacts*", "/api/contacts/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/contacts*", "/api/contacts/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/**").permitAll()
                .and()
                	.formLogin()
                		.successHandler((req, res, auth) -> { if (isAjax(req)) res.setStatus(SC_NO_CONTENT); else res.sendRedirect("/html/contacts-bootstrap"); })
                		.failureHandler((req, res, ex) -> { if (isAjax(req)) res.setStatus(SC_NO_CONTENT); else res.sendError(SC_FORBIDDEN); })
                	.loginPage("/login-bootstrap").permitAll()
                	.loginProcessingUrl("/signin")
                .and()
                	.exceptionHandling()
                		.authenticationEntryPoint((req, res, ex) -> {
                					log.error("ERROR: {}",ex.getMessage());
                					if (isAjax(req))
                						res.setStatus(SC_UNAUTHORIZED);
                					else
                						res.sendRedirect("/login-bootstrap"); })
                .and()
                	.logout()
                		.logoutSuccessHandler((req, res, auth) -> { if (isAjax(req)) res.setStatus(SC_NO_CONTENT); else res.sendRedirect("/html/contacts-bootstrap"); })
                		.logoutSuccessUrl("/html/contacts-bootstrap")
                		.logoutUrl("/logout")
                		.clearAuthentication(true)
                		.invalidateHttpSession(true)
                		.deleteCookies("JSESSIONID")
                		.permitAll()
                .and()
                	.sessionManagement().invalidSessionUrl("/").maximumSessions(1).expiredUrl("/")
                .and()
                	.sessionFixation().migrateSession().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
        return http.build();
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	@Bean
	protected InMemoryUserDetailsManager userDetailsService() {
		return new InMemoryUserDetailsManager(
				User.builder().password(passwordEncoder()
						.encode(password))
					.username(username)
					.roles("USER","ADMIN")
					.build());
	}

	private boolean isAjax(HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}
}
