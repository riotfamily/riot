if (!window.riot) var riot = {}; // riot namespace

riot.form = (function() {

	var handlers = {
		invoke: function(el, data) {
			el[data.method].apply(el, data.args);
		},
		each: function(el, data) {
			el.select(data.selector).each(function(e) {
				e[data.method].apply(e, data.args);	
			});
		},
		moveUp: function(el) {
			var prev = el.previous();
	        if (prev) prev.insert({before: el.remove()});
		},
		moveDown: function(el) {
			var next = el.next();
	        if (next) next.insert({after: el.remove()});
		},
		schedule: function(el, data) {
			setTimeout(function() {
				riot.form.submitEvent(el, data.handler, data.value);
			}, data.millis);
		}
	}
	
	function processAction(a) {
		if (handlers[a.command]) {
			var el = $(a.id);
			if (a.selector) {
				el = el.down(a.selector);
			}
			handlers[a.command](el, a.data);
		}
	}
	
	function getId(el, sel) {
		if (el.match(sel)) {
			return el.id;
		}
		el = el.up(sel);
		return el ? el.id : null;
	}
	
	// ------------------------------------------------------------------------
	// Public API
	// ------------------------------------------------------------------------
	
	return {
		
		loadStyleSheet: function(url) {
			$$('head')[0].insert(new Element('link', {rel: 'stylesheet', type: 'text/css', href: url}));
		},
		
		processActions: function(actions) {
			$A(actions).each(processAction);
		},
			
		submitEvent: function(el, handler, value) {
			el = $(el);
			new Ajax.Request(window.location.href, {
				onSuccess: function(transport) {
					transport.responseJSON.each(processAction);
				},
				requestHeaders: {
					Accept: 'application/json'
				},
				parameters: {
					formId: getId(el, '.form'),
					stateId: getId(el, '.state'),
					handler: handler,
					value: value
				}
			});
		}
	}
	
})();
