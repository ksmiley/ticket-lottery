<%@ include file="includes/header.jsp" %>
<c:set var="includeJs" value="${includeJs} jquery.json-2.2.min.js lottoUtil.js admin_eventseats.js" scope="request"/>
<img src="<c:url value="/assets/images/load_spinner.gif"/>" id="loading" class="float_right" width="32" height="32" />
<h1 class="narrow"><c:out value="${event.displayName}" escapeXml="true"/></h1>
<div id="section_list_col">
    <h2>Sections</h2>
    <ul id="section_list">
    <c:forEach items="${sections}" var="s">
        <li id="sec-${s.sectionId}"><c:out value="${s.label}" escapeXml="true"/></li>
    </c:forEach>
    </ul>
</div>
<div id="seats_col" class="edit_event">
    <div id="edit_command_bar">
    <a id="finished" class="button float_right">Finished Editing Tickets</a>
    <h2>Editing section: <span id="cur_section"></span></h2>
    </div>
    <ul id="seat_chart"></ul>
    <div id="instructions">
        To make tickets available in a section, click its name in the list.
    </div>
</div>


<%@ include file="includes/footer.jsp" %>