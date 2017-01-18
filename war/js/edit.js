var getKamieshiList = function() {
	var xmlHttpRequest = new XMLHttpRequest();
	xmlHttpRequest.onreadystatechange = function() {
		var READYSTATE_COMPLETED = 4;
		var HTTP_STATUS_OK = 200;

		if (this.readyState == READYSTATE_COMPLETED && this.status == HTTP_STATUS_OK) {
			var jsonData = JSON.parse(this.responseText);

			if (jsonData.error == '0' ) {
				var kamieshi;
				var kamieshibox;
				var result;

				for(i = 0; i < jsonData.kamieshi_list.length; i++) {
					kamieshi = jsonData.kamieshi_list[i];

					kamieshibox = document.createElement("div");
					kamieshibox.className = 'kamieshi ' + kamieshi.screen_name;

					result = '<div class="icon"><img src="' + kamieshi.mini_profile_image + '" width="24px" height="24px"></div><div class="screen_name"><a href="https://twitter.com/' + kamieshi.screen_name + '" target="_blank">' + kamieshi.screen_name + '</a></div><div class="profile_name">' + kamieshi.profile_name + '</div><div class="delete"><img src="/img/batsu.png" onclick="removeKamieshi(\'' + kamieshi.screen_name + '\');"></div>';

					kamieshibox.innerHTML = result;

					document.getElementById('kamieshi_add_contents').appendChild(kamieshibox);
					document.getElementById('kamieshi_add_contents').style.display = 'block';
					document.getElementById('kamieshi_add_loading').style.display = 'none';
				}
			} else if(jsonData.error == '1') {
				showError('ログインしていないため、取得に失敗しました。');
			} else if(jsonData.error == '2') {
				showError('API制限が発生しました。しばらくお待ちください。');
			} else {
				showError('想定外のエラーが発生しました。');
			}
		}
	}

	xmlHttpRequest.open('GET', '/kamieshi/kamieshilist');

	document.getElementById('kamieshi_add_contents').innerHTML = '';
	document.getElementById('kamieshi_add_contents').style.display = 'none';
	document.getElementById('kamieshi_add_loading').style.display = 'block';

	xmlHttpRequest.send();
}

var addKamieshi = function() {
	var screenName = document.getElementById('id').value;

	var xmlHttpRequest = new XMLHttpRequest();
	xmlHttpRequest.onreadystatechange = function() {
		var READYSTATE_COMPLETED = 4;
		var HTTP_STATUS_OK = 200;

		if (this.readyState == READYSTATE_COMPLETED && this.status == HTTP_STATUS_OK) {
			var jsonData = JSON.parse(this.responseText);

			if (jsonData.error == '0' ) {
				showSuccess('登録しました。');
				document.getElementById('id').value = "";
			} else if(jsonData.error == '1') {
				showError('ログインしていないため、登録に失敗しました。');
			} else if(jsonData.error == '2') {
				showError('このIDのユーザは存在していません。');
			} else if(jsonData.error == '3') {
				showError('このIDのユーザは登録済みです。');
			} else {
				showError('想定外のエラーが発生しました。');
			}
		}
	}

	xmlHttpRequest.open('POST', '/kamieshi/add');

	xmlHttpRequest.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

	var data = {screenName: screenName};
	// データをリクエスト ボディに含めて送信する
	xmlHttpRequest.send(encodeHTMLForm(data));
}

var removeKamieshi = function(screenName) {
	var xmlHttpRequest = new XMLHttpRequest();
	xmlHttpRequest.onreadystatechange = function() {
		var READYSTATE_COMPLETED = 4;
		var HTTP_STATUS_OK = 200;

		if (this.readyState == READYSTATE_COMPLETED && this.status == HTTP_STATUS_OK) {
			var jsonData = JSON.parse(this.responseText);

			if (jsonData.error == '0' ) {
				showSuccess('削除しました。');
				document.getElementsByClassName('kamieshi ' + screenName)[0].style = "display: none;";
			} else if(jsonData.error == '1') {
				showError('ログインしていないため、削除に失敗しました。');
			} else if(jsonData.error == '2') {
				showError('このIDのユーザは登録されていません。');
			} else {
				showError('想定外のエラーが発生しました。');
			}
		}
	}

	xmlHttpRequest.open('POST', '/kamieshi/remove');

	xmlHttpRequest.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

	var data = {screenName: screenName};
	// データをリクエスト ボディに含めて送信する
	xmlHttpRequest.send(encodeHTMLForm(data));
}
