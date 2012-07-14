<%@ include file="/WEB-INF/jsp/includes/include.jsp" %>
    <div class="event_action">
<c:choose>
    <c:when test="${!e.registered || !e.ticketAssigned}">
        <a class="ticket_search big_button">Search for tickets</a>
    </c:when>
    <c:when test="${e.ticketAssigned && e.ticketClaimed && e.canCancel}">
        <p>You already have a ticket to this event.</p>
		<p><a class="ticket_cancel">Cancel ticket</a></p>
    </c:when>
    <c:when test="${e.ticketAssigned && e.ticketClaimed && !e.canCancel}">
        <p>You already have a ticket to this event. Tickets can no longer be canceled.</p>
    </c:when>
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
    <c:when test="${!e.registered || !e.ticketAssigned}">
        <p>Additional tickets may be available for purchase until <strong><fmt:formatDate value="${e.event.saleEndTime}" type="date" dateStyle="long" /> at <fmt:formatDate value="${e.event.saleEndTime}" type="time" timeStyle="short" /></strong></p>
    </c:when>
    <c:when test="${e.ticketAssigned && e.ticketClaimed && e.canCancel}">
        <p>You can cancel your ticket without penalty any time before <strong><fmt:formatDate value="${e.event.cancelEndTime}" type="date" dateStyle="long" /> at <fmt:formatDate value="${e.event.cancelEndTime}" type="time" timeStyle="short" /></strong></p>
    </c:when>
    <c:when test="${e.ticketAssigned && e.ticketClaimed && !e.canCancel}">
        <%-- really nothing to display here. you got a ticket, and you're stuck with it --%>
    </c:when>
</c:choose>