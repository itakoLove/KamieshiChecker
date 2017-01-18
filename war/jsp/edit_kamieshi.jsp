<%@page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@page import="to8823.kamieshiChecker.entity.Kamieshi" %>
<%@page import="to8823.kamieshiChecker.util.Constants"%>
<%@page import="to8823.kamieshiChecker.util.PropConstants"%>
<%@page import="twitter4j.Twitter"%>
<%@page import="twitter4j.TwitterFactory"%>
<%@page import="twitter4j.TwitterException"%>
<%@page import="twitter4j.auth.AccessToken" %>
<%@page import="twitter4j.User" %>
<%@page import="java.util.List" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	TwitterFactory twitterFactory = new TwitterFactory();

	HttpSession ses = request.getSession();

	Twitter twitter = twitterFactory.getInstance();
	twitter.setOAuthConsumer(PropConstants.CONSUMER_KEY, PropConstants.CONSUMER_SECRET);
	twitter.setOAuthAccessToken((AccessToken) ses.getAttribute(Constants.SESSION_ACCESS_TOKEN));

	List<Kamieshi> kamieshis = Kamieshi.getKamieshis(ds, twitter);
%>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<meta name="viewport" content="width=500px">
		<title>神絵師チェッカー</title>
		<link rel="stylesheet" type="text/css" href="/css/list.css?time=<%= System.currentTimeMillis() %>" media="all">
		<link rel="stylesheet" type="text/css" href="/css/edit.css?time=<%= System.currentTimeMillis() %>" media="all">
		<script src="/js/common.js?time=<%= System.currentTimeMillis() %>" type="text/javascript"></script>
		<script src="/js/edit.js?time=<%= System.currentTimeMillis() %>" type="text/javascript"></script>
	</head>
	<body onload="getKamieshiList();">
		<div class="editbox">
			<p>神絵師の追加</p>
			<input type="text" name="id" id="id" />
			<button onclick="addKamieshi(); return false;">Add</button>
		</div>
		<div class="editbox">
			<p>神絵師の削除</p>
			<div id="kamieshi_add_loading" style="display: none;">
				<img src="/img/loading.gif" />
			</div>
			<div id="kamieshi_add_contents">
			</div>
		</div>
		<jsp:include page="/jsp/header.jsp" />
	</body>
</html>