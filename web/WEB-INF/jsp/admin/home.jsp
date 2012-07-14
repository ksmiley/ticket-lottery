<%@ include file="includes/header.jsp" %>
<%-- need current time when displaying events to figure out which phase they're in --%>
<c:set var="now" value="<%=new java.util.Date()%>" scope="request"/>
<%-- display sidebar, which has a few actions and a list of venues --%>
<div id="sidebar" class="on_left">
    <h3>Venues</h3>
    <ul id="venue_list">
    <c:forEach var="v" items="${venues}" varStatus="status">
        <li>
            <a href="<c:url value="/manage/venue.htm"><c:param name="venue" value="${v.venueId}"/></c:url>">
            <c:out value="${v.name}" escapeXml="true"/>
            </a>
        </li>
    </c:forEach>
    </ul>
    <a href="<c:url value="/manage/venue.htm"/>" class="button float_right" id="addVenue">Add venue</a>
</div>
<%-- display main column, which has the list of upcoming events --%>
<div id="main_col" class="on_right">
<a href="<c:url value="/manage/event.htm"/>" class="button" id="addEvent">Add event</a>
<h1 class="narrow">Upcoming events</h1>
<ul id="upcoming_events">
<c:if test="${fn:length(events) == 0}">
    <li class="first_event"><h2>No upcoming events</h2></li>
</c:if>
<c:forEach var="te" items="${events}" varStatus="status">
    <%-- Each event can be in one of several phases, and it's formatted
         slightly differently for each. to keep this file from getting cluttered,
         each row's formatting will be handled by a separate fragment file --%>
    <li<c:if test="${status.first}"> class="first_event"</c:if>>
    <c:set var="e" value="${te}" scope="request"/>
    <c:choose>
        <c:when test="${e.registerStartTime <= now && now < e.distributionTime}">
            <jsp:include page="includes/event_registration.jsp"/>
        </c:when>
        <c:when test="${e.distributionTime <= now && now < e.claimEndTime}">
            <jsp:include page="includes/event_claim.jsp"/>
        </c:when>
        <c:when test="${e.claimEndTime <= now && now < e.saleEndTime}">
            <jsp:include page="includes/event_sales.jsp"/>
        </c:when>
        <c:when test="${e.saleEndTime <= now && now < e.startTime}">
            <jsp:include page="includes/event_closed.jsp"/>
        </c:when>
        <c:when test="${now < e.registerStartTime}">
            <jsp:include page="includes/event_notopen.jsp"/>
        </c:when>
        <c:when test="${e.startTime <= now && now <= e.endTime}">
            <jsp:include page="includes/event_inprogress.jsp"/>
        </c:when>
        <c:when test="${e.endTime < now}">
            <jsp:include page="includes/event_over.jsp"/>
        </c:when>
    </c:choose>
    </li>
</c:forEach>
</ul>
</div>
<%@ include file="includes/footer.jsp" %>