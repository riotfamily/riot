if (!window.riot) var riot = {};

riot.form = (function($) {

	var formUrl;
	
	var resourceBaseUrl;
	
	var handlers = {
		invoke: function(el, data) {
			el[data.method].apply(el, data.args);
		},
		'eval': function(el, data) {
			eval(data.script);
		},
		schedule: function(el, data) {
			setTimeout(function() {
				el.submitEvent(data.handler, data.value);
			}, data.millis);
		}
	};
	
	function getElement(id, selector) {
		var el = $('#' + id);
		if (selector) {
			el = el.find(selector);
		}
		return el;
	}
	
	function loadStylesheets(stylesheets) {
		$.each(stylesheets, function() { 
			$('<link>', {rel:  'stylesheet', type: 'text/css', href: resourceBaseUrl + this}).appendTo('head');
		});	
	}
	
	function isPresent(exp) {
		var s = exp.split('.');
		exp = '';
		for (var i = 0; i < s.length; i++) {
			exp += s[i];
			if (eval('typeof ' + exp) == 'undefined') {
				return false;
			}
			exp += '.';
		}
		return true;
	}
	
	function loadScripts(scripts, callback) {
		if (scripts && scripts.length > 0) {
			var s = scripts.shift();
			if (!s.test || !isPresent(s.test)) {
				$.getScript(resourceBaseUrl + s.url, function() {
					loadScripts(scripts, callback);
				});
			}
			else {
				loadScripts(scripts, callback);
			}
		}
		else if (callback) {
			callback();
		}
	} 
	
	function loadResources(resources, callback) {
		if (resources.stylesheets) {
			loadStylesheets(resources.stylesheets);
		}
		loadScripts(resources.scripts, callback);
	}
	
	function processActions(actions) {
		if (actions) {
			$.each(actions, function() {
				if (handlers[this.command]) {
					var el = getElement(this.id, this.selector);
					handlers[this.command](el, this.data);
				}
			});
		}
	}
	
	function processUpdate(update) {
		loadResources(update, function() {
			processActions(update.actions);
		});
	}
	
	$.fn.submitEvent = function(handler, value) {
		$.ajax({
			url: formUrl,
			data: {
				formId: this.closest('.FormElement').attr('id'),
				stateId: this.closest('.state').attr('id'),
				handler: handler,
				value: value
			},
			dataType: 'json',
			success: processUpdate
		});
		return this;
	};

		
	// ------------------------------------------------------------------------
	// Public API - These functions are invoked by init-scripts and event-
	// handlers.
	// ------------------------------------------------------------------------
	
	return {
		
		invoke: function(id, selector, method, args) {
			var el = getElement(id, selector);
			el[method].apply(el, args);
		},
		
		setUrl: function(url) {
			formUrl = url;
		},
		
		setResourceBaseUrl: function(url) {
			resourceBaseUrl = url;
		},
		
		/**
		 * Usually updates are processed as result of a submitEvent() call. This
		 * function allows the server to use the same functionality inside an 
		 * init-script.
		 */
		processUpdate: processUpdate,
		
		loadScripts: loadScripts,
		
		/**
		 * Setup-callback for TinyMCE instances that registers an onchange and
		 * onblur listener to submit the content to the server.
		 */
		tinymceSetup: function(e) {
			function save() {
				if (e.isDirty()) {
					e.save();
					e.getElement().onchange();
				}
			}
			e.onChange.add(save);
			e.onInit.add(function() {
				e.getWin().onblur = save;
			});
		}
	};
	
})(jQuery);

