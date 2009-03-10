var Path = Class.create();
Path.prototype = {
	initialize: function() {
		this.url = location.href;
		var i = this.url.indexOf('?');
		if (i != -1) {
			this.url = this.url.substring(0, i);
			this.params = this.url.substring(i + 1);
		}
	},
	
	getQueryString: function(args) {
		var names = ['editorId', 'objectId', 'parentId', 'parentEditorId'];
		return names.collect(function(name, i) {
			if (i < args.length) {
				val = args[i];
				if (val && val != '') return name + '=' + val;
			}
			return null;
		}).compact().join('&');
	},

	reload: function(params) {
		if (params != this.params) {
			this.params = params;
			new Ajax.Updater('path', this.url, {parameters: params});
			if (top.frameset) {
				top.frameset.updateHash();
			}
		}
	},

	update: function(editorId, objectId, parentId, parentEditorId) {
		this.reload(this.getQueryString(arguments));
	},
	
	append: function(param) {
		this.reload(this.params + '&' + param);
	}	
}