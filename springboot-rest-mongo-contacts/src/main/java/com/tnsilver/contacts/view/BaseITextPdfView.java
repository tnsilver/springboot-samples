/*
 * File: BaseITextPdfView.java
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
package com.tnsilver.contacts.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Class BaseITextPdfView - Support for iText PDF document views.
 * <p>
 * The new com.itextpdf:itextpdf library is not supported by default. We can resolve this by first creating an abstract
 * class and extending from the AbstractView. In this class we set the correct content type for our document.
 * <p>
 * Next, we create an abstract method that’ll be used for creating the PDF document.
 * <p>
 * Finally, we override and implement the renderMergedOutputModel(..) method that’ll write the pdf document to the
 * response.
 *
 * @author T.N.Silverman
 */
public abstract class BaseITextPdfView extends AbstractView {

    public BaseITextPdfView() {
        setContentType("application/pdf");
    }

    protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {
    }

    protected abstract void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
        HttpServletRequest request, HttpServletResponse response) throws Exception;

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        ByteArrayOutputStream out = createTemporaryOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, out);
        writer.setViewerPreferences(PdfWriter.ALLOW_PRINTING | PdfWriter.PageLayoutSinglePage);
        buildPdfMetadata(model, document, request);
        document.open();
        buildPdfDocument(model, document, writer, request, response);
        document.close();
        writeToResponse(response, out);
    }

    @Override
    protected boolean generatesDownloadContent() {
        return true;
    }
}