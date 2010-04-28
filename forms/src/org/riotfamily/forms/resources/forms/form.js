if (!window.riot) var riot = {};

riot.form = (function($) {

	var formUrl;
	
	var handlers = {
		invoke: function(el, data) {
			el[data.method].apply(el, data.args);
		},
		/*
		each: function(el, data) {
			el.find(data.selector).each(function() {
				var el = $(this);
				el[data.method].apply(el, data.args);
			})
		},
		*/
		eval: function(el, data) {
			eval(data.script);
		},
		schedule: function(el, data) {
			setTimeout(function() {
				el.submitEvent(data.handler, data.value);
			}, data.millis);
		}
	}
	
	function getElement(id, selector) {
		var el = $('#' + id);
		if (selector) {
			el = el.find(selector);
		}
		return el;
	}
	
	function processAction() {
		if (handlers[this.command]) {
			var el = getElement(this.id, this.selector);
			handlers[this.command](el, this.data);
		}
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
			success: function(actions) {
				$.each(actions, processAction);
			}
		});
		return this;
	}

		
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
		
		/**
		 * Usually actions are processed as result of a submitEvent() call. This
		 * function allows the server to use the same functionality inside an 
		 * init-script.
		 */
		processActions: function(actions) {
			$.each(actions, processAction);
		},
		
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
			})
		}
	}
	
})(jQuery);
