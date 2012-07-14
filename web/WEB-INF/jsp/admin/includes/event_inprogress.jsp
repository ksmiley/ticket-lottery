<%@ include file="/WEB-INF/jsp/includes/include.jsp" %>
    <div class="event_action">
        <p>Event has been archived</p>
    </div>
    <h2><c:out value="${e.displayName}" escapeXml="true"/></h2>
    <h3><fmt:formatDate value="${e.startTime}" type="date" dateStyle="long" /> at <fmt:formatDate value="${e.startTime}" type="time" timeStyle="short" /> </h3>
    <ul>
        <li>This event is in progress and will end <strong><fmt:formatDate value="${e.endTime}" pattern="MMM. d h:mm a"/></strong></li>
    </ul>