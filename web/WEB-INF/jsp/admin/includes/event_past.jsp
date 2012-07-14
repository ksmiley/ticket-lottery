<%@ include file="/WEB-INF/jsp/includes/include.jsp" %>
    <div class="event_action">
        <p>Event has been archived</p>
    </div>
    <h2><c:out value="${e.displayName}" escapeXml="true"/></h2>
    <h3><fmt:formatDate value="${e.startTime}" type="date" dateStyle="long" /> at <fmt:formatDate value="${e.startTime}" type="time" timeStyle="short" /> </h3>
    <ul>
        <li><strong>This event has ended.</strong></li>
    </ul>