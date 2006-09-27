var Resources = {

	basePath: '/',
	
	/** 
	 * Adds the basePath to urls that don't start with a slash.
	 */
	resolveUrl: function(url) {
		if (url.charAt(0) != '/') {
			url = Resources.basePath + url;
		}
		return url;
	},
	
	/**
	 * Returns true if the given resource url has already been loaded.
	 */
	isLoaded: function(url) {
		return Resources._states[url] == Resources.LOADED;
	},
	
	/**
	 * Dynamically loads a script from the given url. Optionally a test
	 * may be specified to check whether the script is (already) loaded.
	 */
	loadScript: function(src, test) {
		src = Resources.resolveUrl(src);
		Resources._debug('Script requested: ' + src);
		if (!Resources.isLoaded(src)) {
			Resources._loading(src);
			if (test) {
				if (Resources._isExpressionDefined(test)) {
					Resources._debug('Script already present: ' + src);
					Resources._loaded(src);
					return;
				}
				Resources.waitFor(test, function() {
					Resources._debug('Script is now loaded: ' + src);
					Resources._loaded(src);
				});
			}
			else {
				Resources._debug('No test for script: ' + src);
				Resources._loaded(src);
			}
					
			var head = document.getElementsByTagName('head')[0];
			var script = document.createElement('script'); 
			script.type = 'text/javascript';
			script.src = src;
			head.appendChild(script);
		}
	},
	
	loadScriptSequence: function(scripts) {
		if (scripts.length > 0) {
			var script = scripts.shift();
			Resources.loadScript(script.src, script.test);
			if (script.test) {
				Resources.execWhenLoaded([script.src], function() {
					Resources.loadScriptSequence(scripts);
				});
			}
			else {
				Resources.loadScriptSequence(scripts);
			}
		}
	},
	
	/**
	 * Dynamically loads a stylesheet from the given url.
	 */
	loadStyleSheet: function(url) {
		url = this.resolveUrl(url);
		if (!this.isLoaded(url)) {
			var head = document.getElementsByTagName('head')[0];
			var link = document.createElement('link');
			link.rel = 'stylesheet';
			link.type = 'text/css';
			link.href = url;
			head.appendChild(link);
			Resources._loaded(url);
		}
	},
	
	/**
	 * Loads a script using document.write(). Use this method only to load 
	 * scripts while the page is loading, otherwise the page will be blanked.
	 */
	writeScript: function(url, test) {
		url = this.resolveUrl(url);
		if (test && Resources._isExpressionDefined(test)) {
			Resources._debug('Script already present: ' + url);
			Resources._loaded(url);
			return;
		}
		document.write('<script type="text/javascript" src="' + url + '"></script>');
	},
		
	/**
	 * Executes the given callback function as soon as all resources in the
	 * res array are completely loaded.
	 */
	execWhenLoaded: function(res, callback) {
		Resources._debug('Registering callback for resources: ' + res);
		var test = function() {
			for (var i = 0; i < res.length; i++) {
				var url = Resources.resolveUrl(res[i]);
				if (!Resources.isLoaded(url)) {
					return false;
				}
			}
			Resources._debug('All resources loaded: ' + res);
			return true;
		};
		Resources.waitFor(test, callback);
	},
	
	useBasePathFromScript: function(scriptName) {
		var scriptTags = document.getElementsByTagName('script');
		for (var s = 0; s < scriptTags.length; s++) {
			var src = scriptTags[s].src;
			var i = src.indexOf(scriptName);
			if (i != -1) {
				Resources.basePath = src.substring(0, i);
				return;
			}
		}
		alert('Script not found: ' + scriptName);
	},
	
	waitFor: function(test, callback) {
		var testPassed;
		if (typeof test == 'function') {
			testPassed = test();
		}
		else {
			testPassed = Resources._isExpressionDefined(test);
		}
		if (testPassed) {
			if (typeof callback == 'function') {
				callback();
			}
			else {
				eval(callback);
			}
		}
		else {
			setTimeout(function() { 
				Resources.waitFor(test, callback); 
			}, 100);
		}
	},	
	
	LOADING: 'loading',
	
	LOADED: 'loaded',
	
	_states: {},
	
	_loading: function(url) {
		Resources._states[url] = Resources.LOADING;
	},
	
	_loaded: function(url) {
		Resources._states[url] = Resources.LOADED;
	},
	
	_isExpressionDefined: function(exp) {
		var s = exp.split('.');
		exp = '';
		for (var i = 0; i < s.length; i++) {
			exp += s[i];
			if (eval('typeof ' + exp) == 'undefined') return false;
			exp += '.';
		}
		return true;
	},
	
	_debug: function(msg) {
		if (Resources.debugEnabled) {
			alert(msg);
		}
	}
}

Resources.useBasePathFromScript('riot-js/resources.js');
Resources.debugEnabled = top.location.href.indexOf('debug-resources') != -1;
