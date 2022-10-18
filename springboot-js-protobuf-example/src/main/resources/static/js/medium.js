	/**
	 *
	 * simple.js
	 *
	 * @description. javascript code for the intermediate.html template
	 * @version 1.0
	 * @author T.N.Silverman <tnsilver@gmail.com>
	 *
	 */

    var subTab1 = '<table id="studentsTable" class="display compact responsive" style="width: 85%, padding-left: 15%">' +
					'<thead>' +
						'<tr>' +
							'<th></th>' +
							'<th>' + messages.getMessage('detailstable.th.student.id') + '</th>' +
							'<th>' + messages.getMessage('detailstable.th.student.fname') + '</th>' +
							'<th>' + messages.getMessage('detailstable.th.student.lname') + '</th>' +
							'<th>' + messages.getMessage('detailstable.th.student.email') + '</th>' +
							'<th>' + messages.getMessage('detailstable.th.student.phonenums') + '</th>' +
							'<th></th>' +
						'</tr>' +
					'</thead>' +
					'<tbody></tbody>'+
				'</table>';

	var subTab2 = '<table id="phonesTable" class="display compact responsive" style="width: 70%, padding-left: 15%">' +
					'<thead>' +
						//'<tr colspan="3">Phones</tr>' +
						'<tr>' +
							'<th>' + messages.getMessage('subtable.th.student.phone.id') + '</th>' +
							'<th>' + messages.getMessage('subtable.th.student.phone.type') + '</th>' +
							'<th>' + messages.getMessage('subtable.th.student.phone.number') + '</th>' +
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
	function getKey(map, val) { return Object.keys(map).find(key => map[key] === val); }

    /*
     * Builds and displays the phones DataTable
     * from the given data row object.
     * @param {Object}
     *            row the DataTable row object, either selected
     *            or found on the parent students table.
     */
	function createPhonesTable ( row ) {
	    var table = $(subTab2);
		row.child(table).show();
		var phonesTable = table.DataTable( {
		    data: row.data().phoneList,
		    searching: false,
		    info: false,
		    lengthChange: false,
		    paging: false,
		    responsive: true,
			columns: [ { data: 'id' },
				       { data: 'type',
						 render: function (data) { return getKey(proto.protobuf.Student.PhoneType,data);}
			           },
					   { data: 'number' }
			]
		});//.on( 'destroy.dt', function ( e, settings ) { console.debug("data table", e.target.id, "destroyed!"); });
	}

    /*
     * Builds and displays the students DataTable
     * from the given data row object.
     * @param {Object}
     *            row the DataTable row object, selected on the parent courses table.
     */
	function createStudentsTable ( row ) {
	    // This is the table we'll convert into a DataTable
        var courseId = row.data().id;
	    var url = $('body').attr('data-courses-url') + '/' + courseId + '/students' ///courses/{id}/students
	    var table = $(subTab1);
	    if (row.data().studentList.length > 0) {
		    // Display the child row
			row.child( table ).show();
	    }
	    // Initialise as a DataTable
		var studentsTable = table.DataTable( {
		    // to use ajax: comment the next row, uncomment the ajax section
		    data: row.data().studentList,
			/* ajax: {
				url: url,
				dataType: "binary",
				responseType: 'arraybuffer',
				cache: true,
				dataSrc: function(data) {
						return proto.protobuf.Students.deserializeBinary(data).toObject().studentsList;
				}
			}, */
			createdRow: function( row, data, dataIndex ) {
	        	if (data.phoneList.length < 1) {
	                $('td:eq(0)', row).removeClass('dt-control');
	                $('td:eq(0)', row).addClass('dt-control-nochild');
	        	}
	        },
			responsive: true,
		    columns: [	{ data: null, className: 'dt-control details', orderable: false, defaultContent: ''},
						{ data: 'id' },
						{ data: 'firstName' },
						{ data: 'lastName' },
						{ data: 'email' },
						{ data: "phoneList.length" },
						{ data: "phoneList"}
			],
		    columnDefs: [{ target: 6, visible: false, searchable: false }],
		    order: [ [1, 'asc'] ]
		}); //.on( 'destroy.dt', function ( e, settings ) { console.debug("data table", e.target.id, "destroyed!"); });

	    /*
	     * click event hadler on the control td of the students table
	     * triggers the build or destruction of the sub phones table.
	     */
		$('#studentsTable tbody').on('click', 'td.dt-control.details', function () {
		    var tr = $(this).closest('tr');
		    var row = studentsTable.row( tr );
		    if ( row.child.isShown() ) {
		        destroyChild(row);
		        tr.removeClass('shown');
		    } else {
		        createPhonesTable(row);
		        tr.addClass('shown');
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
		if (row.child()) {
			var table = $("table", row.child());
		    table.detach();
		    table.DataTable().destroy();
		    row.child.hide();
		}
	}

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

		var coursesTable = $('#coursesTable').DataTable({
			ajax: {
				url: $('body').attr('data-courses-url'),
				dataType: "binary",
				responseType: 'arraybuffer',
				cache: true,
				dataSrc: function(data) { return proto.protobuf.Courses.deserializeBinary(data).toObject().coursesList; }
			},
			responsive: true,
		    columns: [
		        { data: "id" },
		        { data: "courseName" },
		        { data: "studentList.length" },
		        { data: "studentList" }
		    ],
		    select: { style: 'single' },
		    columnDefs: [{ target: 3, visible: false, searchable: false }],
		    order: [ [0, 'asc'] ]
		}).on( 'select', function (e, dt, type, indexes) {
			if (type === 'row') {
			    var row = dt.row( { selected: true } );
			    if (row.data().studentList.length < 1)
			    	row.deselect();
			    else
			    	createStudentsTable(row);
			}
	    }).on( 'deselect', function (e, dt, type, indexes) {
			if (type === 'row') {
			    var row = dt.row(indexes[0]);
			    destroyChild(row);
			}
	    });

	    /*
	     * Click event hadler on the data refresh button.
	     * Triggers clearing the data table and repopulating it
	     * with fresh data from the server.
	     */
		$('#refresh-btn').click(function (e) {
			e.preventDefault(); coursesTable.ajax.reload();
		});
	});
