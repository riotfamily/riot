riot.getWrapper = function(el, selector) {
	if (selector == '.riot-controller') {
		if (!el.controller) {
			el.controller = new riot.Controller(el);
		}
		return el.controller;
	}
	if (selector == '.riot-list' || selector == '.riot-component-list') {
		if (el.hasClassName('riot-entity-list')) {
			if (!el.entityList) {
				el.entityList = new riot.EntityList(el);
			}
			return el.entityList;
		}
		else {
			return riot.getComponentList(el);
		 }
	}
	if (selector == '.riot-component'|| selector == '.riot-form') {
		return riot.getComponent(el);
	}
	if (selector == '.riot-text-editor') {
		return riot.getTextEditor(el);
	}
	if (selector == '.riot-image-editor') {
		return riot.getImageEditor(el);
	}
	return null;
}

riot.getTextEditor = function(el) {
	if (!el.textEditor) {
		var editorType = el.readAttribute('riot:editorType');
		var component = riot.findComponent(el);
		if (editorType == 'text') {
			el.textEditor = new riot.InplaceTextEditor(el, component, {
				textTransform: el.readAttribute('riot:textTransform') == 'true'
			});
		}
		if (editorType == 'textarea') {
			el.textEditor = new riot.PopupTextEditor(el, component, {useInnerHtmlAsDefault: true});
		}
		if (editorType == 'richtext') {
			el.textEditor = new riot.RichtextEditor(el, component, {useInnerHtmlAsDefault: true, config: el.readAttribute('riot:config')});
		}
		if (editorType == 'richtext-chunks') {
			//TODO Check if component is a list-component!
			el.textEditor = new riot.RichtextEditor(el, component, {split: true, useInnerHtmlAsDefault: true, config: el.readAttribute('riot:config')});
		}
	}
	return el.textEditor;
}

riot.getImageEditor = function(el) {
	if (!el.imageEditor) {
		el.imageEditor = new riot.ImageEditor(el, riot.findComponent(el), {
			fileStore: el.readAttribute('riot:fileStore'),
			srcTemplate: el.readAttribute('riot:srcTemplate'),
			minWidth: el.readAttribute('riot:minWidth'),
			maxWidth: el.readAttribute('riot:maxWidth'),
			minHeight: el.readAttribute('riot:minHeight'),
			maxHeight: el.readAttribute('riot:maxHeight')
		});
	}
	return el.imageEditor;
}


riot.getComponent = function(el) {
	if (!el.component) {
		if (el.hasClassName('riot-entity-component')) {
			el.component = new riot.EntityComponent(el);
		}
		else if (el.hasClassName('riot-list-component')) {
			el.component = new riot.ListComponent(el);
		}
		else {
			el.component = new riot.Component(el);
		}
	}
	return el.component;
}

riot.findComponent = function(el) {
	if (!el.hasClassName('riot-component')) {
		el = el.up('.riot-component');
	}
	if (!el) return;
	return riot.getComponent(el);
}

riot.findController = function(el) {
	el = el.up('.riot-controller');
	if (el) {
		if (!el.controller) {
			el.controller = new riot.Controller(el);
		}
		return el.controller;
	}
	return null;
}

riot.findComponentList = function(el) {
	el = el.up('.riot-component-list');
	if (el) {
		return riot.getComponentList(el);
	}
	return null;
}

riot.getComponentList = function(el) {
	if (!el.componentList) {
		el.componentList = new riot.ComponentList(el);
	}
	return el.componentList;
}

