<%@ page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<head>
	<meta charset="utf8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="Cache-Control" content="max-age=3600">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <sec:csrfMetaTags />
    <title><spring:message code="title" text="Spring 5 MVC Web App Example with JSP" /></title>
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<link href='<c:url value="/resources/favicon.ico"/>' type="image/x-icon" rel="shortcut icon">
	<!-- DataTables styles -->
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.12.1/css/jquery.dataTables.min.css">
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/responsive/2.3.0/css/responsive.dataTables.min.css">
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/select/1.4.0/css/select.dataTables.min.css">
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/buttons/2.2.3/css/buttons.dataTables.min.css">
    <!-- Editor Styles -->
    <link rel="stylesheet" type="text/css" href='<c:url value="/resources/js/client/DataTable-AltEditor/css/editor.dataTables.css"/>'>
	<!-- Bootstrap 3.4.1 css -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.css">
	<!-- Custom css -->
	<link rel="stylesheet" href="<spring:theme code='stylesheet-bootstrap'/>" type="text/css">
	<link rel="stylesheet" href='<c:url value="/resources/css/custom.css"/>'/>
	<link rel="stylesheet" href='<c:url value="/resources/css/media.css"/>'/>
</head>
<body data-contacts-url='<c:url value="/api/contacts"/>'>
	<div class="container">
		<div class="header clearfix">
			<jsp:include page='../jsp/include/navbar-bootstrap.jsp'>
				<jsp:param name="url" value="/jsp/contacts-bootstrap" />
				<jsp:param name="view" value="contacts-bootstrap-jsp" />
			</jsp:include>
		</div>

        <div class="row marketing" style="direction: <spring:message code='dir' text='ltr'/>">
	       	<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
				<div class="jumbotron">
					<h2><spring:message code='contacts.jumbotron.jsp.header' text='JPA Contacts (JSP)' /></h2>
					<p class="lead"><spring:message code='contacts.jumbotron.lead' text='This example demonstrates 3 nested data tables. The <i>Courses</i> data table is initialized by an <i>ajax</i> request, resulting in a response body containing a <i>google protobuf</i> message. Select any row in the <i>Courses</i> data table, to expose a nested <i>Students</i> sub table. This table is constructed from data originating in the parent data table. You can then click the <i class="dt-control"></i> control (at the right of each <i>student</i> row), to expose a nested <i>Phones</i> data table. Deselecting a row in the parent <i>Courses</i> data table, or selecting another row, will close and destroy the shown nested data tables.' /></p>
					<p><a id="refresh-btn" class="btn btn-lg btn-success" href="#" role="button"><spring:message code='contacts.jumbotron.button' text='JPA Contacts' /></a></p>
				</div>
			</div>
        </div>

		<div class="row marketing">
			<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
				<h2 style="direction: <spring:message code='dir' text='ltr' />"><spring:message code='contacts.table.header' text='Contacts' /></h2>
			</div>
		</div>

		<div class="row marketing">
			<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
			    <div class="form-group">
			        <div class="form-inline">
						<button id="excel" name="excel"
						        class="control-btn btn-md btn-secondary pull-right"
						        onmouseover="swapBtnImage(this,&quot;<spring:theme code='excel.hover.icon'/>&quot;);"
						        onmouseout="swapBtnImage(this,&quot;<spring:theme code='excel.icon'/>&quot;);"
						        onclick="document.location.href='<c:url value="/excel.htm"/>'">
								<img align="left" src="<spring:theme code='excel.icon'/>"/>
						</button>
						<span class="pull-right">&nbsp;</span>
						<button id="PDF" name="PDF"
								class="control-btn btn-md btn-secondary pull-right"
								onmouseover="swapBtnImage(this,&quot;<spring:theme code='pdf.hover.icon'/>&quot;);"
								onmouseout="swapBtnImage(this,&quot;<spring:theme code='pdf.icon'/>&quot;);"
								onclick="document.location.href='<c:url value="/pdf.htm"/>'">
								<img align="left" src="<spring:theme code='pdf.icon'/>"/>
						</button>
						<label class="control-label pull-left"><spring:message code='file.export.options' text='File export options' /></label>
					</div>
				</div>
			</div>
        </div>

        <div class="row marketing spacer">
			<div class="col-12">
				<span>&nbsp;</span>
			</div>
		</div>

		<div class="row marketing">
			<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
				<h2 style="direction: <spring:message code='dir' text='ltr'/>"><spring:message code='contacts.table.header' text='Contacts' /></h2>
				<table id="contactsTable" class="display compact responsive" style="width: 100%">
					<thead>
						<tr>
						    <th></th>
						    <th></th>
						    <th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
						</tr>
					</thead>
	                   <tbody>
					</tbody>
				</table>
			</div>
		</div>

		<jsp:include page='../jsp/include/footer-bootstrap.jsp' />

	</div>
	<!-- jQuery first, then Bootstrap JS -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.js"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.js"></script>
	<!-- DataTables -->
	<script type="text/javascript" charset="utf8" src="https://cdn.datatables.net/1.12.1/js/jquery.dataTables.js"></script>
	<script type="text/javascript" charset="utf8" src="https://cdn.datatables.net/buttons/2.2.3/js/dataTables.buttons.js"></script>
	<script type="text/javascript" charset="utf8" src="https://cdn.datatables.net/select/1.4.0/js/dataTables.select.js"></script>
	<script type="text/javascript" charset="utf8" src="https://cdn.datatables.net/responsive/2.3.0/js/dataTables.responsive.js"></script>
    <!-- Alt Editor -->
    <script type="text/javascript" src='<c:url value="/resources/js/client/DataTable-AltEditor/dataTables.altEditor.free.js"/>'></script>

	<script type="text/javascript">
		(function (window, undefined) {
			const messages = {};
			messages.map_ = new Map();
			messages.map_.set('table.all.results','<spring:message code='table.all.results' text='All'/>');
			messages.map_.set('ssn.hover.message','<spring:message code='ssn.hover.message' text='Expecting xxx-xx-xxxx social security number pattern'/>');
			messages.map_.set('name.hover.message','<spring:message code='name.hover.message' text='Expecting {0} to {1} characters for a name field'/>');
			messages.map_.set('children.hover.message','<spring:message code='children.hover.message' text='name.hover.message=Expecting number between {0} and {1}'/>');
 			messages.map_.set('date.hover.message','<spring:message code='date.hover.message' text='Expecting date in yyyy-MM-dd format'/>');
			messages.map_.set('contact.id','<spring:message code='contact.id' text='ID'/>');
			messages.map_.set('contact.ssn','<spring:message code='contact.ssn' text='SSN'/>');
			messages.map_.set('contact.fname','<spring:message code='contact.fname' text='First Name'/>');
			messages.map_.set('contact.lname','<spring:message code='contact.lname' text='Last Name'/>');
			messages.map_.set('contact.birthDate','<spring:message code='contact.birthDate' text='Birth Date'/>');
			messages.map_.set('contact.is.married','<spring:message code='contact.is.married' text='Married?'/>');
			messages.map_.set('contact.children','<spring:message code='contact.children' text='Kids'/>');
			messages.map_.set('contact.edit','<spring:message code='contact.edit' text='Edit'/>');
			messages.map_.set('contact.add','<spring:message code='contact.add' text='Add'/>');
			messages.map_.set('contact.delete','<spring:message code='contact.delete' text='Delete'/>');
			messages.map_.set('images.editor.create','<c:url value='/resources/images/controls/add.png'/>');
			messages.map_.set('images.editor.edit','<c:url value='/resources/images/controls/edit.png'/>');
			messages.map_.set('images.editor.delete','<c:url value='/resources/images/controls/delete.png'/>');
			messages.map_.set('bad.request.error.msg','<spring:message code='bad.request.error.msg' text='Bad request error (status {0}). Something is wrong!'/>');
			messages.map_.set('not.found.error.msg','<spring:message code='not.found.error.msg' text='The resource was not found (status {0}). Deleted already?'/>');
			messages.map_.set('unauthorized.error.msg','<spring:message code='unauthorized.error.msg' text='(status {0}): You have to login to perform this operation!'/>');

			messages.getMessage = function(key, args) {
	      		let msg = messages.map_.get(key);
	      		if (msg && args)
	      			args.forEach((arg,index) => msg=msg.replace('{' + index + '}',arg));
		       	return msg ? msg : '?' + key + '?';
			}

			window.messages = messages;
		})(window);
    </script>

    <!-- This page script -->
    <script type="text/javascript" src='<c:url value="/resources/js/client/contacts-bootstrap.js"/>'></script>

</body>
</html>