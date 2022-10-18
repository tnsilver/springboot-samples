/**
 * File: hello.js Creation Date: June 16, 2021
 *
 * JavaScript AJAX support for the hello view
 *
 * Copyright (c) 2021 T.N.Silverman - all rights reserved
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * @author T.N.Silverman
 */

/**
 * returns a greeting message from api/hello in onload event
 * @returns greeting message
 */
function hello() {
	var url = $('#greeting').attr("data-url");
	var firstName = $('#greeting').attr("data-firstName");
	var lastName = $('#greeting').attr("data-lastName");
	if (!isEmpty(firstName)) {
		url = url + "/" + firstName;
		if (!isEmpty(lastName))
			url = url + "/" + lastName;
	}
	$.ajax({
        url: url
    }).then(function(data) {
       $('#greeting').fadeIn("fast").text(data.message);
    });
}

/**
 * returns a greeting message after submission of form
 * @returns greeting message
 */
function greet() {
	var url = $('#helloForm').attr("action"); // /api/hello
	var firstName = $('#firstName').val();
	var lastName = $('#lastName').val();
	if (!isEmpty(firstName))
		url = url + "/" + firstName;
	if (!isEmpty(lastName))
		url = url + "/" + lastName;
	$("#greeting").fadeOut("fast");
	$.ajax({
        url: url
    }).then(function(data) {
       $('#greeting').fadeIn("fast").text(data.message);
    });
}

$(document).ready(function() {
	hello();

	$("#helloForm").submit(function(event) {
		event.preventDefault();
		console.debug("#helloForm submit: event:", event);
		greet();
	});

	$(".reset").click(function() {
		$(this).closest('form').find("input[type=text], input[type=hidden], input[type=number]").val("");
		$(this).closest('form').find("input[type=text], input[type=hidden], input[type=number]")[0].setCustomValidity("");
		$(this).closest('form').find("input[type=checkbox]").prop('checked', false);
		$(this).closest('form').find("span.error").text("");
		$(this).closest('form').find("tr.error").css("display", "none");
		hello();
	});
});