riot.Controller = Class.create({
	initialize: function(el) {
		this.element = el;
		this.id = el.readAttribute('riot:controllerId');
		this.contextKey = el.readAttribute('riot:contextKey'); 
		this.parentController = riot.findController(el);
		if (!this.parentController) {
			this.dirty = typeof el.down('.riot-dirty') != 'undefined';
		}
	},
	
	getReference: function() {
		return { controllerId: this.id, contextKey: this.contextKey };
	},
	
	markDirty: function() {
		if (!this.parentController) {
			this.dirty = true;
			riot.toolbar.enablePublishButtons();
		}
		else {
			this.parentController.markDirty();
		}
	},
	
	update: function() {
		ComponentEditor.getPreviewListHtml(this.id, this.contextKey, 
				this.replaceHtml.bind(this));
	},

	replaceHtml: function(html) {
		this.element.update(html);
		this.onUpdate();
		riot.toolbar.restoreMode(this.element);
	},
	
	setTempHtml: function(html, live) {
		this.element.update(html);
		this.onUpdate(live);
	},
	
	onUpdate: function(live) {
		riot.adoptFloatsAndClears(this.element);
		if (window.riotEditCallbacks) {
			var el = this.element;
			riotEditCallbacks.each(
				function(callback) {
					callback(el, live || false);
			});
		}
	},
	
	publishOn: function() {
		if (this.dirty && !this.parentController) {
			riot.publishWidgets.push(new riot.PublishWidget(this));
		}
	},
	
	discardOn: function() {
		if (this.dirty && !this.parentController) {
			riot.publishWidgets.push(new riot.DiscardWidget(this));
		}
	}
		
});


riot.ComponentList = Class.create({ 
	initialize: function(el) {
		this.element = el;
		this.controller = riot.findController(el);
		this.id = el.readAttribute('riot:listId');
		if (!el.id) el.id = 'riot-list-' + this.id;
		if (el.hasClassName('riot-dirty') || typeof el.down('.riot-dirty') != 'undefined') {
			this.markDirty();
		}		
		this.maxComponents = el.readAttribute('riot:maxComponents');
		this.minComponents = el.readAttribute('riot:minComponents');
				
		this.findComponentElements();
	},
	
	findComponentElements: function() {
		//Note: Element.findChildren is defined in dragdrop.js
		this.componentElements = Element.findChildren(this.element, 'riot-component', false, 'div') || [];
	},
	
	update: function() {
		this.controller.update();
	},
	
	markDirty: function() {
		if (this.element.hasClassName('riot-toplevel-list')) {
			this.dirty = true;
			this.controller.markDirty();
		}
		else {
			riot.findComponentList(this.element).markDirty();
		}
	},
	
	insertOn: function() {
		if (!this.maxComponents || this.componentElements.length < this.maxComponents) {
			this.insertButton = new riot.InsertButton(this);
			riot.activeInsertButton = null;
		}
	},
	
	insertOff: function() {
		if (this.insertButton) {
			this.insertButton.remove();
			this.insertButton = null;
			riot.activeInsertButton = null;
		}
	},

	moveOn: function() {
		if (this.componentElements.length > 1) {
			this.element.addClassName('riot-mode-move');
			var options = {
				tag: 'div',
				only: 'riot-component',
				overlap: 'vertical',
				constraint: 'vertical',
				scroll: window,
				scrollSpeed: 20
			};
			Sortable.create(this.element, options);
			this.componentElements.each(function(el) {
				el.addClassName('riot-moveable-component')
				el.disableClicks();
			});
			Draggables.addObserver(new riot.ComponentDragObserver(this));
		}
	},
	
	moveOff: function() {
		if (this.componentElements.length > 1) {
			this.element.removeClassName('riot-mode-move');
			this.componentElements.each(function(el) {
				el.removeClassName('riot-moveable-component');
				el.restoreClicks();
				el.style.position = '';
			});
			Sortable.destroy(this.element);
			Draggables.removeObserver(this.element);
		}
	},
	
	updatePositionClasses: function() {		
		var last = this.componentElements.length - 1;
		this.componentElements.each(function(componentEl, index) {
			componentEl.descendants().each(function(el) {
				if (el.hasClassName('component-\\w*') && el.up('.riot-component') == componentEl) {
					el.className = el.className.replace(/component-\w*/, 'component-' + (index + 1));
					el.toggleClassName('last-component', index == last);
				}
			});
		});
	},
	
	removeOn: function() {
		if (this.componentElements.length > this.minComponents) {
			this.element.addClassName('riot-mode-remove');
			var list = this;
			this.componentElements.each(function(el) {
				riot.getComponent(el).setClickHandler(list.removeComponent.bind(list));
			});
		}
	},
	
	removeOff: function() {
		this.element.removeClassName('riot-mode-remove');
		this.componentElements.each(function(el) {
			if (el.component) el.component.removeClickHandler();
		});
	},
	
	removeComponent: function(c) {
		ComponentEditor.deleteComponent(c.id);
		this.componentElements = this.componentElements.without(c.element);
		riot.outline.hide();
		riot.outline.suspended = true;
		new Effect.Remove(c.element, function(el) {
			el.remove();
			riot.outline.suspended = false;
		});
		this.updatePositionClasses();
		this.markDirty();
		if (this.minComponents > 0 && this.componentElements.length == this.minComponents) {
			this.removeOff();
		} 
	}
	
});


