<%@ include file="/WEB-INF/jsp/includes/include.jsp" %>
    <div class="event_action">
<c:choose>
    <c:when test="${e.registered}">
        <p>You are registered for this ticket lottery.</p>
		<p><a class="lotto_withdraw">Withdraw from lottery</a></p>
    </c:when>
    <c:otherwise>
        <a class="lotto_register big_button">Register for lottery</a>
    </c:otherwise>
</c:choose>
    </div>
    <h2><c:out value="${e.event.displayName}" escapeXml="true"/></h2>
    <h3><fmt:formatDate value="${e.event.startTime}" type="date" dateStyle="long" /> at <fmt:formatDate value="${e.event.startTime}" type="time" timeStyle="short" /> </h3>
    <p>Location: <c:out value="${e.event.venueInfo.name}" escapeXml="true"/>
        <c:if test="${fn:length(e.event.venueInfo.addrCity) > 0}">
        (<c:out value="${e.event.venueInfo.addrCity}" escapeXml="true"/>, <c:out value="${e.event.venueInfo.addrState}" escapeXml="true"/>)
        </c:if>
    </p>
<c:choose>
    <c:when test="${e.registered}">
        <p>You can withdraw from this lottery without penalty any time before <strong><fmt:formatDate value="${e.event.distributionTime}" type="date" dateStyle="long" /> at <fmt:formatDate value="${e.event.distributionTime}" type="time" timeStyle="short" /></strong></p>
    </c:when>
    <c:otherwise>
        <p>This lottery is currently open, but you must register before tickets are distributed on <strong><fmt:formatDate value="${e.event.distributionTime}" type="date" dateStyle="long" /> at <fmt:formatDate value="${e.event.distributionTime}" type="time" timeStyle="short" /></strong></p>
    </c:otherwise>
</c:choose>