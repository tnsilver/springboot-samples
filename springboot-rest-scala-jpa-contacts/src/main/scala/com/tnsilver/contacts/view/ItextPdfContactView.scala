/*
 * File: ItextPdfContactView.scala
 * Creation Date: Feb 4, 2021
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
package com.tnsilver.contacts.view

import com.itextpdf.text.pdf._
import com.itextpdf.text._
import com.tnsilver.contacts.model.Contact
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import java.time.format.DateTimeFormatter
import java.util
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

/**
 * Class ItextPdfContactView is a simple sub class of the base iText PDF view support class to generate a contact list
 * pdf page
 *
 * @author T.N.Silverman
 */
@Component("pdfView")
class ItextPdfContactView extends BaseITextPdfView {

    @Value("${date.pattern}")
    var  pattern : String = _

    @throws( classOf[ Exception ] )
    override def buildPdfDocument(model : util.Map[String, Object], document : Document, writer : PdfWriter,
                                  request : HttpServletRequest, response : HttpServletResponse): Unit = {
        logger.debug("generatating pdf...")
        response.setHeader("Content-Disposition", "attachment; filename=\"contacts.pdf\"")
        val contacts = model.get("contacts").asInstanceOf[util.List[Contact]]
        writer.setPageEvent(new MyHeaderAndFooter())
        // declare fonts
        val h1Font = FontFactory.getFont("Times Roman", 12, BaseColor.BLACK)
        val h2Font = FontFactory.getFont("Times Roman", 10, BaseColor.BLACK)
        val h3Font = FontFactory.getFont(FontFactory.COURIER, 10, BaseColor.BLACK)
        val thFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, BaseColor.BLACK)
        val tdFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY)
        // create headers paragraphs and add to document
        val h1 = new Paragraph("My Contacts - PDF", h1Font)
        document.add(h1)
        val h2 = new Paragraph("Spring MVC Non Template ViewsScanMarker", h2Font)
        document.add(h2)
        val h3 = new Paragraph("(displaying "+contacts.size()+" contacts)", h3Font)
        document.add(h3)
        // create table with column width percentage
        val table : PdfPTable = new PdfPTable(Array[Float](7, 15, 15, 15, 15, 12, 26))
        table.setSpacingBefore(20f)
        table.setSpacingAfter(20f)
        table.setHorizontalAlignment(Element.ALIGN_LEFT)
        // add header cells to table
        table.addCell(getCell("ID", thFont))
        table.addCell(getCell("SSN", thFont))
        table.addCell(getCell("First Name", thFont))
        table.addCell(getCell("Last Name", thFont))
        table.addCell(getCell("Birth Date", thFont))
        table.addCell(getCell("Married?", thFont))
        table.addCell(getCell("No. of Kids", thFont))
        val formatter = DateTimeFormatter.ofPattern(pattern)
        // add content cells to table
        contacts.stream().forEach(contact => {
            table.addCell(getCell(String.valueOf(contact.getId), tdFont))
            table.addCell(getCell(contact.getSsn.toString, tdFont))
            table.addCell(getCell(contact.getFirstName, tdFont))
            table.addCell(getCell(contact.getLastName, tdFont))
            table.addCell(getCell(formatter.format(contact.getBirthDate), tdFont))
            table.addCell(getCell(String.valueOf(contact.getMarried), tdFont))
            table.addCell(getCell(String.valueOf(contact.getChildren), tdFont))
        })
        // add table to document
        document.add(table)
    }

    def getCell(text : String, font : Font) :PdfPCell = {
        val content = new Paragraph(text, font)
        val cell = new PdfPCell(content)
        cell.setColspan(1)
        cell.setBorder(Rectangle.NO_BORDER)
        cell.setHorizontalAlignment(Element.ALIGN_LEFT)
        cell
    }

    private class MyHeaderAndFooter extends PdfPageEventHelper {

        val hFont = new Font(Font.FontFamily.UNDEFINED, 10, Font.ITALIC)
        val fFont = new Font(Font.FontFamily.UNDEFINED, 5, Font.ITALIC)


        override def onEndPage( writer : PdfWriter,  document : Document): Unit = {
            val cb = writer.getDirectContent
            val header = new Phrase("PDF document created with iText 5.5.11", hFont)
            val footer = new Phrase("created by T.N.Silverman June 01, 2017", fFont)
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, header, (document.right() - document.left()) / 2 + document.leftMargin(), document.top() + 10, 0)
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, (document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() - 10, 0)
        }
    }
}
