/*
 * File: PaymentRequiredException.scala
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
package com.tnsilver.contacts.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Class TeapotException is a sample Exception that sets the response status to a dummy code
 *
 * @author T.N.Silverman
 */
@ResponseStatus(value = HttpStatus.PAYMENT_REQUIRED,
        reason = "You owe us money, damn it! (this is a sample demo exception)") // 402
class PaymentRequiredException extends RuntimeException {

    var message : String = _
    var cause : Throwable = _

    def this(message : String) = {
        this()
        this.message = message
    }

    def this(message : String, cause : Throwable) = {
        this(message)
        this.cause = cause
    }

    override def getMessage: String = this.message
    override def getCause : Throwable = this.cause
}
