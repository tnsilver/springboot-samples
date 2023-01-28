<%@ page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<c:set var="action" scope="page" value='<%=request.getParameter("action") == null ? "add" : request.getParameter("action")%>'></c:set>
<c:set var="self" scope="page" value='<%=request.getParameter("self") == null ? null : request.getParameter("self")%>'></c:set>
<% String action = request.getParameter("action") == null ? "" : request.getParameter("action"); %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <sec:csrfMetaTags />
     <c:choose>
	     <c:when test="${action == 'edit'}"><title><spring:message code="page.contact.title.edit" text="Edit Contact" /></title></c:when>
	     <c:when test="${action == 'delete'}"><title><spring:message code="page.contact.title.delete" text="Delete Contact" /></title></c:when>
	     <c:otherwise><title><spring:message code="page.contact.title.add" text="Add Contact" /></title></c:otherwise>
    </c:choose>
    <link rel="stylesheet" href="<spring:theme code='stylesheet'/>" type="text/css" />
    <script type="text/javascript" src='<c:url value="/resources/js/plugins/jquery-3.3.1.min.js"/>'></script>
    <script type="text/javascript" src='<c:url value="/resources/js/client/common.js"/>'></script>
    <script type="text/javascript" src='<c:url value="/resources/js/client/contact.js"/>'></script>
    <link href='<c:url value="/resources/favicon.ico"/>' type="image/x-icon" rel="shortcut icon">
