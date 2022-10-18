/*
 * File: PageSourceController.scala
 * Creation Date: June 25, 2021
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
package com.tnsilver.contacts.controller

import org.apache.commons.io.FilenameUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.{Resource, ResourceLoader}
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, PathVariable, ResponseBody}

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletResponse
import scala.io.{Codec, Source}
import scala.jdk.CollectionConverters._

/**
 * Class PageSourceController is a controller for displaying the source code of a given file. Currently it supports html
 * or jsp files that are in known locations in this application. HTML templates reside under { @code WEB-INF/views/html}
 * directory and JSP files are under { @code WEB-INF/views/jsp}.
 *
 * @author T.N.Silverman
 */
@Controller
class PageSourceController {

  final val HTML_VIEWS_DIR = "classpath:/templates/html/"
  final val JSP_VIEWS_DIR = "WEB-INF/views/jsp/"

  private val logger = LoggerFactory.getLogger(classOf[PageSourceController])
  @Autowired
  var servletContext: ServletContext = _

  @Autowired
  var resourceLoader: ResourceLoader = _

  @GetMapping(Array("/source/{resource:.+}"))
  @ResponseBody
  @throws(classOf[Exception])
  def showSource(@PathVariable(name = "resource", required = true) resource: String, response: HttpServletResponse): String = {
    val internalResource: Resource = getResource(resource)
    var path: String = null;
    try {
      path = internalResource.getURI.getPath
    } catch {
      case ex: Exception =>
        val error = String.format("Cannot display page source: page '%s' was not found!", path)
        logger.error(error)
        response.setStatus(HttpStatus.NOT_FOUND.value)
        return formatErrorPage(error);
    }
    logger.debug("attempting to read resource: '" + resource + "' from path '" + path + "'...")
    val sb = new StringBuilder()
    try {
      logger.debug("showing source for: {}", path)
      sb.append("<body>").append("\n")
      // create a 'go back' back href link
      val href = createLink(resource)
      sb.append("<a href=\"").append(href).append("\">").append("<<&nbsp;go back").append("</a>").append("\n")
      sb.append("<pre>").append("\n")
      val in = internalResource.getInputStream
      var bufferedSource: Unit = Source.fromInputStream(in)(Codec.UTF8).getLines().asJava.forEachRemaining(line => {
        val out = line.replace("<", "&lt;")
        sb.append(out).append("<br>")
      })
      sb.append("</pre>").append("\n")
      sb.append("</body>").append("\n")
      try {
        if (in != null) {
          logger.info("closing stream to {}", path)
          in.close()
        }
      } catch {
        case ignore: Exception => logger.info("cannot close stream: {}", ignore)
      }
    } catch {
      case ex: Exception =>
        val error = String.format("Cannot display page source: page '%s' was not found!", path)
        logger.error(error)
        sb.append(formatErrorPage(error))
        response.setStatus(HttpStatus.NOT_FOUND.value)
    }
    sb.toString()
  }

  /**
   * Create a { @code go back} link from the given { @code fileName}
   *
   * @param resource the name of the file (view) passed to the handler method
   * @return a link to this view
   */
  def createLink(resource: String): String = {
    val extension = FilenameUtils.getExtension(resource)
    val view = FilenameUtils.getBaseName(resource)
    servletContext.getContextPath + (if (extension.equalsIgnoreCase("html")) "/html/" else "/jsp/") + view
  }

  /**
   * return the relative web-path of this view based on the given { @code fileName}
   *
   * @param resource the name of the file (view) passed to the handler method
   * @return
   */
  def getResource(resource: String): Resource = {
    val extension = FilenameUtils.getExtension(resource)
    val file = if (extension.isEmpty) resource + ".jsp" else resource
    val location = if (extension.equalsIgnoreCase("html")) HTML_VIEWS_DIR + file else JSP_VIEWS_DIR + file
    logger.info("loading resource '" + resource + "' from '" + location + "'")
    resourceLoader.getResource(location)
  }

  /**
   * Creates an HTML formatted output string reporting the { @code error}
   *
   * @param error the error message
   */
  def formatErrorPage(error: String): String = {
    val sb = new StringBuilder()
    sb.append("<!DOCTYPE html>").append("\n")
    sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">").append("\n")
    sb.append("<head>").append("\n")
    sb.append("\t").append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">").append("\n")
    sb.append("\t").append("<title>").append("Error").append("</title>").append("\n")
    sb.append("</head>").append("\n")
    sb.append("<body>").append("\n")
    sb.append("<h4 style=\"color: red;\">").append(error).append("</h4>").append("\n")
    sb.append("</body>").append("\n")
    sb.append("</html>").append("\n")
    sb.toString()
  }
}
