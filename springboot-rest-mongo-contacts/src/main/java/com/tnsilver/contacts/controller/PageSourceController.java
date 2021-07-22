/*
 * File: PageSourceController.java
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
package com.tnsilver.contacts.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Class PageSourceController is a controller for displaying the source code of a given file. Currently it supports html
 * or jsp files that are in known locations in this application. HTML templates reside under {@code WEB-INF/views/html}
 * directory and JSP files are under {@code WEB-INF/views/jsp}.
 *
 * @author T.N.Silverman
 */
@Controller
public class PageSourceController {

    public static final String HTML_VIEWS_DIR = "classpath:/templates/html/";
    public static final String JSP_VIEWS_DIR = "WEB-INF/views/jsp/";

    private static final Logger logger = LoggerFactory.getLogger(PageSourceController.class);
    @Autowired
    private ServletContext servletContext;
    @Autowired
    ResourceLoader resourceLoader;

    @GetMapping("/source/{resource:.+}")
    @ResponseBody
    public String showSource(@PathVariable(name = "resource", required = true) String resource, HttpServletResponse response)
        throws Exception {
        Resource internalResource = getResource(resource);
        StringBuilder sb = new StringBuilder();
        try {
            String path = internalResource.getURI().getPath();
            logger.debug("attempting to read resource: '{}' from path '{}'...", resource, path);
            try (InputStream in = internalResource.getInputStream()) {

                logger.debug("showing source for: {}", path);
                sb.append("<body>").append("\n");
                // create a 'go back' back href link
                String href = createLink(resource);
                sb.append("<a href=\"").append(href).append("\">").append("<<&nbsp;go back").append("</a>").append("\n");
                sb.append("<pre>").append("\n");
                for (int ch = in.read(); ch != -1; ch = in.read()) {
                    if (ch == '<') {
                        sb.append("&lt;");
                    } else {
                        sb.append((char) ch);
                    }
                }
                sb.append("</pre>").append("\n");
                sb.append("</body>").append("\n");
            }
        } catch (IOException ioex) {
            sb.append(formatErrorPage(ioex.getMessage()));
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
        return sb.toString();
    }

    /**
     * Create a {@code go back} link from the given {@code fileName}
     *
     * @param resource the name of the file (view) passed to the handler method
     * @return a link to this view
     */
    private String createLink(String resource) {
        String extension = FilenameUtils.getExtension(resource);
        String view = FilenameUtils.getBaseName(resource);
        return servletContext.getContextPath() + (extension.equalsIgnoreCase("html") ? "/html/" : "/jsp/") + view;
    }

    /**
     * return the relative web-path of this view based on the given {@code fileName}
     *
     * @param resource the name of the file (view) passed to the handler method
     * @return
     */
    private Resource getResource(String resource) {
        String extension = FilenameUtils.getExtension(resource);
        String file = extension.isEmpty() ? (resource + ".jsp") : resource;
        String location = extension.equalsIgnoreCase("html") ? HTML_VIEWS_DIR + file : JSP_VIEWS_DIR + file;
        logger.debug("loading resource '{}' from '{}'", resource, location);
        return resourceLoader.getResource(location);
    }

    /**
     * Creates an HTML formatted output string reporting the {@code error}
     *
     * @param error the error message
     */
    private String formatErrorPage(String error) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>").append("\n");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">").append("\n");
        sb.append("<head>").append("\n");
        sb.append("\t").append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">").append("\n");
        sb.append("\t").append("<title>").append("Error").append("</title>").append("\n");
        sb.append("</head>").append("\n");
        sb.append("<body>").append("\n");
        sb.append("<h4 style=\"color: red;\">").append(error).append("</h4>").append("\n");
        sb.append("</body>").append("\n");
        sb.append("</html>").append("\n");
        return sb.toString();
    }
}
