<%@ include file="/WEB-INF/jsp/includes/include.jsp" %>
    <div class="event_action">
        <p><a class="button" href="<c:url value="/manage/event.htm"><c:param name="event" value="${e.lotteryId}"/></c:url>">Modify this event</a></p>
    </div>
    <h2><c:out value="${e.displayName}" escapeXml="true"/></h2>
    <h3><fmt:formatDate value="${e.startTime}" type="date" dateStyle="long" /> at <fmt:formatDate value="${e.startTime}" type="time" timeStyle="short" /> </h3>
    <ul>
        <li>Registration will open <strong><fmt:formatDate value="${e.registerStartTime}" pattern="MMM. d h:mm a"/></strong></li>
        <li>Tickets available: <strong>${fn:length(e.lotterySeats)}</strong></li>
    </ul>