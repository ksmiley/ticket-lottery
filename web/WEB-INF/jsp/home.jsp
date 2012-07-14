<%@ include file="includes/header.jsp" %>
<c:set var="includeJs" value="${includeJs} lottoUtil.js student_home.js"/>

    <div id="sidebar" class="on_right">
    	<h3>What now?</h3>
        <p>To the right, you'll see a list of upcoming events that have student tickets available through the lottery, along with actions available to you.</p>
        <p>What you can do with each  depends on when the event is scheduled. Most lotteries open for registration about two weeks before the event. Information about the status of the event is displayed next to the event. </p>
        <p>Once the registration window opens, click the Registration button to enroll in the lottery. You will be given the choice to register as an individual or as a group.</p>
        <p>If you enroll and can't attend the game, use the option to withdraw from the lottery or cancel your ticket. If you don't use a ticket and don't cancel it, you'll reduce your chance of getting tickets in the future.</p>
        <p><a href="<c:url value="/help.htm"/>">Click here for more information about registering for student tickets.</a></p>
    </div>
  	<div id="main_col" class="on_left">
        <img src="<c:url value="/assets/images/load_spinner.gif"/>" id="loading" class="float_right" width="32" height="32" />
  		<h1 class="narrow">Upcoming events</h1>
		<ul id="upcoming_events">
        <c:if test="${fn:length(events) == 0}">
            <li class="first_event"><h2>No upcoming events</h2></li>
        </c:if>
        <c:forEach var="te" items="${events}" varStatus="status">
            <%-- Each event can be in one of several phases, and it's formatted
            slightly differently for each. to keep this file from getting cluttered,
            each row's formatting will be handled by a separate fragment file.
            Since the controller already figured out the phase, it's used to
            determine the fragment filename. the c:set is needed so the event
            data is accessible from the fragment file
            Possible phases: register, claim, sales, closed, notopen --%>
            <c:if test="${status.first}">
                <c:set var="class_parts" value="first_event"/>
            </c:if>
            <c:if test="${te.phase eq 'register' && !te.registered}">
                <c:set var="class_parts" value="${class_parts} regopen"/>
            </c:if>
            <c:if test="${fn:length(fn:trim(class_parts)) > 0}">
                <c:set var="li_class"><c:out value=" "/>class="<c:out value="${fn:trim(class_parts)}"/>"</c:set>
            </c:if>
            <li id="event-<c:out value="${te.event.lotteryId}"/>"<c:out value="${li_class}" escapeXml="false"/>>
                <c:set var="e" value="${te}" scope="request"/>
                <jsp:include page="includes/event_${te.phase}.jsp"/>
            </li>
            <c:remove var="class_parts"/>
            <c:remove var="li_class"/>
        </c:forEach>
		</ul>
    </div>

<%-- various dialog boxes that will be hidden as soon as the page loads --%>
<div title="Cancel Ticket" id="confirm_cancel_ticket" class="confirm_dialog">
    <p class="msg_prompt">Are you sure you want to cancel your ticket for <span class="event_name">this event</span>?</p>
    <p>You won't be able to reclaim the ticket.</p>
</div>
<div title="Withdraw From Lottery" id="confirm_lottery_withdraw" class="confirm_dialog">
    <p class="msg_prompt">Are you sure you want to withdraw from the lottery for <span class="event_name">this event</span>?</p>
    <p>You can join again any time during the registration period.</p>
</div>
<div title="Register for Lottery" id="lotto_register">
    <p class="msg_prompt">Register for <span class="event_name">this event</span>...</p>
    <form>
        <label><input type="radio" name="lotto_register_regtype" id="lotto_register_indiv" value="indiv"/> As an individual</label>
        <label><input type="radio" name="lotto_register_regtype" id="lotto_register_curgroup" value="curgroup"/> With an existing group</label>
        <div id="curgroup_entry">
            Group to join:
            <span id="curgroup_name"></span>
            <a href="#" id="curgroup_change">(change)</a>
        </div>
        <label><input type="radio" name="lotto_register_regtype" id="lotto_register_newgroup" value="newgroup"/> With a group I create</label>
        <div id="newgroup_entry">
            <label for="newgroup_name" class="above">Name of new group:</label>
            <input type="text" id="newgroup_name" name="newgroup_name" />
        </div>
    </form>
</div>
<div title="Find Lottery Group" id="find_groups">
    <form>
        <label class="above" for="find_groups_searchfor">Enter all or part of a group name:</label>
        <input type="text" name="find_groups_searchfor" id="find_groups_searchfor" />
        <input type="submit" name="find_groups_dosearch" id="find_groups_dosearch" value="Search" />
    </form>
    <div id="find_groups_results"></div>
</div>
<div title="Ticket Claimed" id="claim_ticket">
    <p class="msg_prompt">
        You have been assigned the following ticket for <span class="event_name">this event</span>.
    </p>
    <p>
        This ticket is now reserved, but it's not officially yours until you pay for
        it using the button below.
    </p>
    <p>
        If you decide you won't be able to attend this event, you should cancel
        your ticket as soon as possible to avoid a penalty for non-attendance.
    </p>
    <div class="form_row">
        <div class="ticket_info_label">Section:</div>
        <div class="ticket_info section"></div>
    </div>
    <div class="form_row">
        <div class="ticket_info_label">Row:</div>
        <div class="ticket_info row"></div>
    </div>
    <div class="form_row">
        <div class="ticket_info_label">Seat:</div>
        <div class="ticket_info seat"></div>
    </div>
</div>

<%@ include file="includes/footer.jsp" %>
