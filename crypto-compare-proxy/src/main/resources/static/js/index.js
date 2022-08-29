    /**
     * This file is unused, unless index.html removes the bottom script code
     * amd includes a link to this file.
     */

    var loadingMsg = $('#details-message').attr('data-loading-msg');
    var errorMsg = $('#details-message').attr('data-error-msg');
    var successMsg = $('#details-message').attr('data-success-msg');
    var defaultMsg = $('#details-message').attr('data-default-msg');

    /**
     * extract the currency symbol from the datatable
     * row and build an ajax url to retrieve details.
     */
    function getDetailsUrl(row) {
    	var url = $('body').attr('data-details-url');
    	var symbol = row.data().symbol;
    	if (url.indexOf('$DUMMY') > -1)
    		url = url.replace('$DUMMY', symbol);
    	else
    		url += symbol;
    	console.debug("getDetailsUrl(row) -> url:", url);
    	return url;
    }

    function getDetails(url) {
    	$.ajax({
    		type: "GET",
    		url: url,
    		beforeSend: function() {
               $('#details-message').text(loadingMsg);
    		   if (!$('#details-panel').hasClass('panel-warning'))
    				$('#details-panel').addClass('panel-warning');
    		},
    		statusCode: {
    			200: function(data, status) {
    			    console.debug("getDetails(url) (200) OK -> status:", status, ",message: ", data.message,",id:", data.data[0].id, ",coinName:", data.data[0].coinName,",symbol: ", data.data[0].symbol, ",algorithm: ", data.data[0].algorithm, ", toUSD:", data.data[0].toUSD);
    			    renderDetails(data, false);
    			}
    		},
    		error: function( jqXHR, status, error ) {
    			var symbol = (url.substring(url.lastIndexOf("/") + 1)).replace(".json","");
    			if (!jqXHR.status || jqXHR.status == 0)
    				console.error("getDetails(url) -> no response from ", url,", Server does not appear to be running!");
    			else if (!jqXHR.status || jqXHR.status == 500)
    				console.error("getDetails(url) (500) error -> No data found for '" + symbol + "'");
    			else
    				console.error("getDetails(url) (error) -> symbol:", symbol ,",status:", jqXHR.status, ",statusText: ", jqXHR.statusText,",error:", error);
    			renderDetails({"symbol": symbol}, true);
    		}
    	});
    }

    function renderDetails(data, isError) {

    	if ($('#details-panel').hasClass('panel-warning'))
    		$('#details-panel').removeClass('panel-warning');
        if ($('#details-panel').hasClass('panel-default'))
        	$('#details-panel').removeClass('panel-default');

    	if (!isError && null != data && data.data) {
    	    //var message = data.message;
    		var json = data.data[0];
    		$("#ccIdSpan").text(json.id ? json.id : "N/A");
    		$("#ccNameSpan").text(json.coinName ? json.coinName : "N/A");
    		$("#ccSymbolSpan").text(json.symbol ? json.symbol : "N/A");
    		$("#ccAlgoSpan").text(json.algorithm ? json.algorithm : "N/A");
    		$("#ccUSDSpan").text(json.toUSD ? json.toUSD : "N/A");
    	}

    	if (isError) {
    		var msg = data && data.symbol ? errorMsg.replace("{0}", data.symbol) : errorMsg;
			$('#details-message').text(msg);
			if (!$('#details-panel').hasClass('panel-danger'))
				$('#details-panel').addClass('panel-danger');
    	} else {
            $('#details-message').text(successMsg.replace("{0}", data.data[0].symbol));
			if (!$('#details-panel').hasClass('panel-primary'))
				$('#details-panel').addClass('panel-primary');
    	}
    }

    function clearDetails() {
    	$("#ccIdSpan").empty();
    	$("#ccNameSpan").empty();
    	$("#ccSymbolSpan").empty();
    	$("#ccAlgoSpan").empty();
    	$("#ccUSDSpan").empty();
    	if ($('#details-panel').hasClass('panel-warning'))
            $('#details-panel').removeClass('panel-warning');
    	if ($('#details-panel').hasClass('panel-primary'))
            $('#details-panel').removeClass('panel-primary');
    	if ($('#details-panel').hasClass('panel-danger'))
            $('#details-panel').removeClass('panel-danger');
        if (!$('#details-panel').hasClass('panel-default'))
            $('#details-panel').addClass('panel-default');
        $('#details-message').text(defaultMsg);
    }

   /**
    * initialize the datatable and create handlers
    * for selection of a row and for retrieving the
    * currency details.
    */
    $(document).ready(function () {

        var masterUrl = $('body').attr('data-master-url');
    	var table = $('#datatable').DataTable({
    		ajax: { url: masterUrl, async: true, cache: true },
    		columns: [ { "data": "coinName", "searchable": false}, { "data": "symbol" }, { "data": "algorithm" } ],
    		order: [ [1, "asc"], [2, "asc"] ],
    		//columns: [ { "data": "symbol" }, { "data": "algorithm" } ],
    		//order: [ [0, "asc"], [1, "asc"] ],
    		lengthMenu: [ [-1,4000,2000,1000,500,250,50], ["All",4000,2000,1000,500,250,50] ],
    		paging: true,
    		scrollY: "300px",
    		scrollCollapse: true,
    		autoWidth: false,
    		deferRender: true,
    		select: true
    	});

       /**
        * make sure only onerow can be selected, extract
        * the symbol from the selected row and ajax to
        * the API url to retrieve the currency details.
        */
       table.on( 'click', 'tbody tr', function () {
    	    clearDetails();
    	    if ($(this).hasClass('selected')) {
                table.$('tr.selected').removeClass('selected');
            } else {
            	table.$('tr.selected').removeClass('selected');
                $(this).addClass('selected');
                var row = $('#datatable').DataTable().row(this);
                var url = getDetailsUrl(row);
    			getDetails(url);
            }
       });

       /**
        * attach on-click handler to the 'refresh' button.
        */
       $('#refresh-btn').click(function (e){
    	   e.preventDefault();
    	   clearDetails();
    	   table.search("").draw();
    	   table.page.len(-1).draw();
    	   table.order([ [1, "asc"], [2, "asc"] ]);
    	   //table.order([ [0, "asc"], [1, "asc"] ]);
    	   table.clear();
    	   table.ajax.reload();
       });
    });
