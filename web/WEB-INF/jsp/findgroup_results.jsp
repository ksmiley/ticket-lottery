<%@ include file="/WEB-INF/jsp/includes/include.jsp" %>
<html>
<head><title>Group search results</title></head>
<body>
    <ul id="group_list">
    <c:if test="${fn:length(groups) == 0}">
        <li class="no_border"><h2>No groups found for search &quot;<c:out value="${searchTerm}" escapeXml="true"/>&quot;</h2></li>
    </c:if>
    <c:forEach var="g" items="${groups}" varStatus="status">
        <li id="<c:out value="results-group-${g.groupId}" escapeXml="true"/>"<c:if test="${status.first}"> class="no_border"</c:if>>
            <a class="join_group button">Join</a>
            <h2><c:out value="${g.name}" escapeXml="true"/></h2>
            <p>Created by: <span class="name"><c:out value="${g.studentInfo.name}" escapeXml="true"/></span></p>
        </li>
    </c:forEach>
    </ul>
</body>
</html>