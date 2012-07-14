<%@ page session="true" %>
<%@ include file="includes/header.jsp" %>
<c:set var="includeJs" value="${includeJs} admin_login.js"/>
<c:set var="lastUsername" value="${fn:substringAfter(SPRING_SECURITY_LAST_USERNAME, '/')}"/>

  	<div id="adm_login_box">
        <c:if test="${not empty param.login_error}">
        <div style="margin-bottom: 8px;" class="login_error">Couldn't sign in. Check your username and password.</div>
        </c:if>
		<form action="<c:url value="/j_spring_security_check"/>" method="post">
            <input type="hidden" id="j_username" name="j_username" value="" />
            <input type="hidden" id="j_password" name="j_password" value="" />
            <input type="hidden" name="spring-security-redirect" value="/manage/home.htm" />
			<label for="adm_login_user">Username</label>
			<input type="text" name="user" id="adm_login_user" value="<c:out value="${lastUsername}"/>" />
			<label for="adm_login_pass">Password</label>
			<input type="password" name="pass" id="adm_login_pass" />
			<input type="submit" name="login" value="Sign in" id="adm_login_send" />
		</form>
	</div>
<%@ include file="includes/footer.jsp" %>