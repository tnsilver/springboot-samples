<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="org.apache.commons.io.FilenameUtils"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
    <c:url var="url" value='<%= request.getParameter("url") %>'/>
    <c:set var="view" value='<%= request.getParameter("view") %>'/>
    <c:set var="action" value='<%= request.getParameter("action") %>'/>
    <%-- <c:out value="${url}"/> <c:out value="${view}"/> --%>
    <div class="nav">
        <ul>
            <li><a href='<c:url value="/html/home"/>'><spring:message code='page.home' text="Home"/></a></li>
            <li><a href='<c:url value="/jsp/hello"/>' <c:if test="${view eq 'hello-jsp'}">class="active"</c:if>><spring:message code='page.hello.jsp' text="Hello JSP"/></a></li>
            <li><a href='<c:url value="/html/hello"/>' <c:if test="${view eq 'hello-html'}">class="active"</c:if>><spring:message code='page.hello.html' text="Hello HTML"/></a></li>
            <li><a href='<c:url value="/jsp/contacts"/>' <c:if test="${view eq 'contacts-jsp'}">class="active"</c:if>><spring:message code='page.contacts.jsp' text="Contacts JSP"/></a></li>
            <c:choose>
	            <c:when test="${action == 'edit'}"><li><a href='<c:url value="/html/contact"/>' <c:if test="${view eq 'contact-jsp'}">class="active"</c:if>><spring:message code="page.contact.edit.jsp" text="Edit"/></a></li>"></c:when>
	            <c:when test="${action == 'delete'}"><li><a href='<c:url value="/html/contact"/>' <c:if test="${view eq 'contact-jsp'}">class="active"</c:if>><spring:message code="page.contact.delete.jsp" text="Delete"/></a></li>"></c:when>
	            <c:otherwise><li><a href='<c:url value="/jsp/contact"/>' <c:if test="${view eq 'contact-jsp'}">class="active"</c:if>><spring:message code="page.contact.add.jsp" text="Add"/></a></li>"></c:otherwise>
            </c:choose>
            <li><a href='<c:url value="/html/contacts"/>'           <c:if test="${view eq 'contacts-html'}">class="active"</c:if>><spring:message code='page.contacts.html' text="Contacts HTML"/></a></li>
            <li><a href='<c:url value="/html/contacts-bootstrap"/>' <c:if test="${view eq 'contacts-bootstrap-html'}">class="active"</c:if>><spring:message code='page.contacts.bootstrap.html' text="Contacts (bootstrap)"/></a></li>
            <c:choose>
	            <c:when test="${action == 'edit'}"><li><a href='<c:url value="/html/contact"/>' <c:if test="${view eq 'contact-html'}">class="active"</c:if>><spring:message code="page.contact.edit" text="Edit"/></a></li>"></c:when>
	            <c:when test="${action == 'delete'}"><li><a href='<c:url value="/html/contact"/>' <c:if test="${view eq 'contact-html'}">class="active"</c:if>><spring:message code="page.contact.delete" text="Delete"/></a></li>"></c:when>
	            <c:otherwise><li><a href='<c:url value="/html/contact"/>' <c:if test="${view eq 'contact-html'}">class="active"</c:if>><spring:message code="page.contact.add" text="Add"/></a></li>"></c:otherwise>
            </c:choose>
            <li class="dropdown">
                <a href="javascript:void(0)" class="dropbtn"><spring:message code='page.language' text="Language"/></a>
                <div class="dropdown-content">
                    <a href='<c:out value="${url}"/>?lang=he_IL'><img src='<c:url value="/resources/images/flags/iw.png"/>'>&nbsp;<span><spring:message code='lang.iw' text="Hebrew"/></span></a>
                    <a href='<c:out value="${url}"/>?lang=en_US'><img src='<c:url value="/resources/images/flags/en.png"/>'>&nbsp;<span><spring:message code='lang.en' text="English"/></span></a>
                </div>
            </li>
            <li class="dropdown">
                <a href="javascript:void(0)" class="dropbtn"><spring:message code='theme.change' text="Theme"/></a>
                <div class="dropdown-content">
                    <a href='<c:out value="${url}"/>?theme=spring'><img src='<c:url value="/resources/images/spring.png"/>'>&nbsp;<span><spring:message code='theme.spring' text="Spring"/></span></a>
                    <a href='<c:out value="${url}"/>?theme=wheat'><img src='<c:url value="/resources/images/wheat.png"/>'>&nbsp;<span><spring:message code='theme.wheat' text="Wheat"/></span></a>
                </div>
            </li>
            <c:if test="${not empty pageContext.request.remoteUser}">
            <li>
               <a href="#" onclick="document.forms['logoutForm'].submit(); return false;"><spring:message code='page.logout' text="Logout"/>&nbsp;${pageContext.request.remoteUser}</a>
               <form id="logoutForm" action='<%= application.getContextPath() + "/logout" %>' method="post">
                  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
               </form>
            </li>
            </c:if>
            <c:if test="${empty pageContext.request.remoteUser}">
            <li>
               <a href="<%= application.getContextPath() + "/login" %>"><spring:message code='page.login' text="Login"/></a>
            </li>
            </c:if>
            <li><a href='<%= application.getContextPath() + "/source/" + FilenameUtils.getBaseName(request.getParameter("url")) %>'><spring:message code='page.source' text="Source"/></a></li>

        </ul>
    </div>