riot.ComponentDragObserver = Class.create({
	initialize: function(componentList) {
		this.componentList = componentList;
		this.element = componentList.element;
	},
	onStart: function(eventName, draggable, event) {
		draggable.element.addClassName('riot-drag');
		if (draggable.element.getStyle('clear') == 'none') {
			draggable.options.constraint = false;
		}
		this.nextEl = draggable.element.next('.riot-component');
	},
	onEnd: function(eventName, draggable, event) {
		var el = draggable.element;
		el.removeClassName('riot-drag');
		var nextEl = el.next('.riot-component');
		if(el.parentNode == this.element && nextEl != this.nextEl) {
			this.componentList.findComponentElements();
			this.componentList.updatePositionClasses();
			var nextId = null;
			if (nextEl) {
				nextEl.forceRerendering();
				nextId = riot.getComponent(nextEl).id;
			}
			ComponentEditor.moveComponent(riot.getComponent(el).id, nextId);
			this.componentList.markDirty();
		}
		this.nextEl = null;
	}
});

riot.EntityList = Class.create({

	initialize: function(el) {
		this.element = el;
		this.id = el.readAttribute('riot:listId');
		this.controller = riot.findController(el);
	},

	update: function() {
		this.controller.update();
	},
		
	insertOn: function() {
		this.insertButton = RBuilder.node('div', {
			className: 'riot-insert-button',
			onclick: this.createObject.bindAsEventListener(this)
		});
		this.element.prependChild(this.insertButton);
	},
	
	insertOff: function() {
		this.insertButton.remove();
	},
	
	createObject: function() {
		EntityEditor.createObject(this.id, this.update.bind(this));
		riot.toolbar.buttons.get('browse').click();
	}

});

