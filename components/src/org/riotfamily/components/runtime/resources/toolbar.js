riot.Toolbar = Class.create();
riot.Toolbar.prototype = {
	
	initialize: function() {
		this.buttons = $H({
			gotoRiot: new riot.ToolbarButton('gotoRiot', '${toolbar-button.gotoRiot}', riot.path).disable(),
			browse: new riot.ToolbarButton('browse', '${toolbar-button.browse}').disable(),
			insert: new riot.ToolbarButton('insert', '${toolbar-button.insert}').disable(),
			remove: new riot.ToolbarButton('remove', '${toolbar-button.remove}').disable(),
			edit: new riot.ToolbarButton('edit', '${toolbar-button.edit}').disable(),
			properties: new riot.ToolbarButton('properties', '${toolbar-button.properties}').disable(),
			move: new riot.ToolbarButton('move', '${toolbar-button.move}').disable(),
			logout: new riot.ToolbarButton('logout', '${toolbar-button.logout}').disable()
		});
		this.buttons.logout.applyHandler = this.logout;
		
		var buttonElements = this.buttons.values().pluck('element');
		document.body.appendChild(this.element = RBuilder.node('div', {id: 'riot-toolbar'},
			RBuilder.node('div', {id: 'riot-toolbar-buttons'}, buttonElements)
		));
		document.body.appendChild(this.inspectorPanel = RBuilder.node('div', {id: 'riot-inspector'}));

		this.publishButton = new riot.ToolbarButton('publish', '${toolbar-button.publish}');
		this.publishButton.getHandlerTargets = function() { return riot.toolbar.dirtyComponentLists; };
		this.publishButton.enable = function() {
			this.enabled = true;
			new Effect.Appear(this.element, {duration: 0.4});
		}
		this.publishButton.disable = function() {
			this.enabled = false;
			this.element.hide();
		}
		this.publishButton.element.hide();
		this.element.appendChild(this.publishButton.element);
	},
	
	activate: function() {
		this.updateComponentLists();
		this.instantPublishMode = false;
		var listIds = this.componentLists.pluck('id');
		ComponentEditor.getDirtyListIds(listIds, {
			callback: this.setDirtyListIds.bind(this),
			errorHandler: Prototype.emptyFunction
		});
		this.buttons.values().invoke('enable');
		this.buttons.browse.click();
		this.keepAliveTimer = setInterval(this.keepAlive.bind(this), 60000);
	},
			
	buttonClicked: function(button) {
		if (isSet(this.activeButton)) {
			this.activeButton.reset();
			this.setInspector(null);
		}
		this.selectedComponent = null;
		this.activeButton = button;
	},
	
	buttonDisabled: function(button) {
		if (this.activeButton == button) {
			this.activeButton = null;
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
		if (isSet(this.inspector)) {
			Element.remove(this.inspector);
			this.inspector = null;
			this.hideInspector();
		}
	},
	
	showInspector: function() {
		if (isSet(this.inspector)) {
			this.inspectorPanel.style.display = 'block';
		}
	},
	
	hideInspector: function() {
		this.inspectorPanel.style.display = 'none';
	},
			
	updateComponentLists: function() {
		var lists = [];
		$$('.riot-components').each(function(e) {
			var list = e.componentList;
			if(!isDefined(list)) {
				list = new riot.ComponentList(e);
				e.componentList = list;
				if (riot.toolbar.activeButton) {
					list[riot.toolbar.handler](true);
				}
			}
			lists.push(list);
		});
		$$('.riot-single-component').each(function(e) {
			var c = new riot.Component(null, e);
			lists.push(c);
		});
		
		this.componentLists = lists.reverse();
	},
		
	setDirtyListIds: function(ids) {
		if (!ids) {
			this.instantPublishMode = true;
			return;
		}
		if (ids.length > 0) {
			this.dirtyComponentLists = this.componentLists.findAll(function(list) {
				return ids.include(list.id);
			});
			this.publishButton.enable();
		}
		else {
			this.dirtyComponentLists = [];
			this.publishButton.disable();
		}
	},
	
	setDirty: function(list, dirty) {
		if (!list) return; //TODO
		this.updateComponentLists();
		if (this.instantPublishMode) return;
		if (dirty) {
			if (!this.dirtyComponentLists.include(list)) {
				this.dirtyComponentLists.push(list);
				this.publishButton.enable();
			}
		}
		else {
			this.dirtyComponentLists = this.dirtyComponentLists.without(list);
			if (this.dirtyComponentLists.length == 0) {
				this.publishButton.disable();
			}
		}
	},
	
	keepAlive: function() {
		ComponentEditor.keepAlive();
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
			className: 'toolbar-button',
			title: title
		});
		if (href) {
			this.element.href = href;
			this.element.target = 'riot';
		}
		else {
			this.element.href = '#';
			this.element.onclick = this.click.bind(this);
		}
		this.enabled = true;
	},

	click: function() {
		if (this.enabled && !this.active) {
			this.active = true;
			this.element.className = 'toolbar-button-active';
			riot.toolbar.buttonClicked(this);
			this.applyHandler(true);
		}
		return false;
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
		if (this.active) {
			this.active = false;
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
			else {
				targets[i].setMode(enable ? this.handler : null);
			}
		}
	}
	
}

riot.toolbar = new riot.Toolbar();
