<%@ include file="/WEB-INF/jsp/includes/include.jsp" %>
    <div class="event_action">
        <p>Event cannot be modified once tickets have been distributed</p>
    </div>
    <h2><c:out value="${e.displayName}" escapeXml="true"/></h2>
    <h3><fmt:formatDate value="${e.startTime}" type="date" dateStyle="long" /> at <fmt:formatDate value="${e.startTime}" type="time" timeStyle="short" /> </h3>
    <ul>
        <li><strong>Tickets have been assigned and can be claimed</strong></li>
        <li>Claim phase ends: <strong><fmt:formatDate value="${e.claimEndTime}" pattern="MMM. d h:mm a"/></strong></li>
    </ul>