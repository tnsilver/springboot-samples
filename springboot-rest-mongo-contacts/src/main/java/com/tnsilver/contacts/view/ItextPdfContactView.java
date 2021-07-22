/*
 * File: ItextPdfContactView.java
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

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.tnsilver.contacts.model.Contact;

/**
 * Class ItextPdfContactView is a simple sub class of the base iText PDF view support class to generate a contact list
 * pdf page
 *
 * @author T.N.Silverman
 */
@Component("pdfView")
public class ItextPdfContactView extends BaseITextPdfView {

    private static final Logger logger = LoggerFactory.getLogger(ItextPdfContactView.class);
    @Value("${date.pattern}")
    private String pattern;

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        logger.debug("generatating pdf...");
        @SuppressWarnings("unchecked")
        List<Contact> contacts = (List<Contact>) model.get("contacts");
        writer.setPageEvent(new MyHeaderAndFooter());
        // declare fonts
        Font h1Font = FontFactory.getFont("Times Roman", 12, BaseColor.BLACK);
        Font h2Font = FontFactory.getFont("Times Roman", 10, BaseColor.BLACK);
        Font h3Font = FontFactory.getFont(FontFactory.COURIER, 10, BaseColor.BLACK);
        Font thFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, BaseColor.BLACK);
        Font tdFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);
        // create headers paragraphs and add to document
        Paragraph h1 = new Paragraph("My Contacts - PDF", h1Font);
        document.add(h1);
        Paragraph h2 = new Paragraph("Spring MVC Non Template ViewsScanMarker", h2Font);
        document.add(h2);
        Paragraph h3 = new Paragraph(String.format("(displaying %d contacts)", contacts.size()), h3Font);
        document.add(h3);
        // create table with column width percentage
        PdfPTable table = new PdfPTable(new float[] { 7, 15, 15, 15, 15, 12, 26 });
        table.setSpacingBefore(20f);
        table.setSpacingAfter(20f);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        // add header cells to table
        table.addCell(getCell("ID", thFont));
        table.addCell(getCell("SSN", thFont));
        table.addCell(getCell("First Name", thFont));
        table.addCell(getCell("Last Name", thFont));
        table.addCell(getCell("Birth Date", thFont));
        table.addCell(getCell("Married?", thFont));
        table.addCell(getCell("No. of Kids", thFont));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        // add content cells to table
        contacts.stream().forEach(contact -> {
            table.addCell(getCell(String.valueOf(contact.getId()), tdFont));
            table.addCell(getCell(contact.getSsn().toString(), tdFont));
            table.addCell(getCell(contact.getFirstName(), tdFont));
            table.addCell(getCell(contact.getLastName(), tdFont));
            table.addCell(getCell(formatter.format(contact.getBirthDate()), tdFont));
            table.addCell(getCell(String.valueOf(contact.getMarried()), tdFont));
            table.addCell(getCell(String.valueOf(contact.getChildren()), tdFont));
        });
        // add table to document
        document.add(table);
    }

    private PdfPCell getCell(String text, Font font) {
        Paragraph content = new Paragraph(text, font);
        PdfPCell cell = new PdfPCell(content);
        cell.setColspan(1);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }

    private class MyHeaderAndFooter extends PdfPageEventHelper {

        Font hFont = new Font(Font.FontFamily.UNDEFINED, 10, Font.ITALIC);
        Font fFont = new Font(Font.FontFamily.UNDEFINED, 5, Font.ITALIC);

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Phrase header = new Phrase("PDF document created with iText 5.5.11", hFont);
            Phrase footer = new Phrase("created by T.N.Silverman June 01, 2017", fFont);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, header,
                (document.right() - document.left()) / 2 + document.leftMargin(), document.top() + 10, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer,
                (document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() - 10, 0);
        }
    }
}
