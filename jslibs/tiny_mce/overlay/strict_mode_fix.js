// TinyMCE disables strict_loading_mode in IE and Opera,
// so we have to overwrite the loadScript method ...
tinyMCE.loadScript = function(url) {
	tinyMCE.settings.strict_loading_mode = true;
	for (var i = 0; i < this.loadedFiles.length; i++) {
		if (this.loadedFiles[i] == url) return;
	}
	this.pendingFiles[this.pendingFiles.length] = url;
	this.loadedFiles[this.loadedFiles.length] = url;
};

// TinyMCE uses document.createElementNS() which is not supported
// by IE, so we also have to overwrite the loadNextSript() method ...
tinyMCE.loadNextScript = function() {
	var d = document;
	if (this.loadingIndex < this.pendingFiles.length) {
		var se = d.createElement('script');
		se.type = 'text/javascript';
		se.src = this.pendingFiles[this.loadingIndex++];
		d.getElementsByTagName("head")[0].appendChild(se);
	} 
	else {
		this.loadingIndex = -1;
	}
};

tinyMCE.addControl = function(id) {
	if (tinyMCELang && tinyMCELang['lang_theme_block']) {
		tinyMCE.addMCEControl(document.getElementById(id));
	}
	else {
		setTimeout(function() { tinyMCE.addControl(id); }, 100);
	}
}
