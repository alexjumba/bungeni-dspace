<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/javascript" %>
<fmt:message key="welcome" var="welcome"/>

<fmt:message key="cross.repoGrid" var="repoGrid"/>
<fmt:message key="cross.repoIcon" var="repoIcon"/>
<fmt:message key="cross.repoOther" var="repoOther"/>
<fmt:message key="cross.dateIssued" var="dateIssued"/>
<fmt:message key="cross.author" var="author"/>
<fmt:message key="cross.title" var="title"/>
<fmt:message key="cross.description" var="description"/>
<fmt:message key="cross.collections" var="collections"/>
<fmt:message key="cross.refresh" var="refresh"/>
<fmt:message key="cross.refreshTip" var="refreshTip"/>
<fmt:message key="cross.searchDescription" var="searchDescription"/>
<fmt:message key="cross.searchResultsFor" var="searchResultsFor"/>
<fmt:message key="cross.search" var="search"/>
<fmt:message key="cross.actions" var="actions"/>
<fmt:message key="cross.page" var="page"/>
<fmt:message key="cross.showPreview" var="showPreview"/>
<fmt:message key="cross.recordsAvailable" var="recordsAvailable"/>
<fmt:message key="cross.recordsUnavailable" var="recordsUnavailable"/>

var repoGrid = '${repoGrid}';
var repoIcon = '${repoIcon}';
var repoOther = '${repoOther}';
var dateIssued = '${dateIssued}';
var author = '${author}';
var title = '${title}';
var description = '${description}';
var collections = '${collections}';
var refresh = '${refresh}';
var refreshTip = '${refreshTip}';
var searchDescription = '${searchDescription}';
var searchResultsFor = '${searchResultsFor}';
var search = '${search}';
var actions = '${actions}';
var page = '${page}';
var showPreview = '${showPreview}';
var recordsAvailable = '${recordsAvailable}';
var recordsUnavailable = '${recordsUnavailable}';
var defaultHome = '${defaultHome}';
var searchFor = '${search}';