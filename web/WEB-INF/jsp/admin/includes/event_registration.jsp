<%@ include file="/WEB-INF/jsp/includes/include.jsp" %>
    <div class="event_action">
        <a href="<c:url value="/manage/event.htm"><c:param name="event" value="${e.lotteryId}"/></c:url>" class="button">Modify this event</a>
        <p>Once distribution starts, no changes can be made to the event.</p>
    </div>
    <h2><c:out value="${e.displayName}" escapeXml="true"/></h2>
    <h3><fmt:formatDate value="${e.startTime}" type="date" dateStyle="long" /> at <fmt:formatDate value="${e.startTime}" type="time" timeStyle="short" /> </h3>
    <ul>
        <li><strong>Registration is currently open</strong></li>
        <li>Students currently registered: <strong>${fn:length(e.lotteryRegistrations)}</strong></li>
        <li>Tickets available: <strong>${fn:length(e.lotterySeats)}</strong></li>
        <li>Registration opened: <strong><fmt:formatDate value="${e.registerStartTime}" pattern="MMM. d h:mm a"/></strong></li>
        <li>Distribution starts: <strong><fmt:formatDate value="${e.distributionTime}" pattern="MMM. d h:mm a"/></strong></li>
    </ul>