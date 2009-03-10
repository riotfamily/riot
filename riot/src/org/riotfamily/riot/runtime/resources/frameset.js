var contextPath = '${contextPath}';
var riotServletPrefix = '${riotServletPrefix}';

var RiotFrameset = Class.create({

	initialize: function(id) {
		this.id = id;
		this.updated = false;
	},

	resizeFrame: function(frame) {
		if (!this.frameset) {
			this.frameset = $(this.id);
			this.rows = this.frameset.rows.split(',');
			this.length = this.rows.length;
		}
		var i;
		for (i = 0; i < frames.length && frames[i] != frame; i++);
		var height = this.getFrameHeight(frame);
		if (i < frames.length && height != this.rows[i]) {
			this.rows[i] = height;
			this.frameset.rows = this.rows.join(',');
		}
	},

	getFrameHeight: function(w) {
		var d = w.document;
		if (Prototype.Browser.IE) return d.body.scrollHeight;
		if (Prototype.Browser.Opera) return d.documentElement.clientHeight;
		return d.body.clientHeight;
	},

	updateHash: function() {
		var l = frames[1].location;
		var newHash = l.pathname + l.search;
		if (!this.updated) {
			this.updated = true;
			if (location.hash) {
				var currentHash = location.hash.substring(1);
				if (currentHash != newHash) {
					l.replace(currentHash);
					return;
				}
			}
		}
		location.hash = newHash;
	},
	
	reloadFrames: function() {
		$A(frames).each(function(w) {w.location.reload()});
	},

	toggleI18n: function() {
		new Ajax.Request(contextPath + riotServletPrefix + '/toggle-i18n', {
			method: 'get', onComplete: this.reloadFrames
		});
	}
});

var frameset = new RiotFrameset('rows');

function setLoading(loading) {
	statusBar.document.getElementById('loading').style.visibility = loading ? 'visible' : 'hidden';
}
