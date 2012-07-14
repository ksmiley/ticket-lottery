<%@ include file="/WEB-INF/jsp/includes/include.jsp" %>
<%-- used by AJAX methods that need to change the display of the home page.
     mostly calls jsp:include for the requested fragment, but has to do
     a little environment setup first
--%>
<html>
<body>
<c:set var="e" value="${event}" scope="request"/>
<jsp:include page="includes/event_${event.phase}.jsp"/>
</body>
</html>