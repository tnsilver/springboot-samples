/*
 * File: HelloRestController.scala
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
package com.tnsilver.contacts.controller

import com.google.gson.Gson
import com.tnsilver.contacts.exception.{PaymentRequiredException, TeapotException}
import com.tnsilver.contacts.exception.TeapotException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation._

import java.io.Serializable
import javax.annotation.PostConstruct
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

/**
 * Class HelloRestController.
 *
 * @author T.N.Silverman
 */
@RestController
@RequestMapping( Array( "/api" ) )
class HelloRestController {

  final val jsonType = "application/json;charset=UTF-8"
  final val gson = new Gson()
  @Autowired
  var ctx : ApplicationContext = _
  @Autowired
  var request : HttpServletRequest = _

  var greeting : String = _
  var stranger : String = _

  @PostConstruct
  def init() : Unit = {
    val locale = LocaleContextHolder.getLocale()
    greeting = ctx.getMessage( "hello.greeting", Array[ Object ](), "Welcome", locale )
    stranger = ctx.getMessage( "hello.stranger", Array[ Object ](), "stranger", locale )
  }

  /**
   * Spring (via ResponseEntity, RestController, and/or ResponseBody) will use the contents of the string as the raw
   * response value, rather than treating the string as JSON value to be encoded. This is true even when the
   * controller method uses produces = MediaType.APPLICATION_JSON_VALUE
   * <p>
   * One way to handle this would be to embed your string inside an object or list, so that you're not passing a raw
   * string to Spring. However, that changes the format of your output, and really there's no good reason not to be
   * able to return a properly-encoded JSON string if that's what you want to do. If that's what you want, the best
   * way to handle it is via a JSON formatter such as Json or Google Gson
   */
  @RequestMapping( value = Array( "/hello" ), produces = Array( jsonType ), method = Array( RequestMethod.GET ) )
  def greetStranger() : ResponseEntity[ String ] = {
    var firstName = stranger
    val authentication : Authentication = SecurityContextHolder.getContext.getAuthentication
    if ( authentication != null ) {
      firstName = authentication.getName
    }
    new ResponseEntity[ String ]( gson.toJson( new Message( greeting + " " + firstName ) ), HttpStatus.OK )
  }

  @RequestMapping( value = Array( "/hello/{firstName}" ), produces = Array( jsonType ), method = Array( RequestMethod.GET ) )
  def greetFirstName( @PathVariable( name = "firstName" ) firstName : String ) : ResponseEntity[ String ] = {
    new ResponseEntity[ String ]( gson.toJson( new Message( greeting + " " + firstName ) ), HttpStatus.OK )
  }

  @RequestMapping( value = Array( "/hello/{firstName}/{lastName}" ), produces = Array( jsonType ), method = Array( RequestMethod.GET ) )
  def greetFullName(
    @PathVariable( name = "firstName" ) firstName : String,
    @PathVariable( name = "lastName" ) lastName : String ) : ResponseEntity[ String ] = {
    new ResponseEntity[ String ](
      gson.toJson( new Message( greeting + " " + firstName + " " + lastName ) ),
      HttpStatus.OK )
  }

  /**
   * ResponseEntity will give you some added flexibility in defining arbitrary HTTP response headers.
   * <p>
   * See the 4th constructor at
   * { @link http://docs.spring.io/spring/docs/3.0.x/api/org/springframework/http/ResponseEntity.html}
   * <p>
   * Some commonly-used ones are Status, Content-Type and Cache-Control. If you don't need that, using @ResponseBody
   * will be a tiny bit more concise.
   */
  @RequestMapping( value = Array( "/greet" ), produces = Array( jsonType ), method = Array( RequestMethod.GET ) )
  @ResponseBody
  def greet() : String = gson.toJson( new Message( greeting + " " + stranger ) )

  @RequestMapping( path = Array( "/paynow" ) )
  @throws( classOf[ Exception ] )
  def paymentRequiredError( response : HttpServletResponse ) : Unit = {
    throw new PaymentRequiredException( "You owe money!", new TeapotException(
      "Darn! I'm a teapot!",
      new UnsupportedOperationException( "You betcha it ain't supported!" ) ) )
  }

  @RequestMapping( path = Array( "/teapot" ) )
  @throws( classOf[ Exception ] )
  def teapotError( response : HttpServletResponse ) : Unit = {
    throw new TeapotException(
      "Darn! I'm a teapot!",
      new UnsupportedOperationException( "You betcha it ain't supported!" ) )
  }

  class Message( _message : String ) extends Serializable {

    var message : String = _message

  }
}
