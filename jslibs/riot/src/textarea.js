var RiotTextArea = Class.create();
RiotTextArea.prototype = {
	initialize: function(el) {
		this.el = $(el);
	},
	setMaxLength: function(maxlength) {
		this.maxLength = maxlength;
		if (!this.counter) {
			var c = this.counter = document.createElement('div');
			c.className = 'counter';
			if (this.el.nextSibling) this.el.parentNode.insertBefore(c, this.el.nextSibling);
			else this.el.parentNode.appendChild(c);
		
			this.el.observe('keypress', this.checkLength.bindAsEventListener(this));
			this.el.observe('keyup', this.updateCounter.bind(this));
		}
		this.updateCounter();
		return this;
	},
	checkLength: function(ev) {
		if(this.el.value.length >= this.maxLength && (ev.keyCode == 0 || ev.keyCode == 13 || ev.keyCode >= 65)) {
			Event.stop(ev);
		}
	},
	updateCounter: function() {
		if (this.el.value.length > this.maxLength) {
			this.el.value = this.el.value.substring(0, this.maxLength);
		}
		this.counter.update(this.el.value.length + '/' + this.maxLength);
	},
	autoResize: function() {
		this.resize();
		if (this.autoSize) return;
		this.el.observe('keyup', this.resize.bind(this));
		this.autoSize = true;
		return this;
	},
	resize: function() {
		var lines = this.el.value.split('\n');
		this.el.rows = lines.length + 1;
	}
}