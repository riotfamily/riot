var e = document.getElementById('${element.id}');
e.form = document.getElementById('${element.form.id}');
e.fireChangeEvent = function() {
	if (this._onchange) {
		this._onchange(new ChangeEvent(this));
	}
};
var opt = e.getElementsByTagName('input');
e.options = opt;
for (var i = 0; i < opt.length; i++) {
	opt[i].containerElement = e;
	opt[i].onclick = function(ev) {
		this.selected = this.checked;
		this.containerElement.fireChangeEvent();
	}
} 