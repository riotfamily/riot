if (!window.riot) var riot = {}; // riot namespace

riot.form = (function () {

	var handlers = {
		insert: function (el, action) {
			el.insert(action.html);
		},
		remove: function (el, action) {
			el.remove();
		},
		replace: function (el, action) {
			el.replace(action.html);
		},
		update: function (el, action) {
			el.update(action.html);
		},
		schedule: function (el, action) {
			setTimeout(function() {
				riot.form.submitEvent(el, action.handler, action.value);
			}, action.millis);
		}
	}
	
	function processAction(a) {
		if (handlers[a.command]) {
			var el = $(a.id);
			if (a.selector) {
				el = el.down(a.selector);
			}
			handlers[a.command](el, a);
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
		
		processActions: function(actions) {
			$A(actions).each(processAction);
		},
	
		submitEvent: function (el, handler, value) {
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
