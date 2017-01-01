<%@page import="to8823.kamieshiChecker.util.Constants"%>

<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div id="header">
	<div class="title">神絵師チェッカー</div>
<%
	HttpSession ses = request.getSession();
	if (ses.getAttribute(Constants.SESSION_ACCESS_TOKEN) != null) {
%>
	<div class="reload" onclick="getTweetList(); return false;"></div>
	<div class="logout">
		<a href="/logout" >ログアウト</a>
	</div>
<%
	} else {
%>
	<div class="reload notlogin"></div>
	<div class="login">
		<a href="/login">ログイン</a>
	</div>
<%
	}
%>
</div>