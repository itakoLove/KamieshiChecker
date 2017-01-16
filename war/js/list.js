var key = null;
var page = null;

var favorite = function(id) {
	var xmlHttpRequest = new XMLHttpRequest();
	xmlHttpRequest.onreadystatechange = function() {
		var READYSTATE_COMPLETED = 4;
		var HTTP_STATUS_OK = 200;

		if (this.readyState == READYSTATE_COMPLETED && this.status == HTTP_STATUS_OK) {
			var jsonData = JSON.parse(this.responseText);

			if (jsonData.error == '0' ) {
				showSuccess('お気に入りしました。');
			} else if(jsonData.error == '1') {
				showError('ログインしていないため、登録に失敗しました。');
			} else if(jsonData.error == '2') {
				showError('IDが不正です。');
			} else if(jsonData.error == '3') {
				showError('そのツイートは既にお気に入り済みです。');
			} else {
				showError('想定外のエラーが発生しました。');
			}
		}
	}

	xmlHttpRequest.open('POST', '/kamieshi/favorite');

	xmlHttpRequest.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

	var data = {id: id};
	// データをリクエスト ボディに含めて送信する
	xmlHttpRequest.send(encodeHTMLForm(data));
}

var retweet = function(id) {
	var xmlHttpRequest = new XMLHttpRequest();
	xmlHttpRequest.onreadystatechange = function() {
		var READYSTATE_COMPLETED = 4;
		var HTTP_STATUS_OK = 200;

		if (this.readyState == READYSTATE_COMPLETED && this.status == HTTP_STATUS_OK) {
			var jsonData = JSON.parse(this.responseText);

			if (jsonData.error == '0' ) {
				showSuccess('RTしました。');
			} else if(jsonData.error == '1') {
				showError('ログインしていないため、登録に失敗しました。');
			} else if(jsonData.error == '2') {
				showError('IDが不正です。');
			} else if(jsonData.error == '3') {
				showError('そのツイートは既にRT済みです。');
			} else {
				showError('想定外のエラーが発生しました。');
			}
		}
	}

	xmlHttpRequest.open('POST', '/kamieshi/retweet');

	xmlHttpRequest.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

	var data = {id: id};
	// データをリクエスト ボディに含めて送信する
	xmlHttpRequest.send(encodeHTMLForm(data));
}

var getTweetList = function(isContinue) {
	var xmlHttpRequest = new XMLHttpRequest();
	xmlHttpRequest.onreadystatechange = function() {
		var READYSTATE_COMPLETED = 4;
		var HTTP_STATUS_OK = 200;

		if (this.readyState == READYSTATE_COMPLETED && this.status == HTTP_STATUS_OK) {
			var jsonData = JSON.parse(this.responseText);

			if (jsonData.error == '0' ) {
				var tweet;
				var tweetbox;
				var result;

				for(i = 0; i < jsonData.tweets.length; i++) {
					tweet = jsonData.tweets[i];

					tweetbox = document.createElement("div");
					tweetbox.className = 'tweetbox';

					result = '<div class="iconbox"><a href="' + tweet.original_profile_image + '" target="_blank"><img src="' + tweet.mini_profile_image + '" /></a></div><div class="kihon"><div class="namebox"><a href="https://twitter.com/' + tweet.screen_name + '" target="_blank">@' + tweet.screen_name + '　' + tweet.profile_name + '</a></div><div class="messagebox">' +  tweet.tweet_text  + '</div><div class="datebox"><a href="https://twitter.com/' + tweet.screen_name + '/status/' + tweet.tweet_id + '" target="_blank">' + tweet.tweet_create_date + '</a></div><div class="imagebox">';
					for (j = 0; j < tweet.tweet_image.length; j++) {
						result += '<a href="' + tweet.tweet_image[j] + ':large" target="_blank"><div class="trim"><img src="' + tweet.tweet_image[j] + '" /></div></a>';
					}
					result += '</div><div class="operationbox"><span class="favo" onclick=\'favorite("' + tweet.tweet_id + '"); return false;\'>☆（' +  tweet.favorite_count + '）</span>　<span class="rt" onclick=\'retweet("' + tweet.tweet_id + '"); return false;\'>RT（' +  tweet.retweet_count + '）</span></div></div>';

					tweetbox.innerHTML = result;

					document.getElementById('alltweetbox').appendChild(tweetbox);
					document.getElementById('alltweetbox').style.display = 'block';
					document.getElementById('loadingbox').style.display = 'none';

				}

				document.getElementById('continuebox').style.display = 'block';

				key = jsonData.key;
				page = jsonData.page;
			} else if(jsonData.error == '1') {
				showError('ログインしていないため、取得に失敗しました。');
			} else if(jsonData.error == '2') {
				showError('ツイートはありませんでした。');
			} else if(jsonData.error == '3') {
				showError('API制限が発生しました。しばらくお待ちください。');
			} else {
				showError('想定外のエラーが発生しました。');
			}
		}
	}


	if (isContinue) {
		xmlHttpRequest.open('GET', '/kamieshi/tweetlist?key=' + key + '&page=' + (Number(page)+1));
	} else {
		xmlHttpRequest.open('GET', '/kamieshi/tweetlist');
		document.getElementById('alltweetbox').innerHTML = '';
		document.getElementById('alltweetbox').style.display = 'none';
	}
	document.getElementById('continuebox').style.display = 'none';
	document.getElementById('loadingbox').style.display = 'block';

	xmlHttpRequest.send();
}
