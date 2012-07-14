<%@ page session="true" %>
<%@ include file="includes/header.jsp" %>
<div id="sidebar" class="on_left">
    <div id="login_box">
        <c:choose>
            <c:when test="${not empty param.login_error}">
                <p><span class="login_error">Couldn't sign in. Check your username and password.</span></p>
            </c:when>
            <c:otherwise>
                <p>To get started, sign in with your student account</p>
            </c:otherwise>
        </c:choose>
        <form action="<c:url value="/j_spring_security_check"/>" method="post">
            <div class="form_row"><label for="login_user">Username:</label> <input id="login_user" type="text" name="j_username" value="<c:out value="${SPRING_SECURITY_LAST_USERNAME}"/>" /></div>
            <div class="form_row"><label for="login_pass">Password:</label> <input id="login_pass" type="password" name="j_password" /></div>
            <div class="form_row"><input id="login_send" type="submit" name="login" value="Sign in" /></div>
        </form>
        <div style="clear: both;"></div>
    </div>
</div>
<div id="main_col" class="on_right">
<h1 class="narrow">Register for student tickets</h1>
<p>The ticket lottery for all University basketball and football games has moved online. To register for the upcoming lotteries or purchase additional tickets, use your standard U-account information to login.</p>
<p>You must be a full time student and has a valid University ID card to qualify. All applicable athletics fees must be paid with your tuition each semester.</p>
<h2>How it works</h2>
<p>Once you login, you'll see a list of lotteries for upcoming games, and information about the status of the lottery. Once the registration window is open, you will be able to register for tickets for that game as an individual or as part of a group.</p>
<p>Want more information about how the distribution works? <a href="#">Click here to learn the details.</a></p>
</div>
<%@ include file="includes/footer.jsp" %>