riot.Component = Class.create({

	initialize: function(el) {
		this.element = el;
		this.id = el.readAttribute('riot:containerId');
		this.controller = riot.findController(el);
		this.form = el.readAttribute('riot:form');
		this.dirty = el.hasClassName('riot-dirty');
		this.bShowOutline = this.showOutline.bindAsEventListener(this);
		this.bHideOutline = this.hideOutline.bindAsEventListener(this);
		this.bOnClick = this.onClick.bindAsEventListener(this);
	},
	
	showOutline: function(ev) {
		// Check to work around "Event.stop is not a function" error upon unload ...
		if (Event.stop) {
			Event.stop(ev);
			riot.outline.show(this.targetElement);
		}
	},
	
	hideOutline: function(ev) {
		if (Event.stop) { 
			Event.stop(ev);
			riot.outline.scheduleHide();
		}
	},
	
	setClickHandler: function(clickHandler) {
		this.clickHandler = clickHandler;
		var c = this.element.childElements();
		this.targetElement = c.length == 1 ? c[0] : this.element;
		this.targetElement.disableClicks();
		Event.observe(this.targetElement, 'click', this.bOnClick);
		Event.observe(this.targetElement, 'mouseover', this.bShowOutline);
		Event.observe(this.targetElement, 'mouseout', this.bHideOutline);
	},
	
	onClick: function(ev) {
		Event.stop(ev);
		this.clickHandler(this);
	},
	
	removeClickHandler: function() {
		if (this.targetElement) {
			this.targetElement.restoreClicks();
			Event.stopObserving(this.targetElement, 'click', this.bOnClick);
			Event.stopObserving(this.targetElement, 'mouseover', this.bShowOutline);
			Event.stopObserving(this.targetElement, 'mouseout', this.bHideOutline);
		}
		this.clickHandler = null;
	},
	
	retrieveText: function(key, callback) {
		ComponentEditor.getText(this.id, key, callback);
	},
	
	updateText: function(key, value, updateFromServer) {
		ComponentEditor.updateText(this.id, key, value, updateFromServer
				? this.update.bind(this) : Prototype.emptyFunction);

		this.markDirty();
	},
	
	updateFile: function(key, value, fileStore, updateFromServer) {
		ComponentEditor.updateFile(this.id, key, value, fileStore,
				updateFromServer ? this.update.bind(this) : Prototype.emptyFunction);

		this.markDirty();
	},

	markDirty: function() {
		this.dirty = true;
		this.controller.markDirty();
	},
	
	propertiesOn: function() {
		this.element.parentNode.addClassName('riot-mode-properties');
		this.setClickHandler(this.editProperties.bind(this));
	},
	
	propertiesOff: function() {
		this.element.parentNode.removeClassName('riot-mode-properties');
		this.removeClickHandler();
	},
	
	editProperties: function() {
		var path = location.pathname.substring(riot.contextPath.length);
		var formUrl = riot.path + this.form + '?' + $H(riotComponentFormParams).toQueryString();
		
		if (riot.instantPublish) {
			formUrl += '&live=true';
		}
		var iframe = RBuilder.node('iframe', {src: formUrl, className: 'properties', width: 1, height: 1});
		riot.popup = new riot.Popup('${title.properties}', iframe, function() {
			var win = iframe.contentWindow ? iframe.contentWindow : iframe.window;
			win.save();
		});
		
		// The ComponentFormSuccessView.ftl will invoke
		// parent.riot.popup.component.propertiesChanged()
		// ... so we need to set a reference:
		riot.popup.component = this;
	},
	
	propertiesChanged: function() {
		riot.popup.close();
		this.markDirty();
		// Timeout as we othwerwise get an 0x8004005 [nsIXMLHttpRequest.open] error.
		// See https://bugzilla.mozilla.org/show_bug.cgi?id=249843
		setTimeout(this.update.bind(this), 1);
	},
	
	update: function() {
		this.controller.update();
	}
});

riot.ListComponent = Class.create(riot.Component, {

	updateTextChunks: function(key, chunks) {
		ComponentEditor.updateTextChunks(this.id, key, chunks, this.update.bind(this));
	},
	
	markDirty: function() {
		this.dirty = true;
		if (!this.componentList) {
			this.componentList = riot.findComponentList(this.element);
		}
		this.componentList.markDirty();
	}
	
});


riot.EntityComponent = Class.create(riot.Component, {

	initialize: function($super, el) {
		$super(el);
		this.listId = el.readAttribute('riot:listId');
		this.objectId = el.readAttribute('riot:objectId');
	},
	
	retrieveText: function(key, callback) {
		EntityEditor.getText(this.listId, this.objectId, key, callback);
	},

	updateText: function(key, value, updateFromServer) {
		EntityEditor.updateText(this.listId, this.objectId, key, value, 
				updateFromServer ? this.update.bind(this) : Prototype.emptyFunction);
	},
	
	update: function() {
		riot.findController(this.element).update(this.listId);
	}
});

