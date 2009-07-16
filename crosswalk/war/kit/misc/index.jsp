<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- !<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %> --%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <meta http-equiv="content-type" content="text/html;charset=UTF-8" />
  <meta name="description" content="Bungeni crosswalk application." />
	<link rel="icon" href="#" type="image/x-icon" />
	<title>BUNGENI Crosswalk System</title>
	<link rel="stylesheet" type="text/css" href="../../deploy/dev/resources/css/core.css" />
	<link rel="stylesheet" type="text/css" href="../../assets/css/extjs.css" />
<link rel="stylesheet" type="text/css" href="../../resources/css/ext-all.css"/>

	
	
	<!-- <script type="text/javascript" src="../../assets/js/extjs.js"></script>	-->
	<script type="text/javascript" src="../../adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="../../adapter/jquery/jquery-1.3.1.min.js"></script>
<!-- <script type="text/javascript" src="../../adapter/jquery/ext-jquery-adapter.js"></script> -->
	<!-- ENDLIBS -->
	<script type="text/javascript" src="../../ext-all.js"></script>

	<script type="text/javascript" src="../../assets/js/site7b30.js?v=4"></script>
<link rel="stylesheet" type="text/css" href="forms.css"/>

	</head>
<body>
<div id="viewport">

	
<div id="bd">
<script type="text/javascript" src="../../assets/js/samples2.js"></script>
<link rel="stylesheet" type="text/css" href="../../assets/css/sview2.css" />

<h3 style="margin-top:10px;">BUGENI Crosswalk System</h3>
<!-- <h5>Welcome <sec:authentication property="principal.username"/>!</h5><h6><a href='<c:url value="../../../j_spring_security_logout"/>'>Logout</a></h6> -->
<div id="samples">
	<div id="samples-cb">
		<img src="../../s.gif" class="normal-view" title="Full view with descriptions"/>
		<img src="../../s.gif" class="condensed-view" title="Condensed view" />
		<img src="../../s.gif" class="mini-view" title="Mini view" />
	</div>
	
    <div id="sample-menu"><div id="sample-menu-inner"></div></div>
    <div id="sample-box"><div id="sample-box-inner">
	  <div id="addSabbathSchool" class="sample-1" style="padding-left: 1em;">
	  </div>
	  <div id="addUnit" style="padding-left: 1em;"><!--display: none; -->
	  </div>
	  <div id="sample-2" style="padding-left: 1em;"><!--display: none; -->
	  </div>
	  <div id="manageUnits" style="padding-left: 1em;">
	  </div>
	  <div id="manageMembers" style="padding-left: 1em;"><!--display: none; -->
	  </div>
	  <div id="reports" style="padding-left: 1em;"><!--display: none; -->
	  </div>
	</div></div>
</div>


<div style="clear:both"></div>
</div><!-- end bd -->

<div id="ft">
	
	<div class="copy">&copy; 2006-2009 BUGENI, UNDESA</div>
</div>
<sec:authorize ifAllGranted="ROLE_ADMIN"></sec:authorize>


</div><!-- end viewport -->








</body>
</html>
