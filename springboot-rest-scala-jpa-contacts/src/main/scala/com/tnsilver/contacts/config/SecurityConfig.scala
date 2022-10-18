package com.tnsilver.contacts.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.{HttpSecurity, WebSecurity}
import org.springframework.security.config.annotation.web.configuration.{EnableWebSecurity, WebSecurityConfigurerAdapter}
import org.springframework.security.web.savedrequest.HttpSessionRequestCache

/*
 * File: SecurityConfig.java
 * Creation Date: Jun 19, 2021
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
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true) class SecurityConfig extends WebSecurityConfigurerAdapter {
  private val logger = LoggerFactory.getLogger(classOf[SecurityConfig])
  @Value("${spring.security.user.name}") private[config] var user : String = null
  @Value("${spring.security.user.password}") private[config] var password : String = null
  @Value("#{'${spring.profiles.active}'.split(',')}") private var profiles : Array[String] = null

  @throws[Exception]
  override def configure(web: WebSecurity): Unit = {
    web.ignoring.antMatchers("/h2-console/**", "/resources/**", "/themes/**", "/error/**", "/html/error")
  }

  @throws[Exception]
  override protected def configure(http: HttpSecurity): Unit = {
    val test = profiles.toList.contains("test")
    val dev = profiles.toList.contains("dev")
    // @formatter:off
    if (test || dev) {
        logger.info("DISABLING csrf for profile: {}", profiles)
        http.csrf().disable()
                .authorizeRequests().antMatchers("/h2-console/**").permitAll()
            .and()
                .headers().frameOptions().disable()
            .and()
                .httpBasic()
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
	        .loginPage("/login")
	        .loginProcessingUrl("/signin")
	        .defaultSuccessUrl("/html/contacts")
	        .failureUrl("/login.html?error=true")
	    .and()
	        .exceptionHandling()
	            .accessDeniedPage("/html/login")
	    .and()
	        .logout()
	        .logoutSuccessUrl("/html/contacts")
	        .logoutUrl("/logout")
	        .clearAuthentication(true)
	        .invalidateHttpSession(true)
	        .deleteCookies("JSESSIONID")
	        .permitAll()
	    .and()
	        .requestCache()
	        .requestCache(new HttpSessionRequestCache())
  }
}
