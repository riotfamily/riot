var TinyMCE_NospamPlugin = {

	getInfo : function() {
		return {
			longname : 'Nospam Plugin',
			author : 'Felix Gnass',
			authorurl : 'http://neteye.de',
			infourl : 'http://neteye.de',
			version : "0.1"
		};
	},

	getControlHTML: function(cn) {
		return "";
	},

	cleanup: function(type, content, inst) {
		if (type == 'insert_to_editor_dom') {
			this._convertMailtoSpansToLinks(inst.getDoc());
		}
		else if (type == 'get_from_editor_dom') {
			this._convertMailtoLinksToSpans(inst.getDoc());
		}
		return content;
	},
	
	_convertMailtoSpansToLinks: function(doc) {
		var spans = doc.getElementsByTagName('span');
		for (var i = 0; i < spans.length; i++) {
			var e = spans[i];
			if (e.className == "mailto") {
				var a = doc.createElement('a');
				var addr = e.innerHTML.replace(/ at /, '@');
				a.href = 'mailto:' + addr;
				a.innerHTML = addr;
				e.parentNode.replaceChild(a, e);
			}
		}
	},
	
	_convertMailtoLinksToSpans: function(doc) {
		var links = doc.getElementsByTagName('a');
		for (var i = 0; i < links.length; i++) {
			var a = links[i];
			if (a.href && a.href.indexOf('mailto:') == 0) {
				var e = doc.createElement('span');
				e.className = 'mailto';
				var addr = a.href.replace(/@/, ' at ');
				e.innerHTML = addr.substring(addr.indexOf(':') + 1);
				a.parentNode.replaceChild(e, a);
			}
		}
	}
	
};

// Adds the plugin class to the list of available TinyMCE plugins
tinyMCE.addPlugin("nospam", TinyMCE_NospamPlugin);
