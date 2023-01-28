<%@ page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <sec:csrfMetaTags />
    <title><spring:message code="title" text="Spring 5 MVC Web App Example with JSP" /></title>
    <link rel="stylesheet" href="<spring:theme code='stylesheet'/>" type="text/css" />
    <script type="text/javascript" src='<c:url value="/resources/js/plugins/jquery-3.3.1.min.js"/>'></script>
    <script type="text/javascript" src='<c:url value="/resources/js/client/common.js"/>'></script>
    <script type="text/javascript" src='<c:url value="/resources/js/client/contacts.js"/>'></script>
    <link href='<c:url value="/resources/favicon.ico"/>' type="image/x-icon" rel="shortcut icon">
</head>
<body style="direction: '<spring:message code='dir' text='ltr' />'" dir="<spring:message code='dir' text='ltr' />"
      data-date-pattern="<spring:message code='date.pattern' text='MM/dd/yyyy' />"
      data-language="${pageContext.response.locale.language}"
      data-context-path="<c:url value='/'/>"
      data-image-path="<c:url value='/resources/images/'/>"
      data-theme="<spring:theme code='theme'/>"
      data-confirm="<spring:message code='confirm.delete' arguments="@,@"/>"
      data-edit-url="<c:url value="/jsp/contact"/>"
      data-delete-url="<c:url value="/api/contacts"/>"
      data-404-page="<c:url value='/error'/>"
      data-login-page="<c:url value='/login'/>">
    <c:url var="sortUrl" value="/jsp/contacts">
        <c:param name="size" value="${page.size}"/>
        <c:param name="page" value="${page.number}"/>
        <c:param name="sort" value=""/>
    </c:url>
    <div class="wrapper">
        <!-- START navbar -->
        <jsp:include page='../jsp/include/navbar.jsp'>
           <jsp:param name="url" value="/jsp/contacts"/>
           <jsp:param name="view" value="contacts-jsp"/>
        </jsp:include>
        <!-- END navbar -->
        <!-- START Content -->
        <div class="content">
            <h2><spring:message code='contacts.title' text='Contacts List' />&nbsp;(JSP)</h2>
            <div id="loader">&nbsp;</div>
            <!-- START filter -->
            <c:url var="actionUrl" value="/api/contacts/search/byParams"/>
            <form id="filterForm" action="${actionUrl}" method="get">
                <table class="filter" summary="<spring:message code='contacts.filter' text='Filter' />">
                    <%-- <caption><spring:message code='contacts.filter' text='Filter' /></caption> --%>
                    <thead>
                        <tr>
                            <th><label><spring:message code='contact.ssn' text='SSN' /></label></th>
                            <th><label><spring:message code='contact.fname' text='First Name' /></label></th>
                            <th><label><spring:message code='contact.lname' text='Last Name' /></label></th>
                            <th><label><spring:message code='contact.birthDate' text='Birth Date' /></label></th>
                            <th><label><spring:message code='contact.children' text='Children' /></label></th>
                            <th><label><spring:message code='contact.married' text='Status' /></label></th>
                            <th><label><spring:message code='contact.btn.reset' text='Reset' /></label></th>
                            <th><label><spring:message code='contact.btn.search' text='Search' /></label></th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <spring:message var="ssnErrMsg" code='invalid.ssn' text='invalid ssn' />
                            <td><input type="text" id="ssn" placeholder="<spring:message code='contact.ssn.placeholder' text='123-45-6789' />"/></td>
                            <spring:message var="nameErrMsg" code='invalid.name' text='invalid name' />
                            <td><input type="text" id="firstName" pattern=".{1,25}" oninvalid="this.setCustomValidity('${nameErrMsg}');" oninput="setCustomValidity('')" /></td>
                            <spring:message var="nameErrMsg" code='invalid.name' text='invalid name' />
                            <td><input type="text" id="lastName" pattern=".{1,25}" oninvalid="this.setCustomValidity('${nameErrMsg}');" oninput="setCustomValidity('')" /></td>
                            <spring:message var="dobErrMsg" code='invalid.birthDate' text='invalid date' />
                            <td><input type="text" id="birthDate" placeholder="<spring:message code='contact.birthDate.placeholder' text='1990-12-30' />" pattern="[0-9]{4}-[0-9]{2}-[0-9]{2}" oninvalid="this.setCustomValidity('${dobErrMsg}')" oninput="setCustomValidity('')" /></td>
                            <td><input type="number" min="0" id="children"/></td>
							<td>
							    <fieldset id="statuses">
									<table id="radios">
										<tr><td><input type="radio" id="married" value="true" name="married"/><label for="married"><spring:message code='contact.married' text='married'/></label></td></tr>
										<tr><td><input type="radio" id="single" value="false" name="married"/><label for="single"><spring:message code='contact.single' text='single'/></label></td></tr>
										<tr><td><input type="radio" id="all" value="" name="married" checked="checked"/><label for="all"><spring:message code='contact.all' text='all'/></label></td></tr>
									</table>
								</fieldset>
							</td>
                            <td><button type="button" id="Reset" class="reset" value="<spring:message code='contact.btn.reset' text='Reset' />"><img src="<c:url value='/resources/images/reset.png'/>" ></button></td>
                            <td><button type="submit" name="Search" value="<spring:message code='contact.btn.search' text='Search' />"><img src="<c:url value='/resources/images/search.png'/>" ></button></td>
                        </tr>
                    </tbody>
                </table>
                <input type="hidden" id="_ssn" value="${param.ssn}" />
            </form>
            <!-- END filter -->
            <!-- START records table -->
            <div class="table">
                <!-- START records header -->
                <div class="th">
                    <%-- <div>
                         <span class="sort_asc" data-sort-property="id" data-sort-direction="ASC" onclick="toggleSort(this)"></span>
                         <input class="th" value="<spring:message code='contact.id' text='ID' />" readonly="readonly" />
                    </div> --%>
                    <div>
                         <span class="sort_both" data-sort-property="ssn" data-sort-direction="" onclick="toggleSort(this)"></span>
                         <input class="th" value="<spring:message code='contact.ssn' text='Contact SSN' />" readonly="readonly" />
                    </div>
                    <div>
                         <span class="sort_both" data-sort-property="firstName" data-sort-direction="" onclick="toggleSort(this)"></span>
                         <input class="th" value="<spring:message code='contact.fname' text='First Name' />" readonly="readonly" />
                    </div>
                    <div>
                         <span class="sort_both" data-sort-property="lastName" data-sort-direction="" onclick="toggleSort(this)"></span>
                         <input class="th" value="<spring:message code='contact.lname' text='Last Name' />" readonly="readonly" />
                    </div>
                    <div>
                        <span class="sort_both" data-sort-property="birthDate" data-sort-direction="" onclick="toggleSort(this)"></span>
                        <input class="th" value="<spring:message code='contact.birthDate' text='Birth Date' />" readonly="readonly" />
                    </div>
                    <div>
                        <span class="sort_both" data-sort-property="married" data-sort-direction="" onclick="toggleSort(this)"></span>
                        <input class="th fixed-width" value="<spring:message code='contact.married' text='Married?' />" readonly="readonly" />
                    </div>
                    <div>
                        <span class="sort_both" data-sort-property="children" data-sort-direction="" onclick="toggleSort(this)"></span>
                        <input class="th" value="<spring:message code='contact.children' text='Children' />" readonly="readonly" />
                    </div>
                    <div>
                        <input class="th fixed-width control" value="<spring:message code='contact.edit' text='Edit' />" readonly="readonly" />
                    </div>
                    <div>
                        <input class="th fixed-width control" value="<spring:message code='contact.delete' text='Delete' />" readonly="readonly" />
                    </div>
                    <c:url var="addContactURL" value="/jsp/contact"/>
                    <div>
                        <button class="td fixed-width control" id="add" name="Add" onmouseover="swapBtnImage(this,&quot;<spring:theme code='add.hover.icon'/>&quot;);" onmouseout="swapBtnImage(this,&quot;<spring:theme code='add.icon'/>&quot;);" onclick="document.location.href='${addContactURL}'">
                            <img align="left" src="<spring:theme code='add.icon'/>"/>&nbsp;<spring:message code='contact.add' text='Add' />
                        </button>
                    </div>
                </div>
                <div class="th error" style="display: none;">
                   <span><spring:message code='pag.no.content' text='No results!' /></span>
                </div>
                <!-- END records header -->
                <!-- START records data -->
                <div id="data">
                </div>
                <!-- END records data -->
                <!-- START pagination -->
                <div class="pagination">
                    <table id="paginationTable" class="paginationTable"
                           data-pag-all="<spring:message code='pag.all' text='All' />"
                           data-pag-record-num="<spring:message code='pag.record.num' text='# Rows' />"
                           data-pag-first="<spring:message code='pag.first' text='First' />"
                           data-pag-last="<spring:message code='pag.last' text='Last' />"
                           data-pag-prev="<spring:message code='pag.prev' text='Prev' />"
                           data-pag-next="<spring:message code='pag.next' text='Next' />"
                           data-pag-sort="id,ASC">
                    </table>
                </div>
                <!-- END pagination -->
            </div>
            <!-- END records table -->
            <div class="spacer"></div>
            <!-- START export -->
            <div class="table">
                <div class="tr">
                    <button class="td wide" name="Excel" onmouseover="swapBtnImage(this,&quot;<spring:theme code='excel.hover.icon'/>&quot;);" onmouseout="swapBtnImage(this,&quot;<spring:theme code='excel.icon'/>&quot;);" onclick="document.location.href='<c:url value="/excel.htm"/>'">
                        <img align="left" src="<spring:theme code='excel.icon'/>"/>&nbsp;<spring:message code='export.excel' text='Export Excel' />
                    </button>
                    <button class="td wide" name="PDF" onmouseover="swapBtnImage(this,&quot;<spring:theme code='pdf.hover.icon'/>&quot;);" onmouseout="swapBtnImage(this,&quot;<spring:theme code='pdf.icon'/>&quot;);" onclick="document.location.href='<c:url value="/pdf.htm"/>'">
                        <img align="left" src="<spring:theme code='pdf.icon'/>"/>&nbsp;<spring:message code='export.pdf' text='Export PDF' />
                    </button>
                </div>
            </div>
            <!-- END export -->
        </div>
        <!-- END Content -->
        <!-- START Footer -->
        <jsp:include page='../jsp/include/footer.jsp'/>
        <!-- END Footer -->
    </div>
</body>
</html>