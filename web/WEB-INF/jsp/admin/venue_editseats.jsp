<%@ include file="includes/header.jsp" %>
<c:set var="includeJs" value="${includeJs} jquery.json-2.2.min.js lottoUtil.js admin_editseats.js" scope="request"/>
<img src="<c:url value="/assets/images/load_spinner.gif"/>" id="loading" class="float_right" width="32" height="32" />
<h1 class="narrow"><c:out value="${venue.name}" escapeXml="true"/></h1>
<div id="section_list_col">
    <h2>Sections</h2>
    <ul id="section_list">
    <c:forEach items="${sections}" var="s">
        <li id="sec-${s.sectionId}"><c:out value="${s.label}" escapeXml="true"/></li>
    </c:forEach>
    </ul>
    <a id="add_section" class="button">Add Section</a>
</div>
<div id="seats_col">
    <div id="edit_command_bar">
    <h2>Editing section: <span id="cur_section"></span></h2>
    <a id="add_rows" class="button">Add Rows</a>
    <a id="del_seats" class="button">Delete Selected</a>
    <a id="undo_section" class="button">Undo Changes</a>
    <a id="finished" class="button float_right">Finished Editing</a>
    </div>
    <ul id="seat_chart"></ul>
    <div id="instructions">
        Click "Add Section" at left to add a seating area.
        <c:if test="${fn:length(sections) > 0}">To change an existing section, click its name in the list.</c:if>
    </div>
</div>

<%-- AJAX forms that will be shown in dialogs --%>
<div id="add_section_form" title="Add Section">
    <form>
        <label class="above" for="add_section_label">Section name or number</label>
        <input type="text" name="add_section_label" id="add_section_label" class="ui-widget-content ui-corner-all" />
        <label class="above" for="add_section_location">Location</label>
        <input type="text" name="add_section_location" id="add_section_location" class="ui-widget-content ui-corner-all" />
    </form>
</div>

<div id="add_rows_form" title="Add Rows">
    <form>
        <label>Number of rows to add: <span id="add_rows_num">1</span></label>
        <div id="add_rows_slider"></div>
        <label>Seats per row: <span id="add_seats_num">10</span></label>
        <div id="add_seats_slider"></div>
        <label for="add_rows_name" class="above">Name or number for first row</label>
        <input type="text" name="add_rows_name" id="add_rows_name" class="ui-widget-content ui-corner-all" />
    </form>
</div>

<%@ include file="includes/footer.jsp" %>