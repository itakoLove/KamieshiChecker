<%@page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@page import="to8823.kamieshiChecker.util.Constants"%>
<%@page import="to8823.kamieshiChecker.util.PropConstants"%>
<%@page import="twitter4j.Twitter"%>
<%@page import="twitter4j.TwitterFactory"%>
<%@page import="twitter4j.Query"%>
<%@page import="twitter4j.QueryResult"%>
<%@page import="twitter4j.Status"%>
<%@page import="twitter4j.MediaEntity"%>
<%@page import="twitter4j.auth.AccessToken" %>
<%@page import="java.util.List" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.TimeZone" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="to8823.kamieshiChecker.entity.Kamieshi" %>
<%@page import="to8823.kamieshiChecker.entity.CheckUser" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	TwitterFactory twitterFactory = new TwitterFactory();
%>

<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<meta name="viewport" content="width=500px">
		<title>神絵師チェッカー</title>
		<link rel="stylesheet" type="text/css" href="/css/list.css?time=<%= System.currentTimeMillis() %>" media="all">
		<script src="/js/common.js?time=<%= System.currentTimeMillis() %>" type="text/javascript"></script>
		<script src="/js/list.js?time=<%= System.currentTimeMillis() %>" type="text/javascript"></script>
	</head>
<%
	HttpSession ses = request.getSession();
	if (ses.getAttribute(Constants.SESSION_ACCESS_TOKEN) != null) {

		Twitter twitter = twitterFactory.getInstance();
		twitter.setOAuthConsumer(PropConstants.CONSUMER_KEY, PropConstants.CONSUMER_SECRET);
		twitter.setOAuthAccessToken((AccessToken) ses.getAttribute(Constants.SESSION_ACCESS_TOKEN));
%>
	<body onload='getTweetList();'>
		<div id="mainbox">
			<div id="alltweetbox" style="display: none;"></div>
			<div id="continuebox" style="display: none;" onclick="getTweetList(true);">
				続きを取り込む
			</div>
			<div id="loadingbox">
				<img src="/img/loading.gif" />
			</div>
<%
	} else {
%>
	<body>
		<div id="mainbox">
			<p>神絵師チェッカーは特定のユーザがTwitter上にアップロードされた画像ファイルのみを取得・表示するWEBアプリです。</p>
			<p>神絵師のイラスト・マンガを見逃すなんてとんでもない！</p>
			<p>ご使用される場合はTwitterアカウントに<a href="/login">ログイン</a>してください。</p>
<%
	}
%>

		</div>
		<jsp:include page="/jsp/header.jsp" />
	</body>
</html>