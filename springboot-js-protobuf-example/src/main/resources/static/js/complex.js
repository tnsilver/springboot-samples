	/**
	 *
	 * complex.js
	 *
	 * @description. javascript code for the complex.html template
	 * @version 1.0
	 * @author T.N.Silverman <tnsilver@gmail.com>
	 *
	 */

    const emailPattern = "[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$";

    const phonePattern = "[+\d]?[0-9]{6,13}";

	const studTab = '<table id="studentsTable" class="display compact responsive">' +
					'<thead>' +
						'<tr>' +
							'<th></th>' +
							'<th>' + messages.getMessage('detailstable.th.student.id') + '</th>' +
							'<th>' + messages.getMessage('detailstable.th.student.fname') + '</th>' +
							'<th>' + messages.getMessage('detailstable.th.student.lname') + '</th>' +
							'<th>' + messages.getMessage('detailstable.th.student.email') + '</th>' +
							'<th>' + messages.getMessage('detailstable.th.student.phonenums') + '</th>' +
							'<th>' + '<a class="control-btn addStudBtn btn btn-warning" href="#">' +
										'<img src="' + messages.getMessage('images.editor.create') + '">' +
									 '</a>' +
							'</th>' +
							'<th></th>' +
							'<th></th>' +
							'<th></th>' +
						'</tr>' +
					'</thead>' +
					'<tbody></tbody>'+
				'</table>';

	const phoneTab = '<table id="phonesTable" class="display compact responsive">' +
					'<thead>' +
						'<tr>' +
							'<th>' + messages.getMessage('subtable.th.student.phone.id') + '</th>' +
							'<th>' + messages.getMessage('subtable.th.student.phone.type') + '</th>' +
							'<th>' + messages.getMessage('subtable.th.student.phone.number') + '</th>' +
							'<th>' + messages.getMessage('detailstable.th.student.id') + '</th>' +
							'<th>' +
								'<a class="control-btn addPhoneBtn btn btn-warning" href="#">' +
									'<img src="' + messages.getMessage('images.editor.create') + '">' +
							    '</a>' +
							'</th>' +
 						'</tr>' +
					'</thead>' +
 					'<tbody></tbody>'+
				  '</table>';

    /*
     * Translates a numeric type to its string constant
     * for example: getKey(proto.protobuf.Student.PhoneType,1)
     * @param {string}
     *            the map of key -> value pairs
     * @param {object}
     *            val the key to return the value for
     * @returns {string}
     *            the key the given val is assigned to
     */
	function getKey(map, val) { return Object.keys(map).find(key => map[key] === val); };

    /*
     * Creates an array of select options for a given protobuf enum
     * for example: getEnumOptions(proto.protobuf.Student.PhoneType,[0,1,2,3,4])
     * @param {string}
     *            protoEnum the protobuf enum, for example: proto.protobuf.Student.PhoneType
     * @param {array}
     *            values the values for which to create the options
     * @returns {array}
     *            an array of select options for a given protobuf enum, for example:
     *            [ "NONE", "MOBILE", "LANDLINE", "TOLLFREE", "BUSINESS"]
     */
    function getEnumOptions(protoEnum,...values) {
    	const options=[];
    	values.forEach((v,i) => { options.push('' + Object.keys(protoEnum).find(key => protoEnum[key] === v) + ''); });
    	return options;
    };

    /*
     * Translates proto.protobuf.Student.PhoneType string key to enum int value
     * for example: 'MOBILE' returns 1
     * @param {string}
     *            key the proto.protobuf.Student.PhoneType key
     * @returns {number}
     *            the proto.protobuf.Student.PhoneType enum int value corresponding with the key.
     *
     */
    function getPhoneTypeValue(key) {
    	return proto.protobuf.Student.PhoneType[key];
    };

    /*
     * Packs a protobuf message as binary uint8Array to wire
     * @param {object}
     *            rowdata the rowdata as returned from an editor modal
     * @param {object}
     *            prototype the object to assign properties from the
     *            given rowdata. Typically empty object {}.
     * @param {string}
     *            builder function name to create the protobuf object.
     *            This is one of 'buildCourse', 'buildStudent' or 'buildPhoneNumber'
     * @returns {uint8Array}
     *            a packaged binary message created from the given row data.
     *
     */
    function pack(rowdata, prototype, builder) {
	    const merged = Object.assign(prototype, rowdata);
		const result = window[builder](merged);
		const uint8Array = result.serializeBinary();
		return jspb.Message.bytesAsB64(uint8Array);
    }

    /*
     * Handles a POST/PATCH/PUT 202 status representing server acceptance of
     * the binary payload but rejecting it with validation errors.
     * @param {object}
     *            alteditor altEditor that ajax the binary content
     * @param {object}
     *            arrayBuffer the binary jqXHR response
     * @param {function}
     *            error the alteditor error function to invoke in order
     *            to display the error in the editing modal.
     */
    function handleValidationErrors(alteditor, arrayBuffer, error) {
    	const message = proto.protobuf.Error.deserializeBinary(arrayBuffer).toObject();
		const name = message.fielderrorsList[0].name;
		const status = message.fielderrorsList[0].status;
		alteditor.setFieldError(name, status);
		error({responseJSON: {errors: {0: [messages.getMessage("message.has.errors",[name])]}}});
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
    function handleBadRequest(xhr, error) {
    	if (xhr.status === 400 || xhr.status === 202) {
            error({responseJSON: {errors: {0: [messages.getMessage('message.validation.error')]}}});
    	} else {
    		error({responseJSON: {errors: {0: [messages.getMessage('message.unknown.error')]}}});
    	}
    }

    /*
     * Copies selected array of key properties to a given object
     * for example: getKey(proto.protobuf.Student.PhoneType,1)
     * @param {Object}
     *            obj the given object to copy (pick) properties into
     * @param {Array}
     *            keys the keys array, each representing a property name
     * @returns {Object}
     *            the populated object
     */
	function pick(obj, keys){ return  Object.assign({}, ...keys.map(key => ({ [key]: obj[key] }))) };

    /*
     * Returns the id of the altEditor form being opened for a
     * CRUD operation such as 'add', 'edit', or 'delete'.
     * for example: getKey(proto.protobuf.Student.PhoneType,1)
     * @param {Object}
     *            alteditor a reference to the current altEditor
     * @param {string}
     *            action the CRUD action 'add', 'edit', or 'delete'.
     * @returns {string}
     *            the id of the altEditor form being opened for a CRUD action.
     */
	function getCoursesEditorFormId(alteditor, action) { return !alteditor ? '' : '#altEditor-' + action + '-form-' + alteditor.random_id; };

    /*
     * Refreshes the courses table after DML of child table.
     */
	function refreshCoursesTable() {
		const parentTab = $('#coursesTable').DataTable();
		const selected = parentTab.row( { selected: true } );
		if (selected.any())
			destroyChild(selected);
		parentTab.ajax.url($('body').attr('data-courses-url')).load();
	}

    /*
     * Builds a proto.protobuf.Course from the given data object.
     * @param {Object}
     *            data the DataTable row object to build a course from
     * @returns {proto.protobuf.Course}
     *            the course built from the given data
     */
	function buildCourse( data ) {
		//console.debug("buildCourse() -> data:", data);
        const course = new proto.protobuf.Course();
        if (data.id)
        	course.setId(data.id);
        else
        	course.setId(0);
        if (data.courseName)
        	course.setCourseName(data.courseName);
        if (data.studentList) {
                data.studentList.forEach((s,i) => {
                	const student = new proto.protobuf.Student();
                	student.setId(s.id);
                	student.setFirstName(s.firstName);
                	student.setLastName(s.lastName);
                	student.setEmail(s.email);
                	if (s.phoneList) {
	                	s.phoneList.forEach((p,i) => {
	                		const phoneNumber = new proto.protobuf.Student.PhoneNumber();
	                		phoneNumber.setId(p.id);
	                		phoneNumber.setNumber(p.number);
	                		phoneNumber.setType(p.type);
	                		student.addPhone(phoneNumber);
	                	})
                	}
                	if (s.courseRefsList)
	                	student.setCourseRefsList(s.courseRefsList);
                	course.addStudent(student);
				});
        }
		return course;
	};

    /*
     * Builds a proto.protobuf.Student from the given data object.
     * @param {Object}
     *            data the DataTable row object to build a student from
     * @returns {proto.protobuf.Student}
     *            the student built from the given data
     */
	function buildStudent( data ) {
		const student = new proto.protobuf.Student();
        if (data.id)
        	student.setId(data.id);
        else
        	student.setId(0);
        if (data.firstName)
        	student.setFirstName(data.firstName);
        if (data.lastName)
        	student.setLastName(data.lastName);
        if (data.email)
        	student.setEmail(data.email);
        if (data.phoneList) {
            data.phoneList.forEach((p,i) => {
            	const phone = new proto.protobuf.Student.PhoneNumber();
            	phone.setId(p.id);
            	phone.setNumber(p.number);
            	phone.setType(p.type);
            	student.addPhone(phone);
			});
        }
        if (data.courseRefsList)
        	student.setCourseRefsList(data.courseRefsList);
		return student;
	}

    /*
     * Builds a proto.protobuf.Student.PhoneNumber from the given data object.
     * @param {Object}
     *            data the DataTable row object to build a student from
     * @returns {proto.protobuf.Student.PhoneNumber}
     *            the student built from the given data
     */
	function buildPhoneNumber( data ) {
		const phoneNumber = new proto.protobuf.Student.PhoneNumber();
        if (data.id) {
        	phoneNumber.setId(data.id);
        } else {
        	phoneNumber.setId(0);
        }
        if (data.number)
        	phoneNumber.setNumber(data.number);
        if (data.type) {
        	phoneNumber.setType(data.type);
        } else {
        	jspb.Message.setProto3EnumField(phoneNumber, 3, 0);
        }
        if (data.studentRef) {
        	phoneNumber.setStudentRef(data.studentRef);
        } else {
        	phoneNumber.setStudentRef(0);
        }
		return phoneNumber;
	}

    /*
     * Builds and displays the phones DataTable
     * from the given data row object.
     * @param {Object}
     *            row the DataTable row object, either selected
     *            or found on the parent students table.
     */
	function createPhoneNumbersTable ( row ) {
		const table = $(phoneTab);
		row.child(table).show();
		const phonesTable = table.DataTable( {
			dom: "tipr",
			altEditor: true,
			deferRender: true,
		    data: row.data().phoneList,
		    /*
		       Note: for some reason ajax in child table triggers a
		       reload we lose the selected parent row. It also screws
		       up the responsiveness on mobile devices.
		    */
		    /* ajax: {
		    	url: $('body').attr('data-students-url') + '/' + row.data().id + '/phones',
				dataType: "binary",
				responseType: 'arraybuffer',
				cache: true,
				dataSrc: function(data) {
					let obj = proto.protobuf.PhoneNumbers.deserializeBinary(data).toObject();
					console.debug("createPhoneNumbersTable() -> object", obj.phoneList);
					return obj.phoneList;
				}
			}, */
		    searching: false,
		    info: false,
		    lengthChange: false,
		    paging: false,
		    responsive: true,
			rowId: 'id',
			columns: [ { data: 'id', type: 'readonly', defaultContent: '0'},
				       { data: 'type', type: 'select', options: getEnumOptions(proto.protobuf.Student.PhoneType,1,2,3,4),
							render: function (data, type, row, meta) {
	                            return getKey(proto.protobuf.Student.PhoneType,data);
				            },
				            editorOnChange : function(event, altEditor) {
								if (!$(event.currentTarget).val()) {
									let rows = altEditor.s.dt.rows(['.editing']);
									if (rows.any()) {
										let type = rows.data()[0].type;
										if (type) {
											let input = $(altEditor.modal_selector).find('#type');
											input.val(getKey(proto.protobuf.Student.PhoneType,type));
										}
									}
								}
							},
				            defaultContent: getEnumOptions(proto.protobuf.Student.PhoneType,[row.data().type])
				       },
					   { data: 'number', type: "text", pattern: phonePattern, required: true,
				        	hoverMsg: messages.getMessage('message.phone.hover',[6,13]),
							editorOnChange : function(event, alteditor) {
								if (alteditor.isInError()) {
									alteditor.clearFieldError(event.currentTarget.id);
									alteditor.clearFormError();
								}
						}},
					   { data: 'studentRef', type: 'readonly', visible: false, searchable: false, orderable: false},
					   { data: null, name: "ctrls", type: 'hidden', visible: true, searchable: false, orderable: false,
							defaultContent: '<a class="control-btn editPhoneBtn btn btn-primary" href="#" style="text-align: right;"><img src="'+ messages.getMessage('images.editor.edit') +'"></a>&nbsp;<a class="control-btn deletePhoneBtn btn btn-danger" href="#" ><img src="'+ messages.getMessage('images.editor.delete') +'"></a>'
					   }
			],
		    onDeleteRow: function(alteditor, rowdata, success, error) {
	        	modifyPhoneNumber(alteditor, rowdata, 'delete', success, error);
	        },
	        onEditRow: function(alteditor, rowdata, success, error, originaldata) {
	        	console.debug("createPhoneNumbersTable() onEditRow - originaldata: ", originaldata);
	        	modifyPhoneNumber(alteditor, rowdata, 'edit', success, error);
	        },
	        onAddRow: function(alteditor, rowdata, success, error) {
	        	modifyPhoneNumber(alteditor, rowdata, 'add', success, error);
	        }
		});//.on( 'destroy.dt', function ( e, settings ) { console.debug("data table", e.target.id, "destroyed!"); });
	}

    /*
     * Builds and displays the students DataTable
     * from the given data row object.
     * @param {Object}
     *            row the DataTable row object, selected on the parent courses table.
     */
	function createStudentsTable ( row ) {
        const courseId = row.data().id;
        const table = $(studTab);
        row.child(table).show();
        const studentsTable = table.DataTable( {
			dom: "tipr",
			altEditor: true,
			deferRender: true,
		    data: row.data().studentList,
		    /*
		       Note: for some reason ajax in child table triggers a
		       reload we lose the selected parent row. It also screws
		       up the responsiveness on mobile devices.
		    */
		    /* ajax: {
		    	url: $('body').attr('data-courses-url') + '/' + courseId + '/students',
				dataType: "binary",
				responseType: 'arraybuffer',
				cache: true,
				dataSrc: function(data) { return proto.protobuf.Students.deserializeBinary(data).toObject().studentsList; }
			}, */
			searching: false,
			paging: false,
			lengthChange: false,
			info: false,
			responsive: true,
			deferRender: true,
			rowId: 'id',
		    columns: [	{ data: null, type: 'hidden', className:  'dt-control details', orderable: false, searchable: false, defaultContent: ''},
						{ data: 'id', type: 'readonly', value: "0" },
						{ data: "firstName", type: "text", maxLength: 20, minLength: 2, required: true,
				        	hoverMsg: messages.getMessage('message.name.hover',[2,20]),
							editorOnChange : function(event, alteditor) {
								if (alteditor.isInError()) {
									alteditor.clearFieldError(event.currentTarget.id);
									alteditor.clearFormError();
								}
							}
				        },
						{ data: "lastName", type: "text", maxLength: 20, minLength: 2, required: true,
				        	hoverMsg: messages.getMessage('message.name.hover',[2,20]),
							editorOnChange : function(event, alteditor) {
								if (alteditor.isInError()) {
									alteditor.clearFieldError(event.currentTarget.id);
									alteditor.clearFormError();
								}
							}
				        },
				        { data: "email", type: "text", pattern: emailPattern, maxLength: 40, minLength: 6, required: true,
				        	hoverMsg: messages.getMessage('message.email.hover',[6,40]),
							editorOnChange : function(event, alteditor) {
								if (alteditor.isInError()) {
									alteditor.clearFieldError(event.currentTarget.id);
									alteditor.clearFormError();
								}
							}
				        },
						{ data: 'phoneList.length', type: 'hidden'},
						{ data: null, type: "hidden", name: "ctrls", visible: true, searchable: false, orderable: false,
								defaultContent: '<a class="control-btn editStudBtn btn btn-primary" href="#"><img src="'+ messages.getMessage('images.editor.edit') +'"></a>&nbsp;<a class="control-btn deleteStudBtn btn btn-danger" href="#"><img src="'+ messages.getMessage('images.editor.delete') +'"></a>'
						},
						{ data: null, type: 'hidden', defaultContent: ''},
						{ data: 'phoneList', type: 'hidden', visible: false, searchable: false, orderable: false},
						{ data: 'courseRefsList', type: 'hidden', visible: false, searchable: false, orderable: false}
			],
		    order: [ [1, 'asc'] ],
		    buttons: [],
		    onDeleteRow: function(alteditor, rowdata, success, error) {
	        	modifyStudent(alteditor, rowdata, 'delete', success, error);
	        },
	        onEditRow: function(alteditor, rowdata, success, error) {
	        	modifyStudent(alteditor, rowdata, 'edit', success, error);
	        },
	        onAddRow: function(alteditor, rowdata, success, error) {
	        	modifyStudent(alteditor, rowdata, 'add', success, error);
	        }
		});//.on( 'destroy.dt', function ( e, settings ) { console.debug("data table", e.target.id, "destroyed!"); });

	    /*
	     * click event hadler on the control td of the students table
	     * triggers the build or destruction of the child phone numbers
	     * table.
	     */
		$('#studentsTable tbody').on('click', 'td.dt-control.details', function () {
		    let tr = $(this).closest('tr');
		    let row = studentsTable.row( tr );
		    if ( row.child.isShown() ) {
			    console.debug("onClick() dt-control shown -> tr", tr);
			    console.debug("onClick() dt-control shown -> row", row.data());
		        destroyChild(row);
		        tr.removeClass('shown');
		    } else {
		        var shownrows = studentsTable.rows( [ '.shown'] );
		        if (shownrows[0]) {
			        shownrows.eq(0).each( function ( index ) {
			        	shownrow = studentsTable.row( index );
			            destroyChild(shownrow);
			            const showntr = shownrow.node();
			            if (showntr)
			            	$(showntr).removeClass('shown');
			        });
		        }
		        createPhoneNumbersTable(row);
		        tr.addClass('shown');
		        row.child.show();
		    }
		});
	}

    /*
     * Destroys a child DataTable when parent row is deselected.
     * @param {Object}
     *				row the DataTable row object, either
     *				selected or found on the parent table.
     */
	function destroyChild(row) {
    	try {
			const table = $('table', row.child());
		    table.detach();
		    table.DataTable().destroy();
		    row.child.hide();
    	} catch(err) {
            console.error("Error: ", err);
	    }
	}

    /*
     * Modifies a given row data representing a proto.protobuf.Course
     * by building an instance and ajax it to the server for a DML operation.
     * @param {Object}
     *            alteditor the current editor
     * @param {Object}
     *            rowdata the row data representing a proto.protobuf.Course
     * @param {string}
     *            action representing the DML operation 'add', 'edit' or 'delete'.
     * @param {function}
     *            success ajax handler function reference
     * @param {function}
     *            error ajax handler function reference
     */
	function modifyCourse(alteditor, rowdata, action, success, error) {
		let payload = null;
		if (action === 'add') {
			payload = pack(rowdata,{},'buildCourse');
		} else if (action === 'edit') {
			const selected = alteditor.s.dt.row( { selected: true } );
			payload = pack(rowdata, selected.data(), 'buildCourse');
		} else if (action === 'delete') {
			id = rowdata[0].id;
			$.ajax({
	            type: 'DELETE',
	            url:  $('body').attr('data-courses-url') + "/" + id,
	            success: function (evt, status, xhr) { success({}); },
	            //error: function (xhr, err, thrown) { error(xhr, err, thrown); }
	            error: function (xhr, err, thrown) { handleBadRequest(xhr, error); }
	        });
			return;
		}
		$.ajax({
			url:  $('body').attr('data-courses-url'),
            type: action === 'edit' ? 'PATCH' : 'POST',
            contentType: "application/x-protobuf",
            responseType: 'arraybuffer',
			dataType: "binary",
			data: payload,
            success: function (arrayBuffer, status, xhr) {
            	if (status == 'success') {
            		if (xhr.status === 201) {
	            		let result = proto.protobuf.Course.deserializeBinary(arrayBuffer).toObject();
			    	    success( result );
            		}
            		if (xhr.status === 202) { // server returned (accepted 202) validation error message
            			handleValidationErrors(alteditor, arrayBuffer, error);
            		}
            	}
            },
            error: function (xhr, err, thrown) { handleBadRequest(xhr, error); }
        });
	};

    /*
     * Modifies a given row data representing a proto.protobuf.Student
     * by building an instance and ajax it to the server for a DML operation.
     * @param {Object}
     *            alteditor the current editor
     * @param {Object}
     *            rowdata the row data representing a proto.protobuf.Course
     * @param {string}
     *            action representing the DML operation 'add', 'edit' or 'delete'.
     * @param {function}
     *            success ajax handler function reference
     * @param {function}
     *            error ajax handler function reference
     */
	function modifyStudent(alteditor, rowdata, action, success, error) {
		let payload = null;
		if (action === 'add') {
        	let courseId = $('#coursesTable').DataTable().row( { selected: true } ).data().id;
        	rowdata.courseRefsList=[courseId];
        	rowdata.phoneList=[];
        	payload = pack(rowdata, {}, 'buildStudent');
		} else if (action === 'edit') {
        	const edited = alteditor.s.dt.row( ['.editing'] );
        	rowdata.phoneList = edited.data().phoneList;
        	payload = pack(rowdata, edited.data(), 'buildStudent');
		} else if (action === 'delete') {
		    const studentId = rowdata[0].id;
		    $.ajax({
	            type: 'DELETE',
	            url:  $('body').attr('data-students-url') + '/' + studentId,
	            success: function (evt, status, xhr) {
	            	success({});
	            	refreshCoursesTable();
	            },
	            error: function (xhr, err, thrown) { handleBadRequest(xhr, error); }
	        });
			return;
		}
		$.ajax({
			url:  $('body').attr('data-students-url'),
            type: action === 'edit' ? 'PATCH' : 'POST',
            contentType: "application/x-protobuf",
            responseType: 'arraybuffer',
			dataType: "binary",
			data: payload,
            success: function (arrayBuffer, status, xhr) {
            	if (status == 'success') {
            		if (xhr.status === 201) {
	            		let result = proto.protobuf.Student.deserializeBinary(arrayBuffer).toObject();
			    	    success( result );
			    	    refreshCoursesTable();
            		}
            		if (xhr.status === 202) { // server returned (accepted 202) validation error message
            			handleValidationErrors(alteditor, arrayBuffer, error);
            		}
            	}
            },
            error: function (xhr, err, thrown) { handleBadRequest(xhr, error); }
        });
	};

    /*
     * Modifies a given row data representing a proto.protobuf.Student.PhoneNumber
     * by building an instance and ajax it to the server for a DML operation.
     * @param {Object}
     *            alteditor the current editor
     * @param {Object}
     *            rowdata the row data representing a proto.protobuf.Course
     * @param {string}
     *            action representing the DML operation 'add', 'edit' or 'delete'.
     * @param {function}
     *            success ajax handler function reference
     * @param {function}
     *            error ajax handler function reference
     */
	function modifyPhoneNumber(alteditor, rowdata, action, success, error) {
		let payload = null;
		if (action === 'add') {
        	rowdata.id = 0;
			rowdata.type=getPhoneTypeValue(rowdata.type);
			payload = pack(rowdata, {}, 'buildPhoneNumber');
		} else if (action === 'edit') {
			rowdata.type=getPhoneTypeValue(rowdata.type);
			payload = pack(rowdata, {}, 'buildPhoneNumber');
		} else if (action === 'delete') {
		    const phoneNumberId = rowdata[0].id;
		    const studentRef = rowdata[0].studentRef;
		    $.ajax({
	            type: 'DELETE',
	            url:  $('body').attr('data-phones-url') + '/' + phoneNumberId,
	            success: function (evt, status, xhr) {
	            	success({});
	            	refreshCoursesTable();
	            },
	            error: function (xhr, err, thrown) { handleBadRequest(xhr, error); }
	        });
			return;
		}
		$.ajax({
			url:  $('body').attr('data-phones-url'),
            type: action === 'edit' ? 'PATCH' : 'POST',
            contentType: "application/x-protobuf",
            responseType: 'arraybuffer',
			dataType: "binary",
			data: payload,
            success: function (arrayBuffer, status, xhr) {
            	if (status == 'success') {
            		if (xhr.status === 201) {
	            		let result = proto.protobuf.Student.PhoneNumber.deserializeBinary(arrayBuffer).toObject();
			    	    success( result );
			    	    refreshCoursesTable();
            		}
            		if (xhr.status === 202) { // server returned (accepted 202) validation error message
            			handleValidationErrors(alteditor, arrayBuffer, error);
            		}
            	}
            },
            error: function (xhr, err, thrown) { handleBadRequest(xhr, error); }
        });
	};

    /*
     * Click event hadler on the edit button of the students table.
     * Triggers the opening of the editor editing modal.
     */
	$(document).on('click', "[id^='studentsTable'] .editStudBtn", 'tr', function (evt) {
	    var altEditor = $('#studentsTable')[0].altEditor;
	    var tr = $(this).closest('tr');
	    tr.addClass("editing"); // distinguish selected tr
	    altEditor._openEditModal();
	    $('#altEditor-edit-form-' + altEditor.random_id)
			.off('submit')
            .on('submit', function (ievt) {
                ievt.preventDefault();
                ievt.stopPropagation();
                altEditor._editRowData();
            });
	    evt.stopPropagation(); //avoid open "Edit" dialog
	});

    /*
     * Click event hadler on the delete button of the students table.
     * Triggers the opening of the editor delete modal.
     */
	$(document).on('click', "[id^='studentsTable'] .deleteStudBtn", 'tr', function (evt) {
	    var altEditor = $('#studentsTable')[0].altEditor;
	    var tr = $(this).closest('tr');
	    tr.addClass("deleting"); // distinguish selected tr
	    altEditor._openDeleteModal();
	    $('#altEditor-delete-form-' + altEditor.random_id)
			.off('submit')
            .on('submit', function (ievt) {
                ievt.preventDefault();
                ievt.stopPropagation();
                altEditor._deleteRow();
            });
	    evt.stopPropagation(); //avoid open "Edit" dialog
	});

    /*
     * Click event hadler on the add button of the students table.
     * Triggers the opening of the editor add modal.
     */
	$(document).on('click', "[id^='studentsTable'] .addStudBtn", 'tr', function (evt) {
		var altEditor = $('#studentsTable')[0].altEditor;
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
     * Click event hadler on the delete button of the phones table.
     * Triggers the opening of the editor delete modal.
     */
	$(document).on('click', "[id^='phonesTable'] .deletePhoneBtn", 'tr', function (evt) {
	    var altEditor = $('#phonesTable')[0].altEditor;
	    var tr = $(this).closest('tr');
	    tr.addClass("deleting"); // distinguish selected tr
	    altEditor._openDeleteModal();
	    $('#altEditor-delete-form-' + altEditor.random_id)
			.off('submit')
            .on('submit', function (ievt) {
                ievt.preventDefault();
                ievt.stopPropagation();
                altEditor._deleteRow();
            });
	    evt.stopPropagation(); //avoid open "Edit" dialog
	});

    /*
     * Click event hadler on the add button of the students table.
     * Triggers the opening of the editor add modal.
     */
	$(document).on('click', "[id^='phonesTable'] .addPhoneBtn", 'tr', function (evt) {
		var altEditor = $('#phonesTable')[0].altEditor;
		altEditor._openAddModal();
	    $('#altEditor-add-form-' + altEditor.random_id)
	                .off('submit')
	                .on('submit', function (e) {
	                    e.preventDefault();
	                    e.stopPropagation();
	                    altEditor._addRowData();
	                });
	    const id = $(altEditor.modal_selector).find('#id');
	    if (id && !id.val())
	    	id.val(0);
	    const studentRef = $(altEditor.modal_selector).find('#studentRef');
	    if (studentRef && !studentRef.val()) {
			let tr = $('#studentsTable').closest("tr").find(".shown");
		    const row = $('#studentsTable').DataTable().row( tr );
		    if (row && row.data().id)
	    		studentRef.val(row.data().id);
	    }
	    evt.stopPropagation();
	});

    /*
     * Click event hadler on the edit button of the phones table.
     * Triggers the opening of the editor editing modal.
     */
	$(document).on('click', "[id^='phonesTable'] .editPhoneBtn", 'tr', function (evt) {
	    var altEditor = $('#phonesTable')[0].altEditor;
	    var tr = $(this).closest('tr');
	    tr.addClass("editing"); // distinguish selected tr
	    altEditor._openEditModal();
	    $('#altEditor-edit-form-' + altEditor.random_id)
			.off('submit')
            .on('submit', function (ievt) {
                ievt.preventDefault();
                ievt.stopPropagation();
                altEditor._editRowData();
            });
	    evt.stopPropagation(); //avoid open "Edit" dialog
	});

    /*
     * Default ajax send pre event handler to add headers
     * the jqXHR object.
     */
	$(document).ajaxSend(function( event, jqXHR, settings ) {
		jqXHR.setRequestHeader($('meta[name="_csrf_header"]').attr('content'), $('meta[name="_csrf"]').attr('content'));
		jqXHR.setRequestHeader('accept','application/x-protobuf');
	});

    /*
     * on load initializer - builds the main courses table
     */
	$(document).ready(function () {

		const coursesTable = $('#coursesTable').DataTable({
			dom: "Blftipr",
			altEditor: true,
			lengthChange: false,
			processing: true,
			responsive: true,
			deferRender: true,
			ajax: {
				url: $('body').attr('data-courses-url'),
				dataType: "binary",
				responseType: 'arraybuffer',
				cache: true,
				dataSrc: function(data) {
					return proto.protobuf.Courses.deserializeBinary(data).toObject().coursesList;
				}
			},
			rowId: 'id',
		    columns: [
		        { data: "id", type: "readonly", value: "0"},
		        { data: "courseName", type: "text", maxLength: 30, minLength: 5, required: true,
		        	hoverMsg: messages.getMessage('message.name.hover',[5,30]),
					editorOnChange : function(event, alteditor) {
						if (alteditor.isInError()) {
							alteditor.clearFieldError(event.currentTarget.id);
							alteditor.clearFormError();
						}
					}
		        },
		        { data: "studentList.length", type: "hidden" },
		        { data: "studentList", type: "hidden"}
		    ],
		    select: { style: 'single' },
		    columnDefs: [ { target: 3, visible: false, searchable: false } ],
		    order: [ [0, 'asc'] ],
		    buttons: [
		        { text: messages.getMessage('editor.btn.create'), name: 'add' },
		        { extend: 'selected', text: messages.getMessage('editor.btn.edit'), name: 'edit' },
		        { extend: 'selected', text: messages.getMessage('editor.btn.delete'), name: 'delete'}
		    ],
		    onDeleteRow: function(alteditor, rowdata, success, error) {
	        	const selected = alteditor.s.dt.row( { selected: true } );
	        	if (selected.any())
	        		modifyCourse(alteditor, rowdata, 'delete', success, error)
	        },
	        onEditRow: function(alteditor, rowdata, success, error) {
	        	const selected = alteditor.s.dt.row( { selected: true } );
	        	if (selected.any()) {
					rowdata.studentList = selected.data().studentList;
	        		modifyCourse(alteditor, rowdata, 'edit', success, error)
	        	}
	        },
	        onAddRow: function(alteditor, rowdata, success, error) {
	        	modifyCourse(alteditor, rowdata, 'add', success, error)
	        }
		}).on('select', function (e, dt, type, indexes) {
			if (type === 'row') {
				const row = dt.row( { selected: true } );
			    createStudentsTable(row);
			}
	    }).on('deselect', function (e, dt, type, indexes) {
			if (type === 'row') {
			    const row = dt.row(indexes[0]);
			    destroyChild(row);
			}
	    });

	    /*
	     * Click event hadler on the data reset button.
	     * Triggers a reset of the repository on the server,
	     * and of refreshing the course table data.
	     */
		$('#reset-btn').click(function (e) {
			e.preventDefault();
			const selected = coursesTable.row( { selected: true } );
			if (selected.any())
				destroyChild(selected);
			coursesTable.ajax.url($('body').attr('data-reset-url')).load();
		});
});
