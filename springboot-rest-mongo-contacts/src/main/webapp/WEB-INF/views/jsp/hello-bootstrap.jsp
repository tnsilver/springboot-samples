<%@ page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<sec:csrfMetaTags />
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<title><spring:message code="title" text="Spring 5 MVC Web App Example with JSP" /></title>
	<link href='<c:url value="/resources/favicon.ico"/>' type="image/x-icon" rel="shortcut icon">
	<!-- Bootstrap 3.4.1 css -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.css">
	<!-- Custom css -->
	<link rel="stylesheet" href="<spring:theme code='stylesheet-bootstrap'/>" type="text/css">
	<link rel="stylesheet" href='<c:url value="/resources/css/custom.css"/>'/>
	<link rel="stylesheet" href='<c:url value="/resources/css/media.css"/>'/>
</head>
<body style="direction: '<spring:message code='dir' text='ltr' />'" dir="<spring:message code='dir' text='ltr' />" data-date-pattern="<spring:message code='date.pattern' text='MM/dd/yyyy' />">
	<spring:message code='date.pattern' var="pattern" scope="page" />

	<div class="container">
		<div class="header clearfix">
			<jsp:include page='../jsp/include/navbar-bootstrap.jsp'>
				<jsp:param name="url" value="/jsp/hello-bootstrap" />
				<jsp:param name="view" value="hello-bootstrap-jsp" />
			</jsp:include>
		</div>

		<div class="row marketing">
			<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
		        <h2><spring:message code="hello.jsp.title" text="Hello World, Spring JSP REST" /></h2>
		        <p id="greeting" data-url="<c:url value='/api/hello'/>" data-firstName="${firstName}" data-lastName="${lastName}">Welcome <sec:authentication property="principal" /></p>
		        <h2><spring:message code="page.hello.form.header" text="Names to greet" /></h2>
			</div>
		</div>

		<div class="row marketing">
			<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
				<form id="helloForm" class="form-horizontal" action="<c:url value='/api/hello'/>" autocomplete="on" data-self="">
					<div class="form-group">
						<label for="firstName" class="col-sm-2 control-label"><spring:message code="page.hello.form.firstName" text="First name" /></label>
						<div class="col-sm-4">
								<input id="firstName"
									name="firstName"
									class="form-control"
									type="text"
									autocomplete="on"
									placeholder="<spring:message code="page.hello.form.firstName.placeholder" text="John"/>"
									pattern=".{2,25}"
									oninvalid="this.setCustomValidity('${nameErrMsg}');"
									oninput="setCustomValidity('')" />
							<span id="fnameError" class="error"></span>
						</div>
					</div>
					<div class="form-group">
						<label for="lastName" class="col-sm-2 control-label"><spring:message code="page.hello.form.lastName" text="Last name" /></label>
						<div class="col-sm-4">
								<input id="lastName"
									class="form-control"
									name="lastName"
									type="text"
									autocomplete="on"
									placeholder="<spring:message code="page.hello.form.lastName.placeholder" text="Doe"/>"
									pattern=".{2,25}"
									oninvalid="this.setCustomValidity('${nameErrMsg}');"
									oninput="setCustomValidity('')" />
							<span id="lnameError" class="error"></span>
						</div>
					</div>
					<div class="form-group">
					    <div class="col-sm-offset-2 col-sm-4">
							<button class="btn btn-default" type="submit" name="Send"><spring:message code='hello.btn.save' text='Save' /></button>
							<button class="btn btn-default reset" type="button" id="reset"><spring:message code='hello.btn.reset' text='Clear' /></button>
						</div>
					</div>
				</form>
			</div>
		</div>

		<div class="row marketing">
			<div class="col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12">
				<p><spring:message code="hello.locale" text="Current locale:" />&nbsp;${pageContext.response.locale}</p>
			</div>
		</div>

		<jsp:include page='../jsp/include/footer-bootstrap.jsp' />
	</div>

	<!-- jQuery first, then Bootstrap JS -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.js"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.js"></script>
	<!-- Custom JavaScript -->
	<script type="text/javascript" src='<c:url value="/resources/js/client/common.js"/>'></script>
	<script type="text/javascript" src='<c:url value="/resources/js/client/hello.js"/>'></script>

</body>
</html>