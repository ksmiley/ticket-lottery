<%@ include file="/WEB-INF/jsp/includes/include.jsp" %>
    <div class="event_action">
<c:choose>
    <c:when test="${e.ticketAssigned && e.ticketClaimed && e.canCancel}">
        <p>You have a ticket to this event.</p>
		<p><a class="ticket_cancel">Cancel ticket</a></p>
    </c:when>
    <c:when test="${e.ticketAssigned && e.ticketClaimed && !e.canCancel}">
        <p>You have a ticket to this event. Tickets can no longer be canceled.</p>
    </c:when>
    <c:when test="${!e.registered}">
        <p>You did not participate in this lottery.</p>
    </c:when>
    <c:otherwise>
        <p>You do not have a ticket to this event.</p>
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
    <p>Tickets are no longer available for this event.</p>