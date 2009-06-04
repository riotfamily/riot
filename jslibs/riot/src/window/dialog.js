if (!window.riot) var riot = {}; // riot namespace

riot.window = (function() {
	
	// ------------------------------------------------------------------------
	// Private fields
	// ------------------------------------------------------------------------
	
	// The root element which is clipped when a modal dialog is opened 
	var root = $$(document.compatMode && document.compatMode == 'BackCompat' ? 'body' : 'html').first();
	
	// A div to intercept clicks to underlying elements and to optionally dim the background 
	var overlay = new Element('div', {id: 'riot-overlay'});
	
	// Stack of open windows
	var stack = [];
	
	var ie6 = Prototype.Browser.IE && typeof document.documentElement.style.maxHeight == 'undefined';
	
	// ------------------------------------------------------------------------
	// Window stack management
	// ------------------------------------------------------------------------
	
	function windowOpened(dialog) {
		var top = findTopModal();
		stack.push(dialog);
		var zIndex = stack.length * 2 + 1001;
		if (dialog.options.modal) {
			if (!top) {
				var initialWidth = document.body.offsetWidth;
				Element.makeClipping(root);
				
				var h = Math.max(document.viewport.getHeight(), Element.getHeight(document.body));
				overlay.style.height = h + 'px';
				document.body.appendChild(overlay);
		
				// The call to root.makeClipping() above removes the scrollbars - add a margin to prevent visual shift. 
				var margin = (document.body.offsetWidth - initialWidth) + 'px'; 
				document.body.style.marginRight = margin;
				overlay.style.paddingRight = margin;
			}
			overlay.style.zIndex = zIndex-1;
		}
		return zIndex;
	}
	
	function windowClosed(dialog) {
		stack = stack.without(dialog);
		if (dialog.options.modal) {
			var top = findTopModal();
			if (top) {
				overlay.style.zIndex = top.box.style.zIndex - 1;
			}
			else {
				document.body.style.marginRight = 0;
				root.undoClipping();
				if (Prototype.Browser.WebKit) {
					// Force re-rendering of scrollbars in Safari
					window.scrollBy(0,-1);
					window.scrollBy(0,1);
				}
				//showElements('object');
				//showElements('embed');
				Element.remove(overlay);
			}
		}
	}
	
	function findTopModal() {
		for (var i = stack.length-1; i >= 0; i--) {
			var dlg = stack[i];
			if (dlg.options.modal) {
				return dlg;
			}
		}
		return null;
	}
	
	function hideElements(name) {
		var exclude = this.div;
		$$(name).each(function (e) {
			if (!e.childOf(exclude) && e.getStyle('visibility') != 'hidden') {
				e.makeInvisible();
				e.hidden = true;
			}
		});
	}

	function showElements(name) {
		$$(name).each(function (e) {
			if (e.hidden) {
				e.setStyle({visibility: 'visible'});
				e.hidden = false;
			}
		});
	}

	function fixPNGs(el) {
		if (ie6) {
			el.select('td').each(function(td) {
				if (td.style.filter) {
					td.style.backgroundImage = '';
				}
				var bg = td.getStyle('background-image');
				if (bg && bg != 'none') {
					bg = bg.replace(/url\(['"]?(.*?)['"]?\)/, '$1');
					td.style.backgroundImage = 'none';
					var repeat = td.getStyle('background-repeat') != 'no-repeat';
					var method = (repeat ? "scale" : "image")
					td.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + bg + "', sizingMethod='" + method +"')";
				}
			});
		}
	}
	
	// Resize overlay and center dialogs on resize events
	Event.observe(window, 'resize', function() {
		var h = Math.max(document.viewport.getHeight(), Element.getHeight(document.body));
		overlay.style.height = h + 'px';
		stack.invoke('center');
	});

	
	// ------------------------------------------------------------------------
	// Public API
	// ------------------------------------------------------------------------
	
	return {
		Dialog: Class.create({
			initialize: function(options) {
				this.options = Object.extend({
					modal: true,
					minWidth: 600,
					minHeight: 100,
					openOnLoad: true
				}, options);
				
				this.box = new Element('table').addClassName('riot-dialog')
					.setStyle({position: 'absolute', top: 0, left: 0}).insert(
					'<tr><td class="border border-top-left"></td><td class="border border-top"></td><td class="border border-top-right"></td></tr>' +
					'<tr><td class="border border-left"></td><td class="pane">' +
					'<div class="title-bar"></div><div class="content"></div>' + 
					'</td><td class="border border-right"></td></tr>' +
					'<tr><td class="border border-bottom-left"></td><td class="border border-bottom"></td><td class="border border-bottom-right"></td></tr>');
				
				this.content = this.box.down('.content');
				this.pane = this.box.down('.pane');
				
				var t = this.box.down('.title-bar');
				if (this.options.title) {
					t.insert('<div class="title">' + this.options.title + '</div>');
				}
				if (this.options.closeButton) {
					t.insert(new Element('div').addClassName('close-button').observe('click', this.close.bind(this)));
				}
				
				if (this.options.url) {
					this.box.style.visibility = 'hidden';
					this.iframe = new Element('iframe', {src: this.options.url, width: '100%'}).observe('load', function() {
						this.resize();
						this.box.style.visibility = 'visible';
					}.bind(this));
					this.content.update(this.iframe);
				}
				else {
					this.content.update(this.options.content);
				}
				if (this.options.openOnLoad) {
					this.open();
				}
			},
			
			center: function() {
				var top = Math.max(5, Math.round(document.viewport.getHeight() / 2 - this.box.clientHeight / 2));
				var left = Math.round(document.viewport.getWidth() / 2 - this.box.clientWidth / 2);
				if (this.box.getStyle('position') != 'fixed') {
					var scroll = document.viewport.getScrollOffsets();
					top += scroll.top;
					left += scroll.left;
				}
				this.box.style.top = top + 'px';
				this.box.style.left = left + 'px';
			},
			
			resize: function() {
				var el = this.content;
				if (this.iframe) {
					var doc = this.iframe.contentWindow || this.iframe.contentDocument;
					if (doc.document) {
						doc = doc.document;
					}
					el = doc.body;
				}

				var w = Math.max(this.options.minWidth, el.offsetWidth);
				w = Math.min(w, document.viewport.getWidth() - 50);
				this.pane.style.width = w + 'px';
				
				var h = Math.max(this.options.minHeight, el.offsetHeight);
				h = Math.min(h, document.viewport.getHeight() - 100); 
				this.pane.style.height = h + 'px';
				if (this.iframe) {
					this.iframe.style.height = this.pane.style.height; 
				}
				this.center();
			},
			
			open: function() {
				if (!this.isOpen) {
					document.body.appendChild(this.box);
					fixPNGs(this.box);
					this.resize();
					this.box.style.zIndex = windowOpened(this);
					this.isOpen = true;
				}
			},
			
			close: function() {
				this.box.remove();
				windowClosed(this);
				this.isOpen = false;
			}
		}),
		
		alert: function(msg, onclose) {
			ask(null, msg, ['Ok'], onclose);
		},
	
		ask: function(title, question, answers, callback) {
			var dlg;	
			var buttons = new Element('form').addClassName('buttons');
			answers.each(function(s, i) {
				buttons.insert(new Element('input', {type: 'button', value: s}).observe('click', function() {
					dlg.close();
					if (callback) {
						callback(i, s);
					}
				}));
			});
			
			var content = new Element('div')
				.insert(new Element('div').addClassName('message question').insert(question))
				.insert(buttons);
				
			dlg = new this.Dialog({title: title, content: content});
		},
		
		closeAll: function() {
			stack.invoke('close');
		}
	
	}
})(); 

/*
	open: function() {
		if (Prototype.Browser.IE) {
			this.hideElements('select');
		}
		this.hideElements('object');
		this.hideElements('embed');
	},

	close: function() {
		Event.stopObserving(document, 'keydown', this.keyDownHandler);
		if (riot.activePopup == this) {
			if (Prototype.Browser.IE) {
				this.showElements('select');
			}
		}
	},

	handleKeyDown: function(ev) {
		if (ev.keyCode == Event.KEY_ESC) {
			Event.stop(ev);
			this.close();
		}
	}
});
*/
