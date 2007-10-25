var TinyMCE_AutoCleanupPlugin = {

	getInfo : function() {
		return {
			longname : 'AutoCleanup Plugin',
			author : 'Felix Gnass',
			authorurl : 'http://neteye.de',
			infourl : 'http://neteye.de',
			version : '1.1'
		};
	},

	onChange: function(inst) {
		var _this = TinyMCE_AutoCleanupPlugin;
		if (_this.ctrlVPressed) {
			_this.ctrlVPressed = false;
			var cleanHtml = tinyMCE._cleanupHTML(inst, inst.contentDocument, inst.settings, inst.getBody(), inst.visualAid);
			if (!_this._structureEquals(cleanHtml, inst.getBody().innerHTML)) {
				inst.execCommand("mceCleanup", false);
			}
		}
		return true;
	},

	handleEvent: function(e) {
		var _this = TinyMCE_AutoCleanupPlugin;
		var code = e.keyCode ? e.keyCode : e.which;
		_this.ctrlVPressed = (e.ctrlKey || e.metaKey) && code == 86;
		return true;
	},

	cleanup: function(type, content, inst) {
		if (type == "get_from_editor") {
			content = content.replace(/<!--.*?-->/g, '');
		}
		return content;
	},

	_extractTags: function(s) {
		return s.replace(/(<[^\/>]*[^>]*)>[^<]*/g, '$1>');
	},

	_structureEquals: function(s1, s2) {
		return this._extractTags(s1) == this._extractTags(s2);
	}

};

tinyMCE.addPlugin("autocleanup", TinyMCE_AutoCleanupPlugin);
