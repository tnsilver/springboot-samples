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

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import lombok.extern.slf4j.Slf4j;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
@Slf4j
public class SecurityConfig {

	// @formatter:off
	@Value("${spring.security.user.name}") String username;
	@Value("${spring.security.user.password}") String password;
	@Value("#{'${spring.profiles.active}'.split(',')}") private List<String> profiles;
	// @formatter:on

	/*
	 * @Bean protected WebSecurityCustomizer webSecurityCustomizer() { return (web)
	 * -> web.ignoring().antMatchers("/h2-console/**", "/resources/**",
	 * "/themes/**", "/error/**", "/html/error"); }
	 */

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// @formatter:off
		final String[] PERMITTED_PATTERNS = { "/resources/favicon.ico", "/h2-console/**", "/resources/**", "/themes/**", "/error**", "/html/error**" };
		final String DEFAULT_URL = "/html/contacts-bootstrap", LOGIN_URL = "/login-bootstrap";
		boolean test = profiles.contains("test"), dev = profiles.contains("dev");
        if (test || dev) {
            String profile = dev ? "dev" : (test ? "test" : profiles.toString());
            log.debug("disabling csrf for profile: {}", profile);
            http
                .csrf().disable()
                    .authorizeRequests()
                        .antMatchers("/**").permitAll()
                    .and()
                        .headers()
                            .frameOptions().disable()
                    .and()
                        .httpBasic(); // basic auth for curl tests
        }
        http.cors().disable()
            .authorizeRequests()
                .antMatchers(PERMITTED_PATTERNS).permitAll()
                .antMatchers("/html/contact/**", "/jsp/contact/**").hasAnyRole("USER")
                .antMatchers(HttpMethod.PATCH, "/api/contacts*", "/api/contacts/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/api/contacts*", "/api/contacts/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/contacts*", "/api/contacts/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/contacts*", "/api/contacts/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/login*").permitAll()
            .and()
                .formLogin() // for ajax sends 204-NO_CONTENT when login is OK and 401-UNAUTHORIZED when login fails, else redirect to success url
                	.successHandler((req, res, auth) -> { if (isAjax(req)) res.setStatus(SC_NO_CONTENT); else res.sendRedirect(DEFAULT_URL); })
                	.failureHandler((req, res, ex) -> { if (isAjax(req)) res.setStatus(SC_NO_CONTENT); else res.sendError(SC_FORBIDDEN); })
                .loginPage("/login-bootstrap").permitAll()
                .loginProcessingUrl("/signin")
            .and()
                .exceptionHandling() // for ajax 401-UNAUTHORIZED when anonymous user attempts to access protected URLs, else redirect to login
                	.authenticationEntryPoint((req, res, ex) -> { if (isAjax(req)) res.setStatus(SC_UNAUTHORIZED); else res.sendRedirect(LOGIN_URL); })
            .and()
                .logout() // standard logout that sends 204-NO_CONTENT when logout is OK
	                .logoutSuccessHandler((req, res, auth) -> { if (isAjax(req)) res.setStatus(SC_NO_CONTENT); else res.sendRedirect(DEFAULT_URL); })
                .logoutSuccessUrl("/html/contacts-bootstrap")
                .logoutUrl("/logout")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
             .and()
                .requestCache()
                .requestCache(new HttpSessionRequestCache());
        // @formatter:off
        return http.build();
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	@Bean
	protected InMemoryUserDetailsManager userDetailsService() {
		return new InMemoryUserDetailsManager(
				User.builder().password(passwordEncoder().encode(password)).username(username).roles("USER","ADMIN").build());
	}

	private boolean isAjax(HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}

}
