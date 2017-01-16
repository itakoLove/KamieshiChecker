var showNumber = 0;

var switchMenu = function() {
	if (document.getElementById('menubox').style.display == 'block') {
		document.getElementById('menubox').style.display = 'none';
	} else {
		document.getElementById('menubox').style.display = 'block';
	}
}

var encodeHTMLForm = function(data) {
	var params = [];

	for ( var name in data) {
		var value = data[name];
		var param = encodeURIComponent(name) + '=' + encodeURIComponent(value);

		params.push(param);
	}

	return params.join('&').replace(/%20/g, '+');
}

var showSuccess = function(message) {
	showNumber++;
	document.getElementById('resultbox').innerText = message;
	document.getElementById('resultbox').className = 'success';
	document.getElementById('resultbox').style.display = 'block';
	document.getElementById('resultbox').style.opacity = 0;
	showResultBox(showNumber);
}

var showError = function(message) {
	showNumber++;

	document.getElementById('resultbox').innerText = message;
	document.getElementById('resultbox').className = 'error';
	document.getElementById('resultbox').style.display = 'block';
	document.getElementById('resultbox').style.opacity = 0;
	showResultBox(showNumber);
}

var showResultBox = function(showNumber) {
	if (this.showNumber == showNumber) {
		document.getElementById('resultbox').style.opacity =
			Number(document.getElementById('resultbox').style.opacity) + 0.05;
		if (Number(document.getElementById('resultbox').style.opacity) < 1) {
			setTimeout(showResultBox, 50, this.showNumber);
		} else {
			setTimeout(hideResultBox, 1500, this.showNumber);
		}
	}
}

var hideResultBox = function(showNumber) {
	if (this.showNumber == showNumber) {
		document.getElementById('resultbox').style.opacity =
			Number(document.getElementById('resultbox').style.opacity) - 0.05;
		if (Number(document.getElementById('resultbox').style.opacity) > 0) {
			setTimeout(hideResultBox, 50, this.showNumber);
		} else {
			document.getElementById('resultbox').style.display = 'none';
		}
	}
}
