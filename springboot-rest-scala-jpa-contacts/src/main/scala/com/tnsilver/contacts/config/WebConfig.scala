package com.tnsilver.contacts.config

import java.util
import java.util.Locale
import java.util.Optional
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.HttpStatus
import org.springframework.ui.context.support.ResourceBundleThemeSource
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import org.springframework.web.servlet.theme.CookieThemeResolver
import org.springframework.web.servlet.theme.ThemeChangeInterceptor
import org.springframework.web.servlet.view.InternalResourceViewResolver
import org.springframework.web.servlet.view.JstlView

import javax.servlet.http.HttpServletRequest

/*
 * File: WebConfig.java
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


@Configuration class WebConfig extends WebMvcConfigurer {

  override def addResourceHandlers(registry: ResourceHandlerRegistry): Unit = {
    registry.addResourceHandler("/").addResourceLocations("html/home")
    registry.addResourceHandler("/resources/**").addResourceLocations("classpath:static/")
  }

  override def addInterceptors(registry: InterceptorRegistry): Unit = {
    registry.addInterceptor(localeChangeInterceptor)
    registry.addInterceptor(themeChangeInterceptor)
  }

  override def addViewControllers(registry: ViewControllerRegistry): Unit = {
    registry.addViewController("/login").setViewName("html/login")
    registry.addRedirectViewController("/", "/html/home")
    registry.addViewController("/html/hello").setViewName("html/hello")
    registry.addViewController("/jsp/hello").setViewName("jsp/hello")
    registry.addViewController("/html/home").setViewName("html/home")
    registry.addViewController("/html/contact").setViewName("html/contact.html")
    registry.addViewController("/html/contacts").setViewName("html/contacts.html")
    registry.addViewController("/jsp/contact").setViewName("jsp/contact")
    registry.addViewController("/jsp/contacts").setViewName("jsp/contacts")
    registry.addViewController("/h2-console").setViewName("h2-console")
  }

  override def addArgumentResolvers(argumentResolvers: util.List[HandlerMethodArgumentResolver]): Unit = {
    val resolver = new PageableHandlerMethodArgumentResolver
    resolver.setFallbackPageable(PageRequest.of(0, 10, Sort.by(Direction.ASC, "id")))
    argumentResolvers.add(resolver)
  }

  @Bean def jspViewResolver: InternalResourceViewResolver = {
    val resolver = new InternalResourceViewResolver
    resolver.setViewClass(classOf[JstlView])
    resolver.setPrefix("/WEB-INF/views/")
    resolver.setViewNames("jsp/*")
    resolver.setSuffix(".jsp")
    resolver.setExposedContextBeanNames("sortHelper")
    resolver.setOrder(200)
    resolver
  }

  @Bean def themeSource: ResourceBundleThemeSource = {
    val themeSource = new ResourceBundleThemeSource
    themeSource.setDefaultEncoding("UTF-8")
    themeSource.setBasenamePrefix("themes.")
    themeSource
  }

  @Bean def themeResolver: CookieThemeResolver = {
    val resolver = new CookieThemeResolver
    resolver.setDefaultThemeName("wheat")
    resolver.setCookieName("mvc-theme-cookie")
    resolver.setCookiePath("/")
    resolver
  }

  @Bean def themeChangeInterceptor: ThemeChangeInterceptor = {
    val interceptor = new ThemeChangeInterceptor
    interceptor.setParamName("theme")
    interceptor
  }

  @Bean def localeResolver: LocaleResolver = {
    val localeResolver = new SessionLocaleResolver
    localeResolver.setDefaultLocale(new Locale("en"))
    localeResolver
  }

  @Bean def localeChangeInterceptor: LocaleChangeInterceptor = {
    val localeChangeInterceptor = new LocaleChangeInterceptor
    localeChangeInterceptor.setParamName("lang")
    localeChangeInterceptor
  }

  @Bean def errorViewResolver: ErrorViewResolver = (request: HttpServletRequest, status: HttpStatus, model: util.Map[String, AnyRef]) => {
    def foo(request: HttpServletRequest, status: HttpStatus, model: util.Map[String, AnyRef]) = {
      val modelMap = new util.HashMap[String, AnyRef](model)
      Optional.ofNullable(request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE)).ifPresent((ex: AnyRef) => modelMap.put("exception", ex))
      new ModelAndView("html/error", modelMap)
    }
    foo(request, status, model)
  }
}
