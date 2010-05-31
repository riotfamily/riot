riot.Toolbar = Class.create({
	initialize: function() {
		this.edit = true;
		this.publish = true;
		this.buttons = $H({
			browse: new riot.ToolbarButton('browse', '${toolbarButton.browse}'),
			insert: new riot.ToolbarButton('insert', '${toolbarButton.insert}', '.riot-component-list'),
			remove: new riot.ToolbarButton('remove', '${toolbarButton.remove}', '.riot-component-list'),
			edit: new riot.ToolbarButton('edit', '${toolbarButton.edit}', '.riot-text-editor'),
			properties: new riot.ToolbarButton('properties', '${toolbarButton.properties}', '.riot-form'),
			move: new riot.ToolbarButton('move', '${toolbarButton.move}', '.riot-component-list'),
			preview: new riot.ToolbarButton('preview', '${toolbarButton.preview}'),
			logout: new riot.ToolbarButton('logout', '${toolbarButton.logout}')
		});
		
		this.disablePreviewButton();
		
		var buttonsDiv = new Element('div', {id: 'riot-toolbar-buttons'});
		this.buttons.values().invoke('insert', buttonsDiv);
		
		this.element = new Element('div', {id: 'riot-toolbar'});
		if (Prototype.Browser.IE) {
			this.element.insert(new Element('div', {id: 'riot-toolbar-ie-shadow'}));
		}
		
		this.element.insert(
			new Element('div', {id: 'riot-toolbar-content'})
				.insert(new Element('div', {id: 'riot-toolbar-title'}))
				.insert(buttonsDiv)
		);
		
		document.body.appendChild(this.element);

		var cookie = new CookieJar({expires: 604800});
		var pos = cookie.get('toolbarPos');
		if (pos) {
			this.element.setStyle(pos);
		}
		
		new Draggable('riot-toolbar', {
			handle: 'riot-toolbar-title', 
			starteffect: null, 
			endeffect: null,
			//scroll: this.element.getStyle('position') == 'absolute' ? 'window' : null
			onEnd: function(d, ev) {
				var s = d.element.style;
				cookie.put('toolbarPos', {top: s.top, left: s.left});
			}
		});
	},

	setState: function(state) {
		this.state = state;
		if (state.edit) { 
			this.buttons.values().invoke('activate');
			this.buttons.get('browse').select();
		}
		if (state.dirty) {
			this.enablePreviewButton();
		}
	},
	
	enablePreviewButton: function() {
		if (this.state.containerIds.length > 0) {
			this.buttons.get('preview').enable();
		}
	},
	
	disablePreviewButton: function() {
		this.buttons.get('preview').disable();
	},
	
	buttonSelected: function(button) {
		if (this.selectedButton) {
			this.selectedButton.reset();
		}
		this.selectedButton = button;
	},

	buttonDisabled: function(button) {
		if (this.selectedButton == button) {
			this.selectedButton = null;
		}
	},

	logout: function() {
		ComponentEditor.logout(function() {
			location.reload();
		});
	}
});

riot.ToolbarButton = Class.create({
	initialize: function(handler, title, selector, href) {
		this.handler = handler;
		this.title = title.stripTags();
		this.selector = selector;
		this.element = this.createElement();
		
		if (href) {
			this.element.href = href;
			this.element.target = 'riot';
		}
		else {
			this.element.href = '#';
			this.element.onclick = this.onclick.bind(this);
		}
		this.enabled = true;
	},
	
	createElement: function() {
		return new Element('a', {title: this.title}).addClassName(this.getClassName());
	},
	
	insert: function(target) {
		target.insert(this.element.wrap(new Element('div', {id: 'riot-toolbar-button-' + this.handler})));
	},
	
	getClassName: function() {
		var className = 'toolbar-button';
		if (this.selected) {
			className += '-active';
		}
		else if (!this.enabled) {
			className += '-disabled';
		}
		return className;
	},
	
	onclick: function() {
		if (this.active) this.click();
		return false;
	},

	select: function() {
		if (this.enabled && !this.selected) {
			this.selected = true;
			this.element.className = this.getClassName();
			riot.toolbar.buttonSelected(this);
			return true;
		}
		return false;
	},

	click: function() {
		if (this.select()) {
			this.applyHandler(true);
		}
	},

	activate: function() {
		this.active = true;
		this.element.className = this.getClassName();
		return this;
	},

	enable: function() {
		if (!this.enabled) {		
			this.enabled = true;
			this.element.className = this.getClassName();
		}		
		return this;
	},

	disable: function() {
		this.enabled = false;
		this.reset();
		if (riot.toolbar) riot.toolbar.buttonDisabled(this);
		return this;
	},

	reset: function() {
		if (this.selected) {
			this.selected = false;
			this.applyHandler(false);
		}
		this.element.className = this.getClassName();
		return this;
	},

	getHandlerTargets: function() {
		return $(document.body).select(this.selector);
	},

	applyHandler: function(enable) {
		if (enable && this.precondition) {
			this.precondition(this.applyHandlerInternal.bind(this, true));
		}
		else {
			if (this.beforeApply) {
				this.beforeApply(enable);
			}
			this.applyHandlerInternal(enable);
			if (this.afterApply) {
				this.afterApply(enable);
			}
			if (enable && window.onRiotToolbarClick) {
				onRiotToolbarClick(this.handler);
			}
		}
	},
	
	applyHandlerInternal: function(enable) {
		dwr.engine.setActiveReverseAjax(true);
		if (this.selector) {
			var targets = this.getHandlerTargets();
			for (var i = 0; i < targets.length; i++) {
				var target = riot.components.getWrapper(targets[i], this.selector);
				var method = this.handler + (enable ? 'On' : 'Off');
				if (target[method]) {
					target[method]();
				}
			}
		}
		else {
			var method = this.handler + (enable ? 'On' : 'Off');
			if (riot.components[method]) {
				riot.components[method]();
			}
		}
	},
	
	reApplyHandler: function() {
		this.applyHandler(false);
		this.applyHandler(true);
	}
});

riot.toolbar = new riot.Toolbar();

riot.showNotification = function(message) {
	var el = $('riot-notification');
	if (!el) {
		el = new Element('div', {id: 'riot-notification'});
		document.body.appendChild(el);
	}
	el.innerHTML = message;
	dwr.engine.setActiveReverseAjax(false);
};
