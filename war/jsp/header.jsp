<%@page import="to8823.kamieshiChecker.util.Constants"%>

<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div id="header">
	<div class="title">神絵師チェッカー</div>
<%
	HttpSession ses = request.getSession();
	boolean isLogin = ses.getAttribute(Constants.SESSION_ACCESS_TOKEN) != null;
	if (isLogin) {
%>
	<div class="reload" onclick="getTweetList(); return false;"></div>
<%
	} else {
%>
	<div class="reload notlogin"></div>
<%
	}
%>
	<div class="menubutton" onclick="switchMenu();">
		<img src="/img/menu.png" />
	</div>
	<div id="menubox" style="display: none;">
		<ul>
<%
	if (isLogin) {
%>
			<li><a href="/" >ホーム</a></li>
			<li class="border"></li>
			<li>神絵師の追加・削除</li>
			<li>設定</li>
			<li class="border"></li>
			<li><a href="/logout" >ログアウト</a></li>
<%
	} else {
%>
			<li><a href="/login">ログイン</a></li>
<%
	}
%>
		</ul>
	</div>
</div>