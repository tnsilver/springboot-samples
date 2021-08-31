<%@ page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<sec:csrfMetaTags />
	<title><spring:message code="title" text="Spring 5 MVC Web App Example with JSP" /></title>
	<link rel="stylesheet" href="<spring:theme code='stylesheet'/>" type="text/css" />
	<script type="text/javascript" src='<c:url value="/resources/js/plugins/jquery-3.3.1.min.js"/>'></script>
	<script type="text/javascript" src='<c:url value="/resources/js/client/common.js"/>'></script>
	<script type="text/javascript" src='<c:url value="/resources/js/client/hello.js"/>'></script>
	<link href='<c:url value="/resources/favicon.ico"/>' type="image/x-icon" rel="shortcut icon">
</head>
<body style="direction: '<spring:message code='dir' text='ltr' />'" dir="<spring:message code='dir'
	  text='ltr' />"
	data-date-pattern="<spring:message code='date.pattern'
	  text='MM/dd/yyyy' />"
	data-language="${pageContext.response.locale.language}" data-context-path="<%=request.getContextPath()%>">
	<spring:message code='date.pattern' var="pattern" scope="page" />
	<div class="wrapper">
		<jsp:include page='../jsp/include/navbar.jsp'>
			<jsp:param name="url" value="/jsp/hello" />
			<jsp:param name="view" value="hello-jsp" />
		</jsp:include>
		<div class="content">
			<h2><spring:message code="hello.jsp.title" text="Hello World, Spring JSP REST" /></h2>
			<p id="greeting" data-url="<c:url value='/api/hello'/>" data-firstName="${firstName}" data-lastName="${lastName}">Welcome <sec:authentication property="principal" /></p>
			<h2><spring:message code="page.hello.form.header" text="Names to greet" /></h2>
			<form id="helloForm" action="<c:url value='/api/hello'/>" autocomplete="on" method="post" data-self="">
				<table class="vertical">
					<tr>
						<td><label><spring:message code="page.hello.form.firstName" text="First name" /></label></td>
						<td>
							<spring:message var="nameErrMsg" code='invalid.name' text='invalid name' />
							<input id="firstName"
								type="text"
								autocomplete="on"
								placeholder="John"
								placeholder="<spring:message code="page.hello.form.firstName.placeholder" text="John"/>"
								pattern=".{2,25}"
								oninvalid="this.setCustomValidity('${nameErrMsg}');"
								oninput="setCustomValidity('')" />
						</td>
					</tr>
					<tr style="display: none;">
						<td></td>
						<td class="error"><span id="firstNameError" class="error">First Name Error</span></td>
					</tr>
					<tr>
						<td><label><spring:message code="page.hello.form.lastName" text="Last name" /></label></td>
						<td>
							<spring:message var="nameErrMsg" code='invalid.name' text='invalid name' />
							<input id="lastName"
								type="text"
								autocomplete="on"
								placeholder="John"
								placeholder="<spring:message code="page.hello.form.lastName.placeholder"
								text="Doe"/>"
								pattern=".{2,25}"
								oninvalid="this.setCustomValidity('${nameErrMsg}');"
								oninput="setCustomValidity('')" />
						</td>
					</tr>
					<tr style="display: none;">
						<td></td>
						<td class="error"><span id="lastNameError" class="error">Last Name Error</span></td>
					</tr>
					<tr>
						<td>
							<button type="submit" name="Save" value="<spring:message code='hello.btn.save' text='Greet' />">
								<spring:message code='hello.btn.save' text='Save' />
							</button>
						</td>
						<td>
							<button type="button" class="reset" value="<spring:message code='hello.btn.reset' text='Clear' />">
								<spring:message code='hello.btn.reset' text='Clear' />
							</button>
						</td>
					</tr>
				</table>
			</form>
			<p>
				<spring:message code="hello.locale" text="Current locale:" />&nbsp;${pageContext.response.locale}
			</p>
		</div>
		<jsp:include page='../jsp/include/footer.jsp' />
	</div>
</body>
</html>