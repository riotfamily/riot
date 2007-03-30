// TinyMCE disables strict_loading_mode in IE and Opera,
// so we have to overwrite the loadScript method ...
TinyMCE_Engine.prototype.loadScript = function(url) {
	tinyMCE.settings.strict_loading_mode = true;
	for (var i = 0; i < this.loadedFiles.length; i++) {
		if (this.loadedFiles[i] == url) return;
	}
	this.pendingFiles[this.pendingFiles.length] = url;
	this.loadedFiles[this.loadedFiles.length] = url;
};

// TinyMCE uses document.createElementNS() which is not supported
// by IE, so we also have to overwrite the loadNextSript() method ...
TinyMCE_Engine.prototype.loadNextScript = function() {
	if (this.loadingIndex < this.pendingFiles.length) {
		var d = document;
		var se = d.createElement('script');
		se.type = 'text/javascript';
		se.src = this.pendingFiles[this.loadingIndex++];
		d.getElementsByTagName("head")[0].appendChild(se);
	} 
	else {
		if (!tinyMCE.isLoaded) {
			tinyMCE.onLoad();
		}
	}
};

// We have to overwrite the onLoad function since it is missing a check for
// window.event which is null when the function is invoked manually. Some
// initialization stuff has been left out to reduce the script size. Therefore
// TinyMCE will only work in init-mode 'none'.
TinyMCE_Engine.prototype.onLoad = function() {
	if (tinyMCE.isRealIE && window.event && window.event.type == "readystatechange" && document.readyState != "complete") return true;

	if (tinyMCE.isLoaded) return true;
	tinyMCE.isLoaded = true;

	if (tinyMCE.isRealIE && document.body) {
		var r = document.body.createTextRange();
		r.collapse(true);
		r.select();
	}

	tinyMCE.dispatchCallback(null, 'onpageload', 'onPageLoad');
		
	for (var c=0; c<tinyMCE.configs.length; c++) {
		tinyMCE.settings = tinyMCE.configs[c];
		// Add submit triggers
		if (document.forms && tinyMCE.settings['add_form_submit_trigger'] && !tinyMCE.submitTriggers) {
			for (var i=0; i<document.forms.length; i++) {
				var form = document.forms[i];

				tinyMCE.addEvent(form, "submit", TinyMCE_Engine.prototype.handleEvent);
				tinyMCE.addEvent(form, "reset", TinyMCE_Engine.prototype.handleEvent);
				tinyMCE.submitTriggers = true; // Do it only once

				// Patch the form.submit function
				if (tinyMCE.settings['submit_patch']) {
					try {
						form.mceOldSubmit = form.submit;
						form.submit = TinyMCE_Engine.prototype.submitPatch;
					} 
					catch (e) {
					}
				}
				tinyMCE.dispatchCallback(null, 'oninit', 'onInit');
			}
		}
	}
};

TinyMCE_Engine.prototype.addControl = function(id) {
	if (tinyMCELang && tinyMCELang['lang_theme_block']) {
		tinyMCE.addMCEControl(document.getElementById(id));
	}
	else {
		setTimeout(function() { tinyMCE.addControl(id); }, 100);
	}
}

tinyMCE.strictModeFixed = true;