riot.InsertButton = Class.create({

	initialize: function(componentList) {
		this.componentList = componentList;
		this.element = RBuilder.node('div', {className: 'riot-insert-button',
				onclick: this.onclick.bindAsEventListener(this)});

		ComponentEditor.getValidTypes(this.componentList.controller.id,
				this.setValidTypes.bind(this));
	},
	
	setValidTypes: function(types) {
		this.types = types;
		this.componentList.element.appendChild(this.element);
	},

	remove: function() {
		this.element.remove();
	},

	onclick: function() {
		if (riot.activeInsertButton) {
			riot.activeInsertButton.element.removeClassName('riot-insert-button-active');
		}
		if (this.types.length == 1) {
			this.insert(this.types[0].type);
			riot.toolbar.removeInspector();
		}
		else {
			this.element.addClassName('riot-insert-button-active');
			riot.activeInsertButton = this;
			this.inspector = new riot.TypeInspector(this.types, null,
					this.insert.bind(this));

			riot.toolbar.setInspector(this.inspector.element);
		}
	},

	insert: function(type) {
		ComponentEditor.insertComponent(this.componentList.id, -1, type, null,
				this.oninsert.bind(this));

		if (this.inspector) {
			this.inspector.onchange = this.changeType.bind(this);
		}
	},

	oninsert: function(id) {
		this.componentId = id;
		this.componentList.update();
	},

	updateList: function() {
		this.componentList.update();
	},
	
	changeType: function(type) {
		ComponentEditor.setType(this.componentId, type, this.updateList.bind(this));
	}
});

riot.TypeInspector = Class.create({

	initialize: function(types, activeType, onchange) {
		this.onchange = onchange;
		var select = RBuilder.node('div', {className: 'type-inspector'});
		for (var i = 0; i < types.length; i++) {
			var e = RBuilder.node('div', {className: 'type'},
				RBuilder.node('span', {className: 'type-' + types[i].type}, types[i].description)
			);
			e.type = types[i].type;
			if (e.type == this.type) {
				this.activeTypeButton = e;
				e.className = 'active-type';
			}
			var inspector = this;
			e.onclick = function() {
				if (inspector.activeTypeButton) {
					inspector.activeTypeButton.className = 'type';
				}
				this.className = 'active-type'
				inspector.activeTypeButton = this;
				inspector.onchange(this.type);
			}
			select.appendChild(e);
		}

		this.element = RBuilder.node('div', {},
			RBuilder.node('div', {className: 'riot-close-button', onclick: riot.toolbar.hideInspector.bind(riot.toolbar)}),
			RBuilder.node('div', {className: 'headline'}, '${title.typeInspector}'),
			select
		);
	}
});


riot.PublishWidget = Class.create({
	initialize: function(controller, mode) {
		mode = mode || 'publish';
		this.className = 'riot-' + mode + '-outline';
		this.controller = controller;		
		this.previewHtml = controller.element.innerHTML;
		this.element = RBuilder.node('div', {className: this.className, style: {position: 'absolute', display: 'none'}},
			this.overlay = RBuilder.node('div')
		);
		this.live = false;
		this.updateUI();
		this.element.observe('click', this.toggleVersion.bind(this), true);
		riot.toolbar.applyButton.enable();
		if (document.addEventListener) {
			this.domListener = this.scaleOverlay.bind(this);
			this.controller.element.addEventListener('DOMNodeInserted', this.domListener, false);
		}
		
		this.dirtyListIds = controller.element.getElementsBySelector('.riot-toplevel-list')
				.collect(riot.getComponentList)
				.select(function(l) { return l.dirty })
				.pluck('id');
				
		this.dirtyContainerIds = controller.element.getElementsBySelector('.riot-single-component')
				.collect(riot.getComponent)
				.select(function(c) { return c.dirty })
				.pluck('id'); 
	},	
		
	getReference: function() {
		return this.controller.getReference();
	},
	
	show: function() {
		this.controller.element.makePositioned();
		if (this.controller.element.innerHTML.empty()) {
			this.controller.element.innerHTML = '<div class="riot-empty-list"></div>';
		}
		this.controller.element.style.overflow = 'hidden';
		this.controller.element.appendChild(this.element);
		this.scaleOverlay();
		setTimeout(this.scaleOverlay.bind(this), 50);
		this.element.show();
	},

	scaleOverlay: function() {
		this.overlay.style.width = (this.controller.element.offsetWidth - 4) + 'px';
		this.overlay.style.height = (this.controller.element.offsetHeight - 4) + 'px';
	},

	destroy: function() {
		if (document.removeEventListener) {
			this.domListener = this.show.bind(this);
			this.controller.element.removeEventListener('DOMNodeInserted', this.domListener, false);
		}		
		this.showPreview();
		this.controller.element.style.overflow = '';
		if (this.element.parentNode) this.element.remove();
		return this.controller.dirty;
	},

	toggleVersion: function(ev) {
		if (ev) Event.stop(ev);
		if (!this.live) {
			this.showLive();
		}
		else {
			this.showPreview();
		}
		if (riot.publishWidgets.any(this.changesAvailable)) {
			riot.toolbar.applyButton.enable();
		}
		else {
			riot.toolbar.applyButton.disable();
		}
	},

	showPreview: function() {
		if (!this.live) return;
		this.live = false;
		this.setHtml(this.previewHtml);
		this.updateUI();
	},

	showLive: function() {
		if (this.live) return;
		this.live = true;
		this.setHtml(this.liveHtml);
		this.updateUI();
	},	

	setLiveHtml: function(html) {
		this.liveHtml = html;
		this.show();
	},

	setHtml: function(html) {
		this.controller.setTempHtml(html, this.live);
		this.show();
	},

	updateUI: function() {
		this.element.toggleClassName(this.className + '-live', this.live);
		this.element.toggleClassName(this.className + '-preview', !this.live);
	},

	changesAvailable: function(instance) {
		return !instance.live;
	},

	applyChanges: function() {
		if (!this.live) {
			ComponentEditor.publish(this.dirtyListIds, this.dirtyContainerIds);
			this.controller.dirty = false;
		}
	}
});

