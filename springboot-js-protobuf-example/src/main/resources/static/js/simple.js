	/**
	 *
	 * simple.js
	 *
	 * @description. javascript code for the simple.html template
	 * @version 1.0
	 * @author T.N.Silverman <tnsilver@gmail.com>
	 *
	 */

    /*
     * Formats an HTML string representing a child table,
     * out of the initial row data d obtained from the parent
     * table. For example by using the API: row.data()
     * @param {object}
     *            d the row data
     * @returns {string}
     *            the formatted html string
     */
	function format(d) {
	    // `d` is the original data object for the row
	    var html = '<table cellpadding="5" cellspacing="0" border="0"><th colspan="4">Students:</th>';
	    d.studentList.forEach((student, index) => {
	    	html += '<tr>' +
	    	        	'<td>' + student.id + '</td>' +
	    	        	'<td>Name:&nbsp;' + student.firstName + ' ' + student.lastName + '</td>' +
	    	        	'<td>' + student.email + '</td><td>&nbsp;</td>' +
	    	        '</tr>';
	    	if (student.phoneList.length) {
		    	student.phoneList.forEach((phone, index) => {
		    		html += '<tr>' +
		    		        	'<td>' + (index == 0 ? '<b>Phones:</b>' : '&nbsp;') + '</td>' +
		    		        	'<td>' + phone.id + '</td>' +
		    					'<td>' + getKey(proto.protobuf.Student.PhoneType,phone.type) + '</td>' +
		    		        	'<td>Number:&nbsp;' + phone.number + '</td>' +
		    		        	'<td>&nbsp;</td>' +
		    		        '</tr>';
		    	});
	    	}

	    });
	    html += '</table>';
	    return html;
	}

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
     * Executes a get request (native javascript, not $ajax by jquery).
     * @param {function}
     *            func the function that accepts the XMLHttpRequest response and processes it
     */
	function execGetRequest(func) {
		var xhr = new XMLHttpRequest();
    	var url = $('body').attr('data-courses-url');
    	xhr.open("GET", url);
    	xhr.setRequestHeader($('meta[name="_csrf_header"]').attr('content'), $('meta[name="_csrf"]').attr('content'));
    	xhr.setRequestHeader("accept","application/x-protobuf");
    	xhr.responseType = "arraybuffer";
    	xhr.onload = function(evt) { func(xhr.response); },
    	xhr.send(null);
	}

    /*
     * on load initializer - builds the main courses table
     */
	$(document).ready(function () {

		// initialize the data table
		execGetRequest( function(response) {
			var message = proto.protobuf.Courses.deserializeBinary(response);
    	    var data = message.toObject().coursesList;
    	    var table = $('#datatable').DataTable({
    	        data: data,
    	        responsive: true,
    	        createdRow: function( row, data, dataIndex ) {
    	        	if (data.studentList.length < 1) {
    	                $('td:eq(0)', row).removeClass('dt-control');
    	                $('td:eq(0)', row).addClass('dt-control-nochild');
    	        	}
    	        },
    	        columns: [
    	        	{ data: null, className: 'dt-control', orderable: false, defaultContent: ''},
    	            { data: "id" },
    	            { data: "courseName" },
    	            { data: "studentList.length" }
    	        ],
    	        order: [ [1, 'asc'] ]
    	    }).on('click', 'td.dt-control', function () {
    	        var tr = $(this).closest('tr');
    	        var row = table.row(tr);
    	        if (row.child.isShown()) {
    	            row.child.hide(); // this row is already open - close it
    	            tr.removeClass('shown');
    	        } else {
    	            row.child(format(row.data())).show(); // open this row
    	            tr.addClass('shown');
    	        }
    	    });
    	});

	    /*
	     * Click event hadler on the data refresh button.
	     * Triggers clearing the data table and repopulating it
	     * with fresh data from the server.
	     */
    	$('#refresh-btn').click(function (e) {
    		e.preventDefault();
    		execGetRequest( function(response) {
   	    		var message = proto.protobuf.Courses.deserializeBinary(response);
	    	    var data = message.toObject().coursesList;
	    		var table = $('#datatable').DataTable();
	    		table.clear();
    	    	table.rows.add(data).draw();
   	    	});
       });

    });
