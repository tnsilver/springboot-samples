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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

	private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
	@Value("${spring.security.user.name}")
	String username;
	@Value("${spring.security.user.password}")
	String password;
	@Value("#{'${spring.profiles.active}'.split(',')}")
	private List<String> profiles;

	@Bean
	protected WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().antMatchers("/h2-console/**", "/resources/**", "/themes/**", "/error**", "/html/error**");
	}

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		boolean test = profiles.contains("test");
		boolean dev = profiles.contains("dev");
		// @formatter:off
        if (test || dev) {
            String profile = dev ? "dev" : (test ? "test" : profiles.toString());
            logger.debug("Disabling csrf for profile: {}", profile);
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
                .antMatchers( "/resources/favicon.ico").permitAll()
                .antMatchers("/html/hello/**", "/jsp/hello/**").permitAll()
                .antMatchers("/html/contact/**", "/jsp/contact/**").hasAnyRole("USER")
                .antMatchers(HttpMethod.DELETE, "/api/contacts/*").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.PATCH, "/api/contacts/*").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/contacts/*").hasAnyRole("USER", "ADMIN")
                .antMatchers("/login*").permitAll()
            .and()
                .formLogin()
                .loginPage("/login-bootstrap").permitAll()
                .loginProcessingUrl("/signin")
                .defaultSuccessUrl("/html/contacts-bootstrap")
                .failureUrl("/login-bootstrap?error=true")
            .and()
                .exceptionHandling()
                    .accessDeniedPage("/html/login-bootstrap")
            .and()
                .logout()
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

}
