<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="org.apache.commons.io.FilenameUtils"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
    <c:url var="url" value='<%= request.getParameter("url") %>'/>
    <c:set var="view" value='<%= request.getParameter("view") %>'/>
    <c:set var="action" value='<%= request.getParameter("action") %>'/>
    <%-- <c:out value="${url}"/> <c:out value="${view}"/> --%>
	<nav class="navbar navbar-inverse">
	<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand" href="https://github.com/tnsilver/springboot-samples"><spring:message code='website.name' text="SpringBootREST"/></a>
				<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
			      <span class="sr-only">Toggle navigation</span>
			      <span class="icon-bar"></span>
			      <span class="icon-bar"></span>
			      <span class="icon-bar"></span>
		    	</button>
			</div>
			<div id="navbar" class="navbar-collapse collapse">
	        <ul class="nav navbar-nav">
				<li><a href='<c:url value="/html/home-bootstrap"/>'><spring:message code='page.home' text="Home"/></a></li>
				<li <c:if test="${view eq 'hello-bootstrap-jsp'}">class="active"</c:if>><a href='<c:url value="/jsp/hello-bootstrap"/>'><spring:message code='page.hello.jsp' text="Hello JSP"/></a></li>
				<li <c:if test="${view eq 'hello-bootstrap-html'}">class="active"</c:if>><a href='<c:url value="/html/hello-bootstrap"/>'><spring:message code='page.hello.html' text="Hello HTML"/></a></li>
				<li <c:if test="${view eq 'contacts-bootstrap-jsp'}">class="active"</c:if>><a href='<c:url value="/jsp/contacts-bootstrap"/>'><spring:message code='page.contacts.jsp' text="Contacts JSP"/></a></li>
	            <li <c:if test="${view eq 'contacts-bootstrap-html'}">class="active"</c:if>><a href='<c:url value="/html/contacts-bootstrap"/>'><spring:message code='page.contacts.bootstrap.html' text="Contacts HTML"/></a></li>
	            <li class="dropdown">
	                <a class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0)"><spring:message code='page.language' text="Language"/><span class="caret"></span></a>
	                <ul class="dropdown-menu">
		                <li><a href='<c:out value="${url}"/>?lang=he_IL'><img src='<c:url value="/resources/images/flags/iw.png"/>'>&nbsp;<span><spring:message code='lang.iw' text="Hebrew"/></span></a></li>
		                <li><a href='<c:out value="${url}"/>?lang=en_US'><img src='<c:url value="/resources/images/flags/en.png"/>'>&nbsp;<span><spring:message code='lang.en' text="English"/></span></a></li>
	                </ul>
	            </li>
	            <li class="dropdown">
	                <a class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0)"><spring:message code='theme.change' text="Theme"/><span class="caret"></span></a>
	                <ul class="dropdown-menu">
	                    <li><a href='<c:out value="${url}"/>?theme=spring'><img src='<c:url value="/resources/images/spring.png"/>'>&nbsp;<span><spring:message code='theme.spring' text="Spring"/></span></a></li>
	                    <li><a href='<c:out value="${url}"/>?theme=wheat'><img src='<c:url value="/resources/images/wheat.png"/>'>&nbsp;<span><spring:message code='theme.wheat' text="Wheat"/></span></a></li>
	                </ul>
	            </li>
			</ul>
			<ul class="nav navbar-nav navbar-right">
			    <c:if test="${not empty pageContext.request.remoteUser}">
			    <li>
	               <a href="#" onclick="document.forms['logoutForm'].submit(); return false;"><spring:message code='page.logout' text="Logout"/>&nbsp;${pageContext.request.remoteUser}</a>
	               <form id="logoutForm" action='<%= application.getContextPath() + "/logout" %>' method="post">
	                  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	               </form>
			    </li>
				</c:if>
	            <c:if test="${empty pageContext.request.remoteUser}">
		            <li <c:if test="${view eq 'login-bootstrap'}">class="active"</c:if>>
		               <a href="<%= application.getContextPath() + "/login-bootstrap" %>"><spring:message code='page.login' text="Login"/></a>
		            </li>
	            </c:if>
				<li><a href='<%= application.getContextPath() + "/source/" + FilenameUtils.getBaseName(request.getParameter("url")) %>'><spring:message code='page.source' text="Source"/></a></li>
			</ul>
			</div>
	</div>
	</nav>
