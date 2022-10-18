/*
 * File: BaseDocumentModelListener.java
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
package com.tnsilver.contacts.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import com.tnsilver.contacts.model.BaseDocument;

@Component
public class BaseDocumentModelListener extends AbstractMongoEventListener<BaseDocument> {

    private static final Logger logger = LoggerFactory.getLogger(BaseDocumentModelListener.class);

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<BaseDocument> event) {
        if (null == event.getSource().getId() || event.getSource().getId() < 1) {
            long id = sequenceGenerator.generateSequence(event.getSource().getSeqId());
            event.getSource().setId(id);
            logger.trace("generated sequence #{} for '{}'", id, event.getSource());
        }
    }
}
