/*
 * File: XlsContactView.scala
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

import com.tnsilver.contacts.model.Contact
import org.apache.poi.ss.usermodel.Workbook
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.servlet.view.document.AbstractXlsView

import java.time.format.DateTimeFormatter
import java.util
import java.util.concurrent.atomic.AtomicInteger
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

/**
 * Class XlsContactView is a simple sub-class to generate an Excel contact list view
 *
 * @author T.N.Silverman
 */
@Component("excelView")
class XlsContactView extends AbstractXlsView {

    @Value("${date.pattern}")
    var  pattern : String = _

    @throws( classOf[ Exception ] )
    override def buildExcelDocument(model : util.Map[String, Object], workbook : Workbook, request : HttpServletRequest,
                                    response : HttpServletResponse): Unit = {
        logger.debug("generatating excell...")
        // change the file name
        response.setHeader("Content-Disposition", "attachment; filename=\"contacts.xls\"")
        val contacts = model.get("contacts").asInstanceOf[util.List[Contact]]
        // create excel xls sheet
        val sheet = workbook.createSheet("Spring MVC Non Template ViewsScanMarker - AbstractXlsView")
        // create header row
        val header = sheet.createRow(0)
        header.createCell(0).setCellValue("ID")
        header.createCell(1).setCellValue("SSN")
        header.createCell(2).setCellValue("First Name")
        header.createCell(3).setCellValue("Last Name")
        header.createCell(4).setCellValue("Birth Date")
        header.createCell(5).setCellValue("Married?")
        header.createCell(6).setCellValue("No. of Kids")
        // Create data cells
        val rowNum = new AtomicInteger(1)
        val formatter = DateTimeFormatter.ofPattern(pattern)
        contacts.forEach(contact => {
            val row = sheet.createRow(rowNum.incrementAndGet())
            row.createCell(0).setCellValue(contact.getId.doubleValue)
            row.createCell(1).setCellValue(contact.getSsn.toString)
            row.createCell(2).setCellValue(contact.getFirstName)
            row.createCell(3).setCellValue(contact.getLastName)
            row.createCell(4).setCellValue(formatter.format(contact.getBirthDate))
            row.createCell(5).setCellValue(String.valueOf(contact.getMarried))
            row.createCell(6).setCellValue(contact.getChildren.doubleValue)
        })
    }
}
