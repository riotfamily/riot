var contextPath = '${contextPath}';
var riotServletPrefix = '${riotServletPrefix}';

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
		var height = this.getFrameHeight(frame);
		if (i < frames.length && height != this.rows[i]) {
			this.rows[i] = height;
			this.frameset.rows = this.rows.join(',');
		}
	},

	getFrameHeight: function(w) {
		var d = w.document;
		// IEs    : d.body.scrollHeight
		// Opera  : d.documentElement.clientHeight
	    // Mozilla: d.body.clientHeight 
		// Safari : d.body.clientHeight
		if (Prototype.Browser.IE) return d.body.scrollHeight;
		if (Prototype.Browser.Opera) return d.documentElement.clientHeight;
		//if (Prototype.Browser.WebKit) {
			// REVISIT: This calculates the heights wrong for some reasons.
			// If an alert is being displayed here everything works fine...
		//}
		return d.body.clientHeight;
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
