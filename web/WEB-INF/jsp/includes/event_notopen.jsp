<%@ include file="/WEB-INF/jsp/includes/include.jsp" %>
    <h2><c:out value="${e.event.displayName}" escapeXml="true"/></h2>
    <h3><fmt:formatDate value="${e.event.startTime}" type="date" dateStyle="long" /> at <fmt:formatDate value="${e.event.startTime}" type="time" timeStyle="short" /> </h3>
    <p>Location: <c:out value="${e.event.venueInfo.name}" escapeXml="true"/>
        <c:if test="${fn:length(e.event.venueInfo.addrCity) > 0}">
        (<c:out value="${e.event.venueInfo.addrCity}" escapeXml="true"/>, <c:out value="${e.event.venueInfo.addrState}" escapeXml="true"/>)
        </c:if>
    </p>
    <p>
        Registration for this lottery will be open from
        <strong><fmt:formatDate value="${e.event.registerStartTime}" type="date" dateStyle="long" /> at <fmt:formatDate value="${e.event.registerStartTime}" type="time" timeStyle="short" /></strong>
        until <strong><fmt:formatDate value="${e.event.distributionTime}" type="date" dateStyle="long" /> at <fmt:formatDate value="${e.event.distributionTime}" type="time" timeStyle="short" /></strong>
    </p>