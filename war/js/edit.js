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
