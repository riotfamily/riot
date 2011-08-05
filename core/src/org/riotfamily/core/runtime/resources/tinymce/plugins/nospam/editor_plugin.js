(function() {
	tinymce.create('tinymce.plugins.Nospam', {
		
		init : function(ed, url) {
			var t = this;
			ed.onBeforeGetContent.add(function(ed, o) {
				if (o.format == "html")
					t._convertMailtoLinksToSpans(ed.contentDocument);
					
			});
			ed.onSetContent.add(function(ed, o) {
				t._convertMailtoSpansToLinks(ed.contentDocument);
			});
		},

		getInfo : function() {
			return {
				longname : 'Nospam Plugin',
				author : 'Felix Gnass / Jan-Frederic Linde',
				authorurl : 'http://neteye.de',
				infourl : 'http://neteye.de',
				version : "0.2"
			};
		},

		// Private methods
		
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
		
	});

	// Register plugin
	tinymce.PluginManager.add('nospam', tinymce.plugins.Nospam);
})();