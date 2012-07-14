<%@ include file="includes/header.jsp" %>
<h1 class="narrow">
    <c:choose>
        <c:when test="${empty param.venue}">Add venue</c:when>
        <c:otherwise>Modify venue</c:otherwise>
    </c:choose>
</h1>
<form:form method="post" id="edit_venue" commandName="venueInfo">
    <form:errors path="name" cssClass="form_error float_right"/><label class="above" for="venueName">Venue name</label>
    <form:input path="name" id="venueName" cssClass="bigger"/>
    <label class="above" for="venuePhone">Phone number</label>
    <form:input path="phoneNumber" id="venuePhone"/>

    <h4>Address</h4>
    <label for="venueAddr1" class="above">Line 1</label>
    <form:input path="addrLine1" id="venueAddr1"/>
    <label for="venueAddr2" class="above">Line 1</label>
    <form:input path="addrLine2" id="venueAddr2"/><br />
    <div class="addr_part">
    <label for="venueCity" class="above">City</label>
    <form:input path="addrCity" id="venueCity"/>
    </div>
    <div class="addr_part">
    <label for="venueState" class="above">State</label>
    <form:input path="addrState" id="venueState"/>
    </div>
    <div class="addr_part">
    <label for="venueZip" class="above">Zip</label>
    <form:input path="addrZip" id="venueZip"/>
    </div>

    <div id="form_actions">
        <input type="submit" value="Save and continue"/> or <a href="<c:url value="/manage/home.htm"/>">Cancel</a>
    </div>
</form:form>
<%@ include file="includes/footer.jsp" %>