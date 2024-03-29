/**
 * File: contacts-bootstrap.js Creation Date: Oct 19, 2022
 *
 * JavaScript AJAX support for the contacts-bootstrap view
 *
 * Copyright (c) 2022 T.N.Silverman - all rights reserved
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

const ssnPattern = "[0-9]{3}-[0-9]{2}-[0-9]{4}";
const dobPattern = "[0-9]{4}-[0-9]{2}-[0-9]{2}";

/**
 * Sets the image on the btn to source
 */
function swapBtnImage(btn, source) {
    var img = btn.children[0];
    if (img != null && typeof img !== 'undefined') {
        img.src = source;
    }
}

/*
 * Handles a POST/PATCH/PUT 400 status representing server acceptance of
 * the binary payload but not being able to find the requested object.
 * @param {object}
 *            xhr the jqXHR object
 * @param {function}
 *            error the alteditor error function to invoke in order
 *            to display the error in the editing modal.
 */
function handleServerError(xhr, error) {
	//console.debug("contacts-bootstrap.js handleServerError()", xhr.status + " " + xhr.statusText);
	if (xhr.status === 400) {
		error({responseJSON: {errors: {0: [messages.getMessage('bad.request.error.msg',[xhr.status])]}}});
    } else if (xhr.status === 401) {
		error({responseJSON: {errors: {0: [messages.getMessage('unauthorized.error.msg',[xhr.status])]}}});
    } else if (xhr.status === 404) {
		error({responseJSON: {errors: {0: [messages.getMessage('not.found.error.msg',[xhr.status])]}}});
	} else if (xhr.status === 405) {
		error({responseJSON: {errors: {0: [messages.getMessage('unauthorized.error.msg',[xhr.status])]}}});
    } else {
		error({responseJSON: {errors: {0: [xhr.status + ' ' + xhr.statusText]}}});
	}
}

/*
 * Refreshes the contacts table
 */
 function reload() {
	var table = $('#contactsTable').DataTable();
	table.search('').columns().search('');
 	table.ajax.reload();
 }

/*
 * Packs a row data into a json object the server expects
 * @param {object}
 *            rowdata the edited/added row data
 * @returns {string}
 *            json representing the row data
 */
function pack(rowdata) {
	let json = {};
	json.id=rowdata.id;
	json.ssn={"ssn": rowdata['ssn.ssn']};
	json.firstName=rowdata.firstName;
	json.lastName=rowdata.lastName;
	json.birthDate=rowdata.birthDate;
	json.married=rowdata.married;
	json.children=rowdata.children;
	json = JSON.stringify(json);
	return json;
}

/*
 * Handles a POST/PATCH/PUT 400 status representing server acceptance of
 * the json payload but rejecting it with validation errors.
 * @param {object}
 *            alteditor altEditor that ajax the binary content
 * @param {object}
 *            responseJSON the jqXHR responseJSON message
 * @param {function}
 *            error the alteditor error function to invoke in order
 *            to display the error in the editing modal.
 */
function handleValidationErrors(alteditor, data, error) {
	alteditor.setFieldError(data.property, data.message);
	error({responseJSON: {errors: {0: [data.message]}}});
}

function modifyContact(alteditor, rowdata, success, error, action) {
	if (action === 'edit' || action == 'add') {
		let url = (action === 'edit') ? $('body').attr('data-contacts-url') + '/' + rowdata.id : $('body').attr('data-contacts-url');
		let method = (action === 'edit') ? 'PUT' : 'POST';
		//console.debug("contacts-bootstrap.js modifyContact('" + action + "') $ajax: type: ", method, "url: ", url);
		$.ajax({
			url:  url,
			type: method,
			contentType: "application/json",
			dataType: "json",
			data: pack( rowdata ),
			success: function (data, status, xhr) {
				if (status == 'success') {
					success( data );
					alteditor.s.dt.search(data.firstName + ' ' + data.lastName).draw()
				} else {
					console.debug("contacts-bootstrap.js $ajax(success -> " + status + ") data: ", data);
					success( {} );
				}
			},
			error: function (xhr, err, thrown) {
				handleServerError(xhr, error);
			}
		});
	} else if (action == 'delete') {
		$.ajax({
	        type: 'DELETE',
	        url:  $('body').attr('data-contacts-url') + '/' + rowdata[0].id,
	        success: function (evt, status, xhr) { success({}); }, // starus 204
	        error: function (xhr, err, thrown) { handleServerError(xhr, error); }
		});
	}
}

