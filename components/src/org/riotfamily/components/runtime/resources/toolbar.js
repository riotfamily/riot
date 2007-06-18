riot.Toolbar = Class.create();
riot.Toolbar.prototype = {

	initialize: function() {
		this.buttons = $H({
			gotoRiot: new riot.ToolbarButton('gotoRiot', '${toolbarButton.gotoRiot}', riot.path),
			browse: new riot.ToolbarButton('browse', '${toolbarButton.browse}'),
			insert: new riot.ToolbarButton('insert', '${toolbarButton.insert}'),
			remove: new riot.ToolbarButton('remove', '${toolbarButton.remove}'),
			edit: new riot.ToolbarButton('edit', '${toolbarButton.edit}'),
			properties: new riot.ToolbarButton('properties', '${toolbarButton.properties}'),
			move: new riot.ToolbarButton('move', '${toolbarButton.move}'),
			discard: new riot.ToolbarButton('discard', '${toolbarButton.discard}'),
			publish: new riot.ToolbarButton('publish', '${toolbarButton.publish}'),
			logout: new riot.ToolbarButton('logout', '${toolbarButton.logout}')
		});
		this.buttons.logout.applyHandler = this.logout;

		var buttonElements = this.buttons.values().pluck('element');
		document.body.appendChild(this.element = RBuilder.node('div', {id: 'riot-toolbar'},
			RBuilder.node('div', {id: 'riot-toolbar-buttons'}, buttonElements)
		));
		document.body.appendChild(this.inspectorPanel = RBuilder.node('div', {id: 'riot-inspector'}));

		this.applyButton = new riot.ToolbarButton('apply', '${toolbarButton.apply}').activate();
		this.applyButton.enable = function() {
			this.enabled = true;
			new Effect.Appear(this.element, {duration: 0.4});
		}
		this.applyButton.disable = function() {
			this.enabled = false;
			this.element.hide();
		}
		this.applyButton.click = function() {
			this.getHandlerTargets().invoke('apply');
			riot.toolbar.buttons.browse.click();
		}
		this.applyButton.element.hide();
		this.element.appendChild(this.applyButton.element);
		this.componentLists = [];
	},

	activate: function() {
		this.updateComponentLists();
		this.buttons.values().invoke('activate');
		this.buttons.browse.click();
		this.keepAliveTimer = setInterval(this.keepAlive.bind(this), 60000);
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

	updateComponentLists: function() {
		// We have to reverse the list, otherwise moving nested components won't work
		this.componentLists = riot.createComponentLists().reverse();
		this.dirtyCheck();
	},

	evictComponentLists: function(orphanedLists) {
		if (orphanedLists && orphanedLists.length > 0) {
			this.componentLists = this.componentLists.findAll(function(list) {
				return !orphanedLists.include(list);
			});
		}
	},

	registerComponentLists: function(lists) {
		lists.each(this.restoreMode.bind(this));
		this.componentLists = lists.concat(this.componentLists);
	},

	restoreMode: function(componentList) {
		if (this.selectedButton &&
				this.selectedButton != this.buttons.publish &&
				this.selectedButton != this.buttons.discard) {

			componentList[this.selectedButton.handler](true);
		}
	},

	dirtyCheck: function(callerIsDirty) {
		if (callerIsDirty || this.componentLists.pluck('dirty').any()) {
			this.buttons.publish.enable();
			this.buttons.discard.enable();
		}
		else {
			this.buttons.publish.disable();
			this.buttons.discard.disable();
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
}

riot.ToolbarButton = Class.create();
riot.ToolbarButton.prototype = {

	initialize: function(handler, title, href) {
		this.handler = handler;
		this.element = RBuilder.node('a', {
			id: 'riot-toolbar-button-' + handler,
			className: 'toolbar-button-disabled',
			title: title
		});
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
		this.element.className = 'toolbar-button';
		this.enabled = true;
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

	getHandlerTargets: function() {
		return riot.toolbar.componentLists;
	},

	applyHandler: function(enable) {
		var targets = this.getHandlerTargets();
		for (var i = 0; i < targets.length; i++) {
			if (targets[i][this.handler]) {
				targets[i][this.handler](enable);
			}
		}
	}

}

riot.toolbar = new riot.Toolbar();
