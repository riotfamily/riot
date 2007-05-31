var RiotFrameset = Class.create();
RiotFrameset.prototype = {

	initialize: function(id) {
		this.id = id;
		Event.observe(window, 'unload', this.onUnload.bind(this));
	},

	resizeFrame: function(frame) {
		if (!this.frameset) {
			this.frameset = $(this.id);
			this.rows = this.frameset.rows.split(',');
			this.length = this.rows.length;
		}
		var i = $A(frames).indexOf(frame);
		var height = Viewport.getPageHeight(frame);
		if (height != this.rows[i]) {
			this.rows[i] = height;
			this.frameset.rows = this.rows.join(',');
		}
	},

	reloadFrames: function() {
		$A(frames).each(function(w) {w.location.reload()});
	},

	onUnload: function() {
		var date = new Date();
		date.setTime(date.getTime() + 5000);
		document.cookie = 'rioteditor=' + frames['editor'].location.href
				+ '; expires=' + date.toGMTString();
	},

	restoreEditor: function(frame) {
		if (!this.restored) {
			var match = /rioteditor=([^;]*)/.exec(document.cookie);
			if (match) {
				var editorUrl = match[1];
				if (editorUrl != frame.location.href) {
					frame.location.replace(editorUrl);
				}
			}
			this.restored = true;
		}
	},

	toggleI18n: function() {
		new Ajax.Request('${contextPath}${riotServletPrefix}/toggle-i18n', {
			method: 'get', onComplete: this.reloadFrames
		});
	}
}

var frameset = new RiotFrameset('rows');
