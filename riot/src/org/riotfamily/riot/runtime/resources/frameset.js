var RiotFrameset = Class.create({

	initialize: function(id) {
		this.id = id;
	},

	resizeFrame: function(frame) {
		if (!this.frameset) {
			this.frameset = $(this.id);
			this.rows = this.frameset.rows.split(',');
			this.length = this.rows.length;
		}
		var i;
		for (i = 0; i < frames.length && frames[i] != frame; i++);
		var height = Viewport.getPageHeight(frame);
		if (i < frames.length && height != this.rows[i]) {
			this.rows[i] = height;
			this.frameset.rows = this.rows.join(',');
		}
	},

	reloadFrames: function() {
		$A(frames).each(function(w) {w.location.reload()});
	},

	toggleI18n: function() {
		new Ajax.Request('${contextPath}${riotServletPrefix}/toggle-i18n', {
			method: 'get', onComplete: this.reloadFrames
		});
	}
});

var frameset = new RiotFrameset('rows');

function setLoading(loading) {
	statusbar.document.getElementById('loading').style.visibility = loading ? 'visible' : 'hidden';
}
