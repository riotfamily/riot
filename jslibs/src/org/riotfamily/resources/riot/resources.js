if (!window.riot) var riot = {}; // riot namespace

riot.Resources = (function() {

	var basePath = '/';
	var LOADING = 'loading';	
	var LOADED = 'loaded';
	
	/**
	 * Returns true if the given resource url has already been loaded.
	 */
	function isLoaded(url) {
		return states[url] == LOADED;
	}
	
	var stopLoading = false;

	var states = {};

	function loading(url) {
		states[url] = LOADING;
	}

	function loaded(url) {
		states[url] = LOADED;
	}

	function isExpressionDefined(exp) {
		var s = exp.split('.');
		exp = '';
		for (var i = 0; i < s.length; i++) {
			exp += s[i];
			if (eval('typeof ' + exp) == 'undefined') return false;
			exp += '.';
		}
		return true;
	}

	function debug(msg) {
		if (debugEnabled) {
			if (typeof console != 'undefined') console.log(msg); else alert(msg);
		}
	}
	
	debugEnabled = top.location.href.indexOf('debug-resources') != -1;
	
	// Add an unload handler to stop loading when the user leaves the page before
	// all resources have finished loading. Otherwise exceptions might be raised
	// when a script tries to access another resource that has already been garbage
	// collected.
	if (window.addEventListener) {
		window.addEventListener('unload', function() { stopLoading = true }, false);
	}
	else if (window.attachEvent) {
	    window.attachEvent('onunload', function() { stopLoading = true });
	}
	
	return {

		setBasePath: function(path) {
			basePath = path;
			return this;
		},
		
		/**
		 * Adds the basePath to urls that don't start with a slash.
		 */
		resolveUrl: function(url) {
			if (url.charAt(0) != '/') {
				url = basePath + url;
			}
			return url;
		},
	
		/**
		 * Dynamically loads a script from the given url. Optionally a test
		 * may be specified to check whether the script is (already) loaded.
		 */
		loadScript: function(src, test, onload) {
			if (stopLoading) return;
			src = this.resolveUrl(src);
			debug('Script requested: ' + src);
			if (!isLoaded(src)) {
				loading(src);
				if (test) {
					if (isExpressionDefined(test)) {
						debug('Script already present: ' + src);
						loaded(src);
						return;
					}
					this.waitFor(test, function() {
						debug('Script is now loaded: ' + src);
						loaded(src);
						if (onload) onload();
					});
				}
				else {
					debug('No test for script: ' + src);
					loaded(src);
				}
				this.insertScript(src);	
			}
			return this;
		},
		
		insertScript: function(src) {
			var head = document.getElementsByTagName('head')[0];
			var script = document.createElement('script');
			script.type = 'text/javascript';
			script.src = src;
			head.appendChild(script);
			return this;
		},
	
		getRequiredSources: function(scripts) {
			var result = [];
			for (var i = 0; i < scripts.length; i++) {
				var script = scripts[i];
				if (!(script.test && isExpressionDefined(script.test))) {
					result.push(script.src);
				}
			}
			return result;
		},
		
		loadScriptSequence: function(scripts) {
			if (stopLoading) return;
			if (scripts.length > 0) {
				var script = scripts.shift();
				this.loadScript(script.src, script.test);
				if (script.test) {
					this.execWhenLoaded([script.src], function() {
						if (script.onload) script.onload();
						riot.Resources.loadScriptSequence(scripts);
					});
				}
				else {
					this.loadScriptSequence(scripts);
				}
			}
			return this;
		},
	
		/**
		 * Dynamically loads a stylesheet from the given url.
		 */
		loadStyleSheet: function(url) {
			url = this.resolveUrl(url);
			if (!isLoaded(url)) {
				var head = document.getElementsByTagName('head')[0];
				var link = document.createElement('link');
				link.rel = 'stylesheet';
				link.type = 'text/css';
				link.href = url;
				head.appendChild(link);
				loaded(url);
			}
			return this;
		},
	
		/**
		 * Loads a script using document.write(). Use this method only to load
		 * scripts while the page is loading, otherwise the page will be blanked.
		 */
		writeScript: function(url, test) {
			url = this.resolveUrl(url);
			if (test && isExpressionDefined(test)) {
				debug('Script already present: ' + url);
				loaded(url);
				return;
			}
			document.write('<script type="text/javascript" src="' + url + '"></script>');
			return this;
		},
	
		waitFor: function(test, callback) {
			var testPassed;
			if (typeof test == 'function') {
				testPassed = test();
			}
			else {
				testPassed = isExpressionDefined(test);
			}
			if (testPassed) {
				if (typeof callback == 'function') {
					callback();
				}
				else {
					eval(callback);
				}
			}
			else if (!stopLoading) {
				setTimeout(function() {
					riot.Resources.waitFor(test, callback);
				}, 100);
			}
			return this;
		}, 
		
		/**
		 * Executes the given callback function as soon as all resources in the
		 * res array are completely loaded.
		 */
		execWhenLoaded: function(res, callback) {
			debug('Registering callback for resources: ' + res);
			var test = function() {
				for (var i = 0; i < res.length; i++) {
					var url = riot.Resources.resolveUrl(res[i]);
					if (!isLoaded(url)) {
						return false;
					}
				}
				debug('All resources loaded: ' + res);
				return true;
			};
			this.waitFor(test, callback);
			return this;
		}
	}
})();

