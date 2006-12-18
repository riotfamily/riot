var RiotFrameset = Class.create();
RiotFrameset.prototype = {

	initialize: function(id) {
		this.id = id;
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
		$A(frames).each(function (w) {w.location.reload()});
	},
	
	toggleI18n: function() {
		new Ajax.Request('${contextPath}${riotServletPrefix}/toggle-i18n', {
			method: 'get', onComplete: this.reloadFrames
		});
	}
}