riot.DiscardWidget = Class.create(riot.PublishWidget, {
	initialize: function($super, controller) {
		$super(controller, 'discard');
	},	

	changesAvailable: function(instance) {
		return instance.live;
	},
	
	setLiveHtml: function(html) {
		this.liveHtml = html;
		this.showLive();
	},
	
	applyChanges: function() {
		if (this.live) {
			ComponentEditor.discard(this.dirtyListIds, this.dirtyContainerIds);
			this.controller.dirty = false;			
			this.previewHtml = this.liveHtml;			
		}
	}
});


riot.setLiveHtml = function(html) {
	for (var i = 0; i < riot.publishWidgets.length; i++) {
		riot.publishWidgets[i].setLiveHtml(html[i]);
	}
}

if (riot.toolbar.buttons.get('publish')) {
	riot.toolbar.buttons.get('publish').beforeApply = 
	riot.toolbar.buttons.get('discard').beforeApply = function(enable) {
		if (enable) {
			riot.publishWidgets = [];
		}
	};
	riot.toolbar.buttons.get('publish').afterApply = 
	riot.toolbar.buttons.get('discard').afterApply = function(enable) {
		if (enable) {
			var refs = riot.publishWidgets.invoke('getReference');
			ComponentEditor.getLiveListHtml(refs, riot.setLiveHtml);
		}
		else {
			var dirty = riot.publishWidgets.invoke('destroy').any();
			riot.publishWidgets = null;
			riot.toolbar.applyButton.disable();
			if (!dirty) {
				riot.toolbar.disablePublishButtons();
			}
		}
	};
}

riot.toolbar.buttons.get('editImages').precondition = riot.initSwfUpload;

dwr.engine.setErrorHandler(function(err, ex) {
	if (ex.javaClassName == 'org.riotfamily.components.context.RequestContextExpiredException') {
		location.reload();
	}
	else {
		alert(err);
	}
});

riot.adoptFloatsAndClears = function(root) {
	Selector.findChildElements(root || document, ['.riot-component']).each(function(el) {
		var c = el.childElements();
		if (c.length == 1) {
			var child = c[0];
			var cssFloat = child.getStyle('float');
			if (cssFloat != 'none') {
				el.style.zIndex = 1;
				el.style.cssFloat = el.style.styleFloat = cssFloat;
				child.style.cssFloat = child.style.styleFloat = 'none';
				child.originalFloat = cssFloat;
			}
			var cssClear = child.getStyle('clear');
			if (cssClear != 'none') {
				el.style.clear = cssClear;
			}
		}
	});
}

riot.adoptFloatsAndClears();
riot.toolbar.activate();


