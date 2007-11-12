riot.Toolbar = Class.create({
	initialize: function() {
		this.buttons = $H({
			gotoRiot: new riot.ToolbarButton('gotoRiot', '${toolbarButton.gotoRiot}', null, riot.path),
			browse: new riot.ToolbarButton('browse', '${toolbarButton.browse}'),
			insert: new riot.ToolbarButton('insert', '${toolbarButton.insert}', '.riot-list'),
			remove: new riot.ToolbarButton('remove', '${toolbarButton.remove}', '.riot-component-list'),
			edit: new riot.ToolbarButton('edit', '${toolbarButton.edit}', '.riot-text-editor'),
			editImages: new riot.ToolbarButton('editImages', '${toolbarButton.editImages}', '.riot-image-editor'),
			properties: new riot.ToolbarButton('properties', '${toolbarButton.properties}', '.riot-form'),
			move: new riot.ToolbarButton('move', '${toolbarButton.move}', '.riot-component-list'),
			logout: new riot.ToolbarButton('logout', '${toolbarButton.logout}')
		});

		if (!riot.instantPublish) {
			this.buttons.set('discard', new riot.ToolbarButton('discard', '${toolbarButton.discard}', '.riot-controller'));
			this.buttons.set('publish', new riot.ToolbarButton('publish', '${toolbarButton.publish}', '.riot-controller'));
		}

		this.buttons.get('logout').applyHandler = this.logout;

		var buttonElements = this.buttons.values().pluck('element');
		document.body.appendChild(this.element = RBuilder.node('div', {id: 'riot-toolbar'},
			RBuilder.node('div', {id: 'riot-toolbar-buttons'}, buttonElements)
		));
		document.body.appendChild(this.inspectorPanel = RBuilder.node('div', {id: 'riot-inspector'}));

		if (!riot.instantPublish) {
			this.applyButton = new riot.ToolbarButton('apply', '${toolbarButton.apply}', '.riot-controller').activate();
			this.applyButton.enable = function() {
				this.enabled = true;
				new Effect.Appear(this.element, {duration: 0.4});
			}
			this.applyButton.disable = function() {
				this.enabled = false;
				this.element.hide();
			}
			this.applyButton.click = function() {
				riot.publishWidgets.invoke('applyChanges');
				riot.toolbar.buttons.get('browse').click();
			}
			this.applyButton.element.hide();
			this.element.appendChild(this.applyButton.element);
		}
	},

	activate: function() {
		if (!riot.instantPublish) {
			var dirty = typeof document.body.down('.riot-dirty') != 'undefined';
			if (!dirty) this.disablePublishButtons();
		}
		this.buttons.values().invoke('activate');
		this.buttons.get('browse').click();
		this.keepAliveTimer = setInterval(this.keepAlive.bind(this), 60000);
	},

	enablePublishButtons: function() {
		if (!riot.instantPublish) {
			this.buttons.get('publish').enable();
			this.buttons.get('discard').enable();
		}
	},
	
	disablePublishButtons: function() {
		if (!riot.instantPublish) {
			this.buttons.get('publish').disable();
			this.buttons.get('discard').disable();
		}
	},
	
	buttonClicked: function(button) {
		if (this.selectedButton) {
			this.selectedButton.reset();
			this.setInspector(null);
		}
		this.selectedComponent = null;
		this.selectedButton = button;
	},

	buttonDisabled: function(button) {
		if (this.selectedButton == button) {
			this.selectedButton = null;
		}
	},

	setInspector: function(inspector) {
		if (inspector != this.inspector) {
			this.removeInspector();
			if (inspector != null) {
				this.inspector = inspector;
				this.inspectorPanel.appendChild(inspector);
				this.showInspector();
			}
		}
	},

	removeInspector: function() {
		if (this.inspector) {
			Element.remove(this.inspector);
			this.inspector = null;
			this.hideInspector();
		}
	},

	showInspector: function() {
		if (this.inspector) {
			this.inspectorPanel.style.display = 'block';
		}
	},

	hideInspector: function() {
		this.inspectorPanel.style.display = 'none';
	},

	restoreMode: function(el) {
		if (this.selectedButton &&
				this.selectedButton != this.buttons.get('publish') &&
				this.selectedButton != this.buttons.get('discard')) {

			this.selectedButton.reApplyHandler(el);
		}
	},

	keepAlive: function() {
		ComponentEditor.keepAlive({
			errorHandler: Prototype.emptyFunction
		});
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
		this.element = RBuilder.node('a', {
			id: 'riot-toolbar-button-' + handler,
			className: 'toolbar-button-disabled',
			title: title
		});
		this.selector = selector;
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

	onclick: function() {
		if (this.active) this.click();
		return false;
	},

	click: function() {
		if (this.enabled && !this.selected) {
			this.selected = true;
			this.element.className = 'toolbar-button-active';
			riot.toolbar.buttonClicked(this);
			this.applyHandler(true);
		}
		return false;
	},

	activate: function() {
		this.active = true;
		this.element.className = this.enabled ? 'toolbar-button' : 'toolbar-button-disabled';
		return this;
	},

	enable: function() {
		if (!this.enabled) {		
			this.element.className = 'toolbar-button';
			this.enabled = true;
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
		this.element.className = this.enabled ?
				'toolbar-button' : 'toolbar-button-disabled';

		return this;
	},

	getHandlerTargets: function(root) {
		// The reverse is required for the Sortable of nested lists
		return Selector.findChildElements(root, [this.selector]).reverse();
	},

	applyHandler: function(enable) {
		if (enable && this.precondition) {
			this.precondition(this.applyHandlerInternal.bind(this, document, true));
		}
		else {
			if (this.beforeApply) {
				this.beforeApply(enable);
			}
			this.applyHandlerInternal(document, enable);
			if (this.afterApply) {
				this.afterApply(enable);
			}
		}
	},
	
	applyHandlerInternal: function(root, enable) {
		var targets = this.getHandlerTargets(root);
		for (var i = 0; i < targets.length; i++) {
			var target = riot.getWrapper(targets[i], this.selector)
			var method = this.handler + (enable ? 'On' : 'Off');
			if (target[method]) {
				target[method]();
			}
		}
	},
	
	reApplyHandler: function(root) {
		this.applyHandlerInternal(root, true);
	}
});

riot.toolbar = new riot.Toolbar();
