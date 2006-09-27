var RowFrameset = function(id) {

	this.init = function() {
		if (!this.frameset) {
			this.frameset = document.getElementById(id);
			this.rows = this.frameset.rows.split(',');
			this.length = this.rows.length;
		}
	}
	
	this.getInnerHeight = function(frame) {
		if (frame.innerHeight) { return frame.innerHeight; }
		var doc = frame.document;
		if (doc.documentElement && doc.documentElement.clientHeight) {
			return doc.documentElement.clientHeight;
		}
		if (doc.body) { return doc.body.clientHeight; }
	};

	this.getPageHeight = function(frame) {
		var b = frame.document.body;
		var bodyHeight = Math.max(b.scrollHeight, b.offsetHeight);
		return Math.max(bodyHeight, this.getInnerHeight(frame));
	};

	this.resizeFrame = function(frame) {
		this.init();
		var i = 0;
		for (; i < frames.length; i++) {
			if (frame == frames[i]) {
				break;
			}
		}
		var height = this.getPageHeight(frame);
		if (height != this.rows[i]) {
			this.rows[i] = height;
			var s = '';
			for (var j = 0; j < this.length; j++) {
				s += this.rows[j];
				if (j < this.length - 1) {
					s += ',';
				}
			}
			this.frameset.rows = s;
		}
	};
}