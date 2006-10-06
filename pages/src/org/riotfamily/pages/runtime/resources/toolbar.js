riot.Toolbar = Class.create();
riot.Toolbar.prototype = {
	
	initialize: function() {
		this.buttons = $H({
			gotoRiot: new riot.ToolbarButton('gotoRiot', '${toolbar-button.gotoRiot}', riot.path),
			browse: new riot.ToolbarButton('browse', '${toolbar-button.browse}'),
			insert: new riot.ToolbarButton('insert', '${toolbar-button.insert}'),
			remove: new riot.ToolbarButton('remove', '${toolbar-button.remove}'),
			//changeType: new riot.ToolbarButton('changeType', '${toolbar-button.changeType}'),
			edit: new riot.ToolbarButton('edit', '${toolbar-button.edit}'),
			properties: new riot.ToolbarButton('properties', '${toolbar-button.properties}'),
			move: new riot.ToolbarButton('move', '${toolbar-button.move}'),
			logout: new riot.ToolbarButton('logout', '${toolbar-button.logout}')
		});
		var buttonElements = this.buttons.values().pluck('element');
		document.body.appendChild(this.element = Element.create('div', {id: 'riot-toolbar'},
			Element.create('div', {id: 'riot-toolbar-buttons'}, buttonElements)
		));
		document.body.appendChild(this.inspectorPanel = Element.create('div', {id: 'riot-inspector'}));

		this.buttons.logout.applyHandler = this.logout;
	
		this.buttons.publish = new riot.ToolbarButton('publish', '${toolbar-button.publish}');
		this.buttons.publish.getHandlerTargets = function() { return riot.toolbar.dirtyComponentLists; };
		this.buttons.publish.disable();
		this.element.appendChild(this.buttons.publish.element);
		this.overlay = new riot.Overlay();
		
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
	
	setInspector: function(inspector, component) {
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
	
	showDialog: function(el) {
		this.dialog = el;
		if (el.parentNode == null || el.parentNode.nodeType == 11) {
			Element.invisible(el);
			document.body.appendChild(el);
		}
		this.overlay.show();
		Viewport.center(el);
		Element.visible(el);
	},
	
	closeDialog: function() {
		if (this.dialog) {
			Element.remove(this.dialog);
			this.overlay.hide();
			this.dialog = null;
		}
	},
		
	updateComponentLists: function() {
		var lists = [];
		var e = document.getElementsByClassName('riot-components');
		for (var i = 0; i < e.length; i++) {
			var list = e[i].componentList;
			if(!isDefined(list)) {
				list = new riot.ComponentList(e[i]);
				e[i].componentList = list;
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
		ComponentEditor.keepAlive(this.resume.bind(this));
	},
	
	suspend: function(message) {
		if (!this.suspended) {
			this.suspended = true;
			if (riot.activeEditor && riot.activeEditor.popup) {
				riot.activeEditor.suspend(message);
			}
			else {
				this.showDialog(Element.create('div', {className: 'riot-popup riot-message-popup'},
					Element.create('h2', {}, '${message-popup.title}'), 
					Element.create('div', {className: 'message'}, 
						Element.create('p', {}, message)
					)
				));
			}
		}
	},
	
	resume: function() {
		if (this.suspended) {
			this.suspended = false;
			if (riot.activeEditor && riot.activeEditor.popup) {
				riot.activeEditor.resume();
			}
			else {
				this.closeDialog();
			}
		}
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
		this.element = Element.create('a', {
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
		var handler = this.handler;
		this.getHandlerTargets().each(function(list) {
			list[handler](enable);
		});
	}
	
}

riot.Overlay = Class.create();
riot.Overlay.prototype = {
	initialize: function() {
		var img = new Image(); // Preload image ...
		img.src = Resources.resolveUrl('overlay.png');
		this.element = Element.create('div', {id: 'riot-overlay', style: {display: 'none', position: 'absolute', top: 0, left: 0, width: '100%'}});
		if (isDefined(this.element.style.filter)) {
			this.element.style.filter = 
				'progid:DXImageTransform.Microsoft.AlphaImageLoader(src="' 
				+ img.src + '", sizingMethod="scale")';
		}
		else {
			this.element.style.backgroundImage = 'url(' + img.src + ')'; 
		}
	},
	
	hideElements: function(name) {
		$A(document.getElementsByTagName(name)).each(function (e) {
			if (!Element.childOf(e, riot.toolbar.dialog)) {
				Element.invisible(e);
				e.hidden = true;
			}
		});
	},
	
	showElements: function(name) {
		$A(document.getElementsByTagName(name)).each(function (e) {
			if (e.hidden) {
				Element.visible(e);
				e.hidden = false;
			}
		});
	},
	
	show: function() {
		if (browserInfo.ie) this.hideElements('select');
		this.hideElements('object');
		this.hideElements('embed');
		this.element.style.height = Viewport.getPageHeight() + 'px';
		Element.show(this.element);
		Element.prependChild(document.body, this.element);
	},
	
	hide: function() {
		Element.hide(this.element);
		Element.remove(this.element);
		if (browserInfo.ie) this.showElements('select');
		this.showElements('object');
		this.showElements('embed');
	}
}

DWREngine.setErrorHandler(function(err, ex) {
	if (err == 'Invalid reply from server') { // See engine.js 778
		riot.toolbar.suspend('${error.invalidReply}');
	}
	else if (err == 'No data received from server' || // See engine.js 759 (IE only)
		(err.name && err.name == 'NS_ERROR_NOT_AVAILABLE')) { // ... for Mozilla
		riot.toolbar.suspend('${error.serverNotAvailable}');
	}
	else if (err == 'Request context has expired') { // RequestContextExpiredException
		location.reload();
	}
	else {
		alert(err);
	}
});

riot.toolbar = new riot.Toolbar();
riot.toolbar.buttons.browse.click();