var TextArea = {};
TextArea.setMaxLength = function(ta, maxlength) {
	if (typeof ta == 'string') ta = document.getElementById(ta);
	var c = document.createElement('div');
	c.className = 'counter';
	if (ta.nextSibling) ta.parentNode.insertBefore(c, ta.nextSibling);
	else ta.parentNode.appendChild(c);
	ta.counter = c;
	ta.maxLength = maxlength;
	ta.onkeypress = function(e) {
		if (!e) var e = window.event;
		if(this.value.length >= this.maxLength && (e.keyCode == 0 ||e.keyCode >= 65)) {
			return false;
		}
	}
	ta.onkeyup = function() {
		if (this.value.length > this.maxLength) {
			this.value = this.value.substring(0, this.maxLength);
		}
		this.counter.innerHTML = this.value.length + '/' + this.maxLength;
	}
	ta.onkeyup();
}