</head>
<body style="direction: '<spring:message code='dir' text='ltr' />'" dir="<spring:message code='dir' text='ltr' />"
      data-date-pattern="<spring:message code='date.pattern' text='yyyy-MM-dd' />"
      data-language="${pageContext.response.locale.language}"
      data-context-path="<%=request.getContextPath()%>"
      data-next-page="<c:url value='/jsp/contacts'/>"
      data-404-page="<c:url value='/error'/>"
      data-self="${self}"
      data-action="${action}">
    <spring:message code='date.pattern' var="pattern" scope="page" />
    <div class="wrapper">
        <jsp:include page='../jsp/include/navbar.jsp'>
            <jsp:param name="url" value="/jsp/contact" />
            <jsp:param name="view" value="contact-jsp" />
        </jsp:include>
        <div class="content">
             <h2><spring:message code='page.contact.jsp' text='Modify Contact' /></h2>
		     <c:choose>
			     <c:when test="${action == 'edit'}"><h3><spring:message code="page.contact.title.edit" text="Edit Contact" /></h3></c:when>
			     <c:when test="${action == 'delete'}"><h3><spring:message code="page.contact.title.delete" text="Delete Contact" /></h3></c:when>
			     <c:otherwise><h3><spring:message code="page.contact.title.add" text="Add Contact" /></h3></c:otherwise>
		    </c:choose>
            <c:url var="api" value="/api/contacts/" />
            <form id="contactForm"
                  action="${api}"
                  data-self="${self}"
                  data-action="${action}"
                  autocomplete="on"
                  method="post">
                <table class="vertical">
                   <%--  <tr>
                        <td><label><spring:message code='contact.id' text='ID' /></label></td>
                        <td><input id="id" type="text" readonly="readonly" /></td>
                    </tr> --%>
                    <tr style="display: none;">
                        <td></td>
                        <td class="error"><span id="idError" class="error">ID Error</span></td>
                    </tr>
                    <tr>
                        <td><label><spring:message code='contact.ssn' text='SSN' /></label></td>
                        <spring:message var="ssnErrMsg" code='invalid.ssn' text='invalid name' />
                        <td>
                        	<input type="text" id="ssn" autocomplete="on"
                        	       placeholder="<spring:message code='contact.ssn.placeholder'
                        	       text='123-45-6789' />" required pattern="[0-9]{3}-[0-9]{2}-[0-9]{4}"
                        	       <% if (action.equals("delete")) {%> disabled="disabled" <%}%>
                        	       oninvalid="this.setCustomValidity('${ssnErrMsg}')"
                        	       oninput="setCustomValidity('')"/>
                        </td>
                    </tr>
                    <tr style="display: none;">
                        <td></td>
                        <td class="error"><span id="ssnError" class="error">SSN Error</span></td>
                    </tr>
                    <tr>
                        <td><label><spring:message code='contact.fname' text='First Name' /></label></td>
                        <spring:message var="nameErrMsg" code='invalid.name' text='invalid name' />
                        <td>
                        	<input type="text" id="firstName" autocomplete="on" required pattern=".{2,25}"
                        	       <% if (action.equals("delete")) {%> disabled="disabled" <%}%>
                        	       oninvalid="this.setCustomValidity('${nameErrMsg}');"
                        	       oninput="setCustomValidity('')" />
                        </td>
                    </tr>
                    <tr style="display: none;">
                        <td></td>
                        <td class="error"><span id="firstNameError" class="error">First Name Error</span></td>
                    </tr>
                    <tr>
                        <td><label><spring:message code='contact.lname' text='Last Name' /></label></td>
                        <spring:message var="nameErrMsg" code='invalid.name' text='invalid name' />
                        <td>
                        	<input type="text" id="lastName" autocomplete="on" required pattern=".{2,25}"
                        	       <% if (action.equals("delete")) {%> disabled="disabled" <%}%>
                        	       oninvalid="this.setCustomValidity('${nameErrMsg}');"
                        	       oninput="setCustomValidity('')" />
                        </td>
                    </tr>
                    <tr style="display: none;">
                        <td></td>
                        <td class="error"><span id="lastNameError" class="error">Last Name Error</span></td>
                    </tr>
                    <tr>
                        <td><label><spring:message code='contact.birthDate' text='Birth Date' /></label></td>
                        <spring:message var="dobErrMsg" code='invalid.birthDate' text='invalid date' />
                        <td>
                        	<input type="text" id="birthDate" autocomplete="on" placeholder="<spring:message
                        	       code='contact.birthDate.placeholder'  text='1990-12-31' />"
                        	       required pattern="[0-9]{4}-[0-9]{2}-[0-9]{2}"
                        	       <% if (action.equals("delete")) {%> disabled="disabled" <%}%>
                        	       oninvalid="this.setCustomValidity('${dobErrMsg}')" oninput="setCustomValidity('')" />
                        </td>
                    </tr>
                    <tr style="display: none;">
                        <td></td>
                        <td class="error"><span id="birthDateError" class="error">Birth Date Error</span></td>
                    </tr>
                    <tr>
                        <td><label><spring:message code='contact.married' text='Married' /></label></td>
                        <td><input id="married"
                                   <% if (action.equals("delete")) {%> disabled="disabled" <%}%>
                                   type="checkbox"/></td>
                    </tr>
                    <tr style="display: none;">
                        <td></td>
                        <td class="error"><span id="marriedError" class="error">Married Error</span></td>
                    </tr>
                    <tr>
                        <td><label><spring:message code='contact.children' text='No. of Kids' /></label></td>
                        <spring:message var="childrenErrMsg" code='invalid.children' text='invalid num of children' arguments="0,99" />
                        <td>
                        	<input type="number" autocomplete="on" min="0" max="99" id="children"
                        	       required="required"
                        	       <% if (action.equals("delete")) {%> disabled="disabled" <%}%>
                        	       oninvalid="this.setCustomValidity('${childrenErrMsg}');"
                        	       oninput="setCustomValidity('')"/>
                        </td>
                    </tr>
                    <tr style="display: none;">
                        <td></td>
                        <td class="error"><span class="error" id="childrenError">Children Error</span></td>
                    </tr>
                    <tr>
                        <td>
                            <spring:message code='contact.btn.delete' text='Delete' var="delete" scope="page"/>
                            <spring:message code='contact.btn.save' text='Save' var="save" scope="page"/>
                            <button type="submit"
                                    name="Save"
                                    value='${action == "delete" ? delete : save}'>
                                    ${action == "delete" ? delete : save}
                            </button>
                        </td>
                        <td>
                            <spring:message code='contact.btn.reset' text='Clear' var="reset" scope="page"/>
                            <spring:message code='contact.btn.cancel' text='Cancel' var="cancel" scope="page"/>
                            <button type="button" class="reset" id="reset" value='${action == "add" ? reset : cancel}'>
                            		${action == "add" ? reset : cancel}
                            </button>
                        </td>
                    </tr>
                    <tr class="message">
                        <td class="message" colspan="2"><p data-message="<spring:message code='contact.saved'/>"></p></td>
                    </tr>
                </table>
                <sec:csrfInput />
            </form>
        </div>
        <jsp:include page='../jsp/include/footer.jsp' />
    </div>
</body>
</html>