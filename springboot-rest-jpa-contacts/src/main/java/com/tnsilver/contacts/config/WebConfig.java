/*
 * File: WebConfig.java
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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.ui.context.support.ResourceBundleThemeSource;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.theme.CookieThemeResolver;
import org.springframework.web.servlet.theme.ThemeChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

// do not use @EnableWebMvc !!!
@SuppressWarnings("deprecation")
@Configuration
public class WebConfig implements WebMvcConfigurer {

	/**
	 * addResourceHandler("/css/**") = how you write URL's in your app
	 * addResourceLocations("/resources/css/") = where the resources really are
	 * (relative to the root context)
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/").addResourceLocations("html/home");
		registry.addResourceHandler("/resources/**").addResourceLocations("classpath:static/");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
		registry.addInterceptor(themeChangeInterceptor());
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		// @formatter:off
        registry.addViewController("/login-bootstrap").setViewName("html/login-bootstrap");
        registry.addRedirectViewController("/", "html/home-bootstrap");
        registry.addViewController("/html/home").setViewName("html/home");
        registry.addViewController("/html/home-bootstrap").setViewName("html/home-bootstrap.html");
        registry.addViewController("/html/hello").setViewName("html/hello");
        registry.addViewController("/html/hello-bootstrap").setViewName("html/hello-bootstrap.html");
        registry.addViewController("/jsp/hello").setViewName("jsp/hello");
        registry.addViewController("/jsp/hello-bootstrap").setViewName("jsp/hello-bootstrap");
        registry.addViewController("/html/contact").setViewName("html/contact.html");
        registry.addViewController("/html/contacts").setViewName("html/contacts.html");
        registry.addViewController("/html/contacts-bootstrap").setViewName("html/contacts-bootstrap.html");
        registry.addViewController("/jsp/contact").setViewName("jsp/contact");
        registry.addViewController("/jsp/contacts").setViewName("jsp/contacts");
        registry.addViewController("/jsp/contacts-bootstrap").setViewName("jsp/contacts-bootstrap");
        registry.addViewController("/h2-console").setViewName("h2-console");
        // @formatter:on
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
		resolver.setFallbackPageable(PageRequest.of(0, 10, Sort.by(Direction.ASC, "id")));
		argumentResolvers.add(resolver);
	}

	@Bean
	InternalResourceViewResolver jspViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setViewClass(JstlView.class);
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setViewNames("jsp/*");
		resolver.setSuffix(".jsp");
		resolver.setExposedContextBeanNames("sortHelper");
		resolver.setOrder(200);
		return resolver;
	}

	@Bean
	ResourceBundleThemeSource themeSource() {
		ResourceBundleThemeSource themeSource = new ResourceBundleThemeSource();
		themeSource.setDefaultEncoding("UTF-8");
		themeSource.setBasenamePrefix("themes.");
		return themeSource;
	}

	@Bean
	CookieThemeResolver themeResolver() {
		CookieThemeResolver resolver = new CookieThemeResolver();
		resolver.setDefaultThemeName("wheat");
		resolver.setCookieName("mvc-theme-cookie");
		resolver.setCookiePath("/");
		return resolver;
	}

	@Bean
	ThemeChangeInterceptor themeChangeInterceptor() {
		ThemeChangeInterceptor interceptor = new ThemeChangeInterceptor();
		interceptor.setParamName("theme");
		return interceptor;
	}

	@Bean
	LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(Locale.forLanguageTag("en"));
		return localeResolver;
	}

	@Bean
	LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("lang");
		return localeChangeInterceptor;
	}

	@Bean
	ErrorViewResolver errorViewResolver() {
		return (request, status, model) -> {
			Map<String, Object> modelMap = new HashMap<>(model);
			Optional.ofNullable(request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE)).ifPresent(ex -> modelMap.put("exception", ex));
			return new ModelAndView("html/error-bootstrap", modelMap);
		};
	}
}
