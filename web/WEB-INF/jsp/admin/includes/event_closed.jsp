<%@ include file="/WEB-INF/jsp/includes/include.jsp" %>
    <div class="event_action">
        <p>Event cannot be modified once tickets have been distributed</p>
    </div>
    <h2><c:out value="${e.displayName}" escapeXml="true"/></h2>
    <h3><fmt:formatDate value="${e.startTime}" type="date" dateStyle="long" /> at <fmt:formatDate value="${e.startTime}" type="time" timeStyle="short" /> </h3>
    <ul>
        <li><strong>Ticket distribution is closed</strong></li>
        <c:if test="${now < e.cancelEndTime}">
            <li>Purchased tickets can be canceled until <fmt:formatDate value="${e.cancelEndTime}" pattern="MMM. d h:mm a"/></li>
        </c:if>
    </ul>