<%@ include file="/WEB-INF/jsp/includes/include.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%--
Start building a list of javascript files to include in the footer.
Any page between the header and the footer can queue up another javascript file
by adding it to this space-delimited list. In the footer, these will have
/assets/ prepended to the names and will be passed through <c:url>
--%>
<c:set var="includeJs" value="jquery-1.3.2.min.js jquery-ui-1.7.2.custom.min.js" scope="request"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>U Ticket Lottery</title>
<link href="<c:url value="/assets/lottolayout.css"/>" rel="stylesheet" type="text/css" />
<link href="<c:url value="/assets/jquery-ui-1.7.2.custom.css"/>" rel="stylesheet" type="text/css" />
</head>

<body>

<div id="container">
  <div id="header">
    <h1><img src="<c:url value="/assets/lottologo.png"/>" alt="U Ticket Lottery" width="370" height="90" /></h1>
    <ul id="minornav">
    	<li class="nav_first_item"><a href="<c:url value="/about.htm"/>">About</a></li>
        <li><a href="<c:url value="/contact.htm"/>">Contact</a></li>
        <li><a href<c:url value="help.htm"/>">Help</a></li>
        <sec:authorize ifAllGranted="ROLE_USER">
        <li><a href="<c:url value="/logout.htm"/>">Logout</a></li>
        </sec:authorize>
    </ul>
  </div>
  <div id="mainContent">