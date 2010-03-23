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
		el = el.match(sel) ? el : el.up(sel);
		return el ? el.id : null;
	}
	
	// ------------------------------------------------------------------------
	// Public API - These functions are invoked by init-scripts and event-
	// handlers.
	// ------------------------------------------------------------------------
	
	return {
		
		/**
		 * Dynamically loads a stylesheet by appending a new link element into the head element.
		 */
		loadStyleSheet: function(url) {
			$$('head')[0].insert(new Element('link', {rel: 'stylesheet', type: 'text/css', href: url}));
		},
		
		/**
		 * Counts the number of previous siblings that match the given selector.
		 * If the specified element does not match itself,  the function goes 
		 * up the DOM to the next matching ancestor.   
		 */
		indexOf: function(el, sel) {
			var p = el.match(sel) ? el : el.up(sel);
			return p.previousSiblings().select(function(el) { return el.match(sel) }).length;
		},
		
		/**
		 * Submits a client-side event to the server. The function performs an
		 * AJAX request using the originating URL and passes formId, stateId,
		 * handler and value as parameters.
		 */
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
		},
		
		/**
		 * Usually actions are processed as result of a submitEvent() call. This
		 * function allows the server to use the same functionality inside an 
		 * init-script.
		 */
		processActions: function(actions) {
			$A(actions).each(processAction);
		}
	}
	
})();
