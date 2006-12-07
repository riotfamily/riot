riot.Toolbar = Class.create();
riot.Toolbar.prototype = {
	
	initialize: function() {
		this.buttons = $H({
			gotoRiot: new riot.ToolbarButton('gotoRiot', '${toolbar-button.gotoRiot}', riot.path),
			browse: new riot.ToolbarButton('browse', '${toolbar-button.browse}'),
			insert: new riot.ToolbarButton('insert', '${toolbar-button.insert}'),
			remove: new riot.ToolbarButton('remove', '${toolbar-button.remove}'),
			edit: new riot.ToolbarButton('edit', '${toolbar-button.edit}'),
			properties: new riot.ToolbarButton('properties', '${toolbar-button.properties}'),
			move: new riot.ToolbarButton('move', '${toolbar-button.move}'),
			logout: new riot.ToolbarButton('logout', '${toolbar-button.logout}')
		});
		var buttonElements = this.buttons.values().pluck('element');
		document.body.appendChild(this.element = RBuilder.node('div', {id: 'riot-toolbar'},
			RBuilder.node('div', {id: 'riot-toolbar-buttons'}, buttonElements)
		));
		document.body.appendChild(this.inspectorPanel = RBuilder.node('div', {id: 'riot-inspector'}));

		this.buttons.logout.applyHandler = this.logout;
	
		this.buttons.publish = new riot.ToolbarButton('publish', '${toolbar-button.publish}');
		this.buttons.publish.getHandlerTargets = function() { return riot.toolbar.dirtyComponentLists; };
		this.buttons.publish.disable();
		this.element.appendChild(this.buttons.publish.element);
		
		this.updateComponentLists();
		this.instantPublishMode = false;
		var listIds = this.componentLists.pluck('id');
		ComponentEditor.getDirtyListIds(listIds, {
			callback: this.setDirtyListIds.bind(this),
			errorHandler: Prototype.emptyFunction
		});
		
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
		
	isWithin: function(event) {
		return Event.within(event, this.element) 
				|| Event.within(event, this.inspectorPanel);
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
		var e = document.getElementsByClassName('riot-components');
		for (var i = 0; i < e.length; i++) {
			var list = e[i].componentList;
			if(!isDefined(list)) {
				list = new riot.ComponentList(e[i]);
				e[i].componentList = list;
				if (this.activeButton) {
					list[this.activeButton.handler](true);
				}
			}
			lists.push(list);
		}
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
			this.buttons.publish.enable();
		}
		else {
			this.dirtyComponentLists = [];
			this.buttons.publish.disable();
		}
	},
	
	setDirty: function(list, dirty) {
		this.updateComponentLists();
		if (this.instantPublishMode) return;
		if (dirty) {
			if (!this.dirtyComponentLists.include(list)) {
				this.dirtyComponentLists.push(list);
				this.buttons.publish.enable();
			}
		}
		else {
			this.dirtyComponentLists = this.dirtyComponentLists.without(list);
			if (this.dirtyComponentLists.length == 0) {
				this.buttons.publish.disable();
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
	},
	
	disable: function() {
		this.enabled = false;
		this.reset();
	},

	reset: function() {
		if (this.active) {
			this.active = false;
			this.applyHandler(false);
		}
		this.element.className = this.enabled ? 
				'toolbar-button' : 'toolbar-button-disabled';
	},

	getHandlerTargets: function() {
		return riot.toolbar.componentLists;
	},
	
	applyHandler: function(enable) {
		var targets = this.getHandlerTargets();
		for (var i = 0; i < targets.length; i++) {
			targets[i][this.handler](enable);
		}
	}
	
}

DWREngine.setErrorHandler(function(err, ex) {
	if (err == 'Request context has expired') { // RequestContextExpiredException
		location.reload();
	}
	else {
		alert(err);
	}
});

riot.toolbar = new riot.Toolbar();
riot.toolbar.buttons.browse.click();