/*
 * Default ajax send pre event handler to add headers
 * the jqXHR object.
 */
$(document).ajaxSend(function( event, jqXHR, settings ) {
	if ($('meta[name="_csrf"]').attr('content'))
		jqXHR.setRequestHeader($('meta[name="_csrf_header"]').attr('content'), $('meta[name="_csrf"]').attr('content'));
	jqXHR.setRequestHeader('accept','application/json');
});

/*
$( window ).resize(function() {
    var table = $('#contactsTable').DataTable();
    $('#container').css( 'display', 'block' );
    table.columns.adjust().draw();
});
*/

$(document).ready(function () {

	var contactsTable = $('#contactsTable').DataTable({
		dom: "lftipr",
		altEditor: true,
		processing: true,
		responsive: true,
		deferRender: true,
		select: { style: 'single' },
		ajax: {
			url: $('body').attr('data-contacts-url') + '?size=2000',
			cache: true,
			dataSrc: function(data) { return data._embedded.contacts; }
		},
		rowId: 'id',
		columns: [
			{ data: "id", title: messages.getMessage('contact.id'), type: "readonly"},
	        { data: "ssn.ssn", title: messages.getMessage('contact.ssn'), type: 'text', pattern: ssnPattern, required: true, hoverMsg: messages.getMessage('ssn.hover.message'),
				editorOnChange : function(event, alteditor) {
					if (alteditor.isInError()) {
						alteditor.clearFieldError(event.currentTarget.id);
						alteditor.clearFormError();
					}
				}
	        },
	        { data: "firstName", title: messages.getMessage('contact.fname'), maxLength: 25, minLength: 2, required: true, hoverMsg: messages.getMessage('name.hover.message',[2,25]),
				editorOnChange : function(event, alteditor) {
					if (alteditor.isInError()) {
						alteditor.clearFieldError(event.currentTarget.id);
						alteditor.clearFormError();
					}
				}
	        },
	        { data: "lastName", title: messages.getMessage('contact.lname'), maxLength: 25, minLength: 2, required: true, hoverMsg: messages.getMessage('name.hover.message',[2,25]),
				editorOnChange : function(event, alteditor) {
					if (alteditor.isInError()) {
						alteditor.clearFieldError(event.currentTarget.id);
						alteditor.clearFormError();
					}
				}
	        },
	        { data: "birthDate", title: messages.getMessage('contact.birthDate'), type: 'date', pattern: dobPattern, required: true, hoverMsg: messages.getMessage('date.hover.message'),
				editorOnChange : function(event, alteditor) {
					if (alteditor.isInError()) {
						alteditor.clearFieldError(event.currentTarget.id);
						alteditor.clearFormError();
					}
				}
	        },
	        { data: "married", type: 'checkbox', title: messages.getMessage('contact.is.married'), type: 'checkbox', unselectedValue: false,
	        	render: function (data, type, row) { return (type === 'display') ? '<span id="marriedCbx' + row.id + '" class="glyphicon glyphicon-' + (data ? 'ok' : 'remove') + '" data="' + data + '" style="color: ' + ((data) ? 'green' : 'black') + '"></span>' : data; }
	        },
	        { data: "children", type: 'number', title: messages.getMessage('contact.children'), min: '0', max: '99', value: '0',
				editorOnChange : function(event, alteditor) {
					if (alteditor.isInError()) {
						alteditor.clearFieldError(event.currentTarget.id);
						alteditor.clearFormError();
					}
				},
				hoverMsg: messages.getMessage('children.hover.message',[0,99])
	        },
	        { data: null, type: 'hidden', visible: true, searchable: false, orderable: false,
				defaultContent: '<a class="control-btn editContactBtn btn btn-primary" href="#" style="text-align: right;"><img src="'+ messages.getMessage('images.editor.edit') +'"></a>'
		    },
	        { data: null, title: '<a class="control-btn addContactBtn btn btn-warning" href="#"><img src="' + messages.getMessage('images.editor.create') + '"></a>', name: "ctrls", type: 'hidden', visible: true, searchable: false, orderable: false,
				defaultContent: '<a class="control-btn deleteContactBtn btn btn-danger" href="#" style="text-align: right;"><img src="'+ messages.getMessage('images.editor.delete') +'"></a>'
		    }
	    ],
	    lengthMenu: [ [5, 10, 25, 50, -1], [5, 10, 25, 50, messages.getMessage("table.all.results")] ],
	    order: [ [0, 'asc'] ],
	    onDeleteRow: function(alteditor, rowdata, success, error) {
	    	modifyContact(alteditor, rowdata, success, error, 'delete');
		},
		onEditRow: function(alteditor, rowdata, success, error) {
			modifyContact(alteditor, rowdata, success, error, 'edit');
		},
		onAddRow: function(alteditor, rowdata, success, error) {
			modifyContact(alteditor, rowdata, success, error, 'add');
		}
	});

	/*
	 * Click event hadler on the delete button of the contacts table.
	 * Triggers the opening of the editor delete modal.
	 */
	$(document).on('click', "[id^='contactsTable'] .deleteContactBtn", 'tr', function (evt) {
	    var altEditor = $('#contactsTable')[0].altEditor;
	    var tr = $(this).closest('tr');
	    tr.addClass("deleting");
		//console.debug("contacts-bootstrap.js onClick(add) tr:", tr);
	    altEditor._openDeleteModal();
	    $('#altEditor-delete-form-' + altEditor.random_id)
			.off('submit')
	           .on('submit', function (ievt) {
	               ievt.preventDefault();
	               ievt.stopPropagation();
	               altEditor._deleteRow();
	           });
	    evt.stopPropagation();
	});

	/*
	 * Click event hadler on the edit button of the contacts table.
	 * Triggers the opening of the editor editing modal.
	 */
	$(document).on('click', "[id^='contactsTable'] .editContactBtn", 'tr', function (evt) {
	    var altEditor = $('#contactsTable')[0].altEditor;
	    var tr = $(this).closest('tr');
	    tr.addClass("editing");
		//console.debug("contacts-bootstrap.js onClick(edit) tr:", tr);
	    altEditor._openEditModal();
	    $('#altEditor-edit-form-' + altEditor.random_id)
			.off('submit')
	           .on('submit', function (ievt) {
	               ievt.preventDefault();
	               ievt.stopPropagation();
	               altEditor._editRowData();
	           });
	    evt.stopPropagation();
	});

	/*
	 * Click event hadler on the add button of the students table.
	 * Triggers the opening of the editor add modal.
	 */
	$(document).on('click', "[id^='contactsTable'] .addContactBtn", 'tr', function (evt) {
		var altEditor = $('#contactsTable')[0].altEditor;
		altEditor._openAddModal();
	    $('#altEditor-add-form-' + altEditor.random_id)
	                .off('submit')
	                .on('submit', function (e) {
	                    e.preventDefault();
	                    e.stopPropagation();
	                    altEditor._addRowData();
	                });
	    evt.stopPropagation();
	});


	/*
	 * Click event hadler on the data refresh button.
	 * Triggers clearing the data table and repopulating it
	 * with fresh data from the server.
	 */
	$('#refresh-btn').click(function (evt) {
		evt.preventDefault();
		reload();
	});
});
