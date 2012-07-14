<%@ include file="includes/header.jsp" %>
<c:set var="includeJs" value="${includeJs} lottoUtil.js admin_eventinfo.js"/>
<h1 class="narrow">
    <c:choose>
        <c:when test="${empty param.event}">Add event</c:when>
        <c:otherwise>Modify event</c:otherwise>
    </c:choose>
</h1>
<form:form method="post" id="edit_event" commandName="lotteryEvent">
    <form:errors path="displayName" cssClass="form_error float_right"/>
    <label class="above" for="eventName">Event name</label>
    <form:input path="displayName" id="eventName" cssClass="bigger"/>
    <form:errors path="venueInfo" cssClass="form_error float_right"/>
    <label class="above" for="eventVenue">Venue</label>
    <form:select path="venueInfo" id="eventVenue">
        <form:option value="-" label="-- Select a venue --"/>
        <form:options items="${venues}" itemValue="venueId" itemLabel="name"/>
    </form:select>

    <%-- There's some Javascript trickery going on to make these date fields work,
         since the interface is friendlier if the entire date and time isn't all
         in one textbox. to make this work, we'll stick the real values in
         hidden fields and parse them into and out of the display fields on the
         client side
    --%>
    <div class="datetime_fields">
        <label class="above" for="startTime">Start time</label>
        <form:hidden path="startTime" id="startTime"/>
        <label class="above" for="endTime">End time</label>
        <form:hidden path="endTime" id="endTime"/>
        <label class="above" for="registerStartTime">Registration open time</label>
        <form:hidden path="registerStartTime" id="registerStartTime"/>
        <label class="above" for="distributionTime">Ticket distribution start time</label>
        <form:hidden path="distributionTime" id="distributionTime"/>
        <label class="above" for="claimEndTime">Ticket claim end time</label>
        <form:hidden path="claimEndTime" id="claimEndTime"/>
        <label class="above" for="cancelEndTime">Last time to cancel tickets</label>
        <form:hidden path="cancelEndTime" id="cancelEndTime"/>
        <label class="above" for="saleEndTime">Residual sales end time</label>
        <form:hidden path="saleEndTime" id="saleEndTime"/>
    </div>

    <div id="form_actions">
        <input type="submit" value="Save and continue"/> or <a href="<c:url value="/manage/home.htm"/>">Cancel</a>
    </div>
</form:form>
<%@ include file="includes/footer.jsp" %>