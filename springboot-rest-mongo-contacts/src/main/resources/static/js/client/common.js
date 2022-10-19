/**
 * File: common.js Creation Date: March 06, 2021
 *
 * JavaScript utility functions to support UI and form related actions
 *
 * Copyright (c) 2021 T.N.Silverman - all rights reserved
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * @author T.N.Silverman
 */

/**
 * gets the context path of the application based on the value of the body's
 * 'data-context-path' attribute
 */
function getContextPath() {
    var ctx = $("body").attr("data-context-path");
    if (isEmpty(ctx)) {
        console.debug("common.js:getContextPath() -> no 'data-context-path' attribute found on body element!");
        return "/";
    }
    return ctx;
}

/**
 * gets the context path of the application based on the value of the body's
 * 'data-image-path' attribute
 */
function getImagePath() {
    var path = $("body").attr("data-image-path");
    if (isEmpty(path)) {
        console.debug("common.js:getImagePath() -> no 'data-image-path' attribute found on body element!");
        return "resources/images";
    }
    return path;
}

/**
 * Test if object is empty
 *
 * @param obj -
 *            any JS object
 * @returns true if the given object is empty or if it's a string with value 'null'
 */
function isEmpty(obj) {
    if (typeof obj == 'undefined')
        return true;
    if (obj == null)
        return true;
    if (typeof obj == 'string' && (obj.trim() === "null"))
        return true;
    if (typeof obj == 'string' && obj.length > 0 && obj.trim() === "0")
        return false;
    if (typeof obj == 'number' && obj == 0)
        return false;
    if (obj == false)
        return true;
    if ((obj.length == 0) || (obj == "") || (typeof obj == 'string' && obj.replace(/\s/g, "") == "")
            || (!/[^\s]/.test(obj)) || (/^\s*$/.test(obj)))
        return true;
    if ((typeof obj == 'string' && obj.trim().length == 0) || obj.length == 0)
        return true;
    return false;
}

/**
 * Sets the image on the btn to source
 */
function swapBtnImage(btn, source) {
    var img = btn.children[0];
    if (img != null && typeof img !== 'undefined') {
        img.src = source;
    }
}

/**
 * clears an error for a given input by setting the closes TR element display property to none.
 * The closes TR is expected to contain an id that is identical to the input id but is suffixed with the String 'Error'.
 * @param input the input to reset
 */
function clearError(input) {
	var target = input.id + "Error";
	$('#' + target).closest('tr').css("display", "none");
	$('#' + target).text('');
}

/*
 * Default ajax send pre event handler to add headers
 * the jqXHR object.
 */
$(document).ajaxSend(function( event, jqXHR, settings ) {
	if (!isEmpty($('meta[name="_csrf"]').attr('content')))
		jqXHR.setRequestHeader($('meta[name="_csrf_header"]').attr('content'), $('meta[name="_csrf"]').attr('content'));
});

