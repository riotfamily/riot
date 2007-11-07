$('${element.buttonId}').onclick = function() {
	var div = this.parentNode.parentNode;
	var input = div.getElementsByTagName('input');
	for (var i = 0; i < input.length; i++) {
		if (this.checked && input[i].type == 'password') {
			input[i].type = 'text';
		}
		else if (!this.checked && input[i].type == 'text') {
			input[i].type = 'password';
		}
	}
}