riot.AbstractComponent = Class.create();
riot.AbstractComponent.prototype = {

	initialize: function(componentList, el) {
		this.componentList = componentList;
		this.element = el;
		el.component = this;
		this.handlers = {};
		this.onMouseOver = this.showOutline.bindAsEventListener(this);
		this.form = el.readAttribute('riot:form');
		if (this.form) {
			this.handlers.properties = this.editProperties.bindAsEventListener(this);
		}
		this.setupElement();
	},

	edit: function(enable) {
		this.editing = enable;
		this.editors.each(function(editor) {
			editor.setEnabled(enable);
		});
	},

	setMode: function(mode) {
		if (mode != null) {
			if (!this.handlers[mode]) {
				this.mode = null;
				return;
			}
			this.element.disableClicks();
			Event.observe(this.element, 'click', riot.stopEvent, true);
			Event.observe(this.element, 'mouseup', this.handlers[mode]);
			Event.observe(this.element, 'mouseover', this.onMouseOver, true);
			Event.observe(this.element, 'mouseout', riot.outline.hide, true);
		}
		else {
			if (this.mode) {
				this.element.restoreClicks();
				Event.stopObserving(this.element, 'click', riot.stopEvent, true);
				Event.stopObserving(this.element, 'mouseup', this.handlers[this.mode]);
				Event.stopObserving(this.element, 'mouseover', this.onMouseOver, true);
				Event.stopObserving(this.element, 'mouseout', riot.outline.scheduleHide, true);
			}
		}
		this.mode = mode;
	},

	showOutline: function(event) {
		riot.outline.show(this.element);
	},

	removeComponent: function(event) {
		var e = event || window.event;
		if (e) Event.stop(e);
		this.setMode(null);
		riot.outline.hide();
		riot.outline.suspended = true;
		new Effect.Remove(this.element, function(el) {
			el.remove();
			el.component.componentList.componentRemoved();
			riot.outline.suspended = false;
		});
		this.deleteObject();
		this.componentList.setDirty(true);
		return false;
	},

	setHtml: function(html) {
		this.element.update(html);
		this.setupElement();
		this.repaint();
	},

	editProperties: function(ev) {
		ev = ev || window.event;
		if (ev) Event.stop(ev);
		if (this.form) {
			var path = location.pathname.substring(riot.contextPath.length);
			var formUrl = riot.path + this.form + '?path=' + path;
			
			if (riot.instantPublish) {
				formUrl += '&live=true';
			}
			var iframe = RBuilder.node('iframe', {src: formUrl, className: 'properties', width: 1, height: 1});
			riot.popup = new riot.Popup('${title.properties}', iframe, function() {
				var win = iframe.contentWindow ? iframe.contentWindow : iframe.window;
				win.save();
			});
			riot.popup.component = this;
		}
		return false;
	},

	propertiesChanged: function() {
		riot.popup.close();
		// Timeout as we othwerwise get an 0x8004005 [nsIXMLHttpRequest.open] error.
		// See https://bugzilla.mozilla.org/show_bug.cgi?id=249843
		setTimeout(this.onupdate.bind(this), 1);
	},

	onupdate: function() {
		this.componentList.update();
	},

	repaint: function() {
		var next = this.element.next();
		if (next) {	next.forceRerendering(); }
	},

	createEditor: function(e, editorType) {
		if (editorType == 'text') {
			return new riot.InplaceTextEditor(e, this, {
				textTransform: e.readAttribute('riot:textTransform') == 'true'
			});
		}
		if (editorType == 'textarea') {
			return new riot.PopupTextEditor(e, this);
		}
		if (editorType == 'richtext') {
			return new riot.RichtextEditor(e, this, {useInnerHtmlAsDefault: true});
		}
		if (editorType == 'richtext-chunks') {
			return new riot.RichtextEditor(e, this, {split: true, useInnerHtmlAsDefault: true});
		}
		if (editorType == 'image') {
			return new riot.ImageEditor(e, this, {
				srcTemplate: e.readAttribute('riot:srcTemplate'),
				minWidth: e.readAttribute('riot:minWidth'),
				maxWidth: e.readAttribute('riot:maxWidth'),
				minHeight: e.readAttribute('riot:minHeight'),
				maxHeight: e.readAttribute('riot:maxHeight')
			});
		}
		return null;
	},
					
	setupElement: function() {
		this.editors = [];
		var desc = this.element.descendants();
		desc.push(this.element);
		for (var i = 0, len = desc.length; i < len; i++) {
			var e = desc[i];
			try {
				var editorType = e.readAttribute('riot:editorType');
				if (editorType) {
					if (e == this.element || (!e.component && this.element == Element.up(e, '.riot-component'))) {
						var editor = this.createEditor(e, editorType);
						if (editor) {
							this.editors.push(editor);
						}
					}
				}
			}
			catch (ex) {
				// getAttribute('riot:editorType') fails in IE for TABLE elements (and maybe others?)
			}
		}
		
		if (this.editing) {
			this.edit(true);
		}

		this.element.toggleClassName('riot-component-with-form', this.form);
	}
};

riot.Component = Class.extend(riot.AbstractComponent, {

	initialize: function(componentList, el) {
		this.id = el.readAttribute('riot:containerId');
		this.type = el.readAttribute('riot:componentType');
		this.SUPER(componentList, el);
		this.handlers.remove = this.removeComponent.bindAsEventListener(this);
	},

	deleteObject: function() {
		ComponentEditor.deleteComponent(this.id);
	},

	retrieveText: function(key, callback) {
		ComponentEditor.getText(this.id, key, callback);
	},
	
	updateText: function(key, value, updateFromServer) {
		ComponentEditor.updateText(this.id, key, value, updateFromServer
				? this.onupdate.bind(this) : Prototype.emptyFunction);

		this.componentList.setDirty(true);
	},

	updateTextChunks: function(key, chunks) {
		ComponentEditor.updateTextChunks(this.id, key, chunks, this.onupdate.bind(this));
	},
	
	onupdate: function() {
		this.componentList.setDirty(true);
		this.SUPER();
	},

	createEditor: function(e, editorType) {
		if (editorType == 'richtext-chunks') {
			return new riot.RichtextEditor(e, this, {split: true, useInnerHtmlAsDefault: true});
		}
		return this.SUPER(e, editorType);
	},

	setupElement: function() {
		this.SUPER();
		// Adopt float and clear styles from the first child element
		riot.adoptFloatAndClear(this.element);
	},
	
	updatePositionClasses: function(position, last) {
		if (this.type != 'inherit') {
			this.element.descendants().each(function(el) {
				if (el.hasClassName('component-\\w*')) {
					el.className = el.className.replace(/component-\w*/, 'component-' + position);
					el.toggleClassName('last-component', last);
				}
			});
		}
	}
});


riot.EntityComponent = Class.extend(riot.AbstractComponent, {

	initialize: function(componentList, el) {
		this.objectId = el.readAttribute('riot:objectId');
		this.SUPER(componentList, el);
	},
	
	retrieveText: function(key, callback) {
		EntityEditor.getText(this.componentList.listId, this.objectId, key, callback);
	},

	updateText: function(key, value, updateFromServer) {
		EntityEditor.updateText(this.componentList.listId, this.objectId, key, value, 
				updateFromServer ? this.onupdate.bind(this) : Prototype.emptyFunction);
	}
});

riot.InsertButton = Class.create();
riot.InsertButton.prototype = {
	initialize: function(component, componentList) {
		this.component = component;
		this.componentList = componentList || component.componentList;
		this.element = RBuilder.node('div', {className: 'riot-insert-button',
				onclick: this.onclick.bindAsEventListener(this)});
				
		componentList.element.appendChild(this.element);
	},

	remove: function() {
		this.element.remove();
	},

	onclick: function() {
		if (riot.activeInsertButton) {
			riot.activeInsertButton.element.removeClassName('riot-insert-button-active');
		}
		if (this.componentList.fixedType) {
			this.insert(this.componentList.fixedType);
			riot.toolbar.removeInspector();
		}
		else {
			this.element.addClassName('riot-insert-button-active');
			riot.activeInsertButton = this;
			this.inspector = new riot.TypeInspector(this.componentList.types, null,
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
		this.componentList.setDirty(true);
	},

	changeType: function(type) {
		ComponentEditor.setType(this.componentId, type,
				this.componentList.update.bind(this.componentList));
	}
};

riot.TypeInspector = Class.create();
riot.TypeInspector.prototype = {

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
}

riot.publishWidgets = [];

riot.PublishWidget = Class.create();
riot.PublishWidget.prototype = {
	initialize: function(componentList, mode) {
		mode = mode || 'publish'
		this.className = 'riot-' + mode + '-outline';
		this.componentList = componentList;		
		this.previewHtml = componentList.element.innerHTML;
		this.element = RBuilder.node('div', {className: this.className, style: {position: 'absolute', display: 'none'}},
			this.overlay = RBuilder.node('div')
		);
		this.live = false;
		this.updateUI();
		this.element.observe('click', this.toggleVersion.bind(this), true);
		riot.publishWidgets.push(this);
		riot.toolbar.applyButton.enable();
		if (document.addEventListener) {
			this.domListener = this.scaleOverlay.bind(this);
			this.componentList.element.addEventListener('DOMNodeInserted', this.domListener, false);
		}		
		this.show();		
	},	
	
	show: function() {
		this.componentList.element.makePositioned();
		if (this.componentList.element.innerHTML.empty()) {
			this.componentList.element.innerHTML = '<div class="riot-empty-list"></div>';
		}
		this.componentList.element.appendChild(this.element);
		this.scaleOverlay();
		setTimeout(this.scaleOverlay.bind(this), 50);
		this.element.show();
	},

	scaleOverlay: function() {
		this.overlay.style.width = (this.componentList.element.offsetWidth - 4) + 'px';
		this.overlay.style.height = (this.componentList.element.offsetHeight - 4) + 'px';
	},

	destroy: function() {
		if (document.removeEventListener) {
			this.domListener = this.show.bind(this);
			this.componentList.element.removeEventListener('DOMNodeInserted', this.domListener, false);
		}		
		this.componentList.replaceHtml(this.previewHtml);
		riot.publishWidgets = riot.publishWidgets.without(this);
		this.componentList.publishWidget = null;
		if (this.element.parentNode) this.element.remove();
			
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
		if (!this.liveHtml) {
			ComponentEditor.getLiveListHtml(this.componentList.controllerId,
					this.componentList.id, this.setLiveHtml.bind(this));
		}
		else {
			this.setHtml(this.liveHtml);
		}
		this.updateUI();
	},	

	setLiveHtml: function(html) {
		this.liveHtml = html;
		this.setHtml(html);
	},

	setHtml: function(html) {
		this.componentList.setTempHtml(html);
		this.show();
	},

	updateUI: function() {
		this.element.toggleClassName(this.className + '-live', this.live);
		this.element.toggleClassName(this.className + '-preview', !this.live);
	},

	changesAvailable: function(instance) {
		return !instance.live;
	},

	apply: function() {
		if (!this.live) {
			this.componentList.publishChanges();										
		}
	}
	
	
}

riot.DiscardWidget = Class.extend(riot.PublishWidget, {
	initialize: function(componentList) {
		this.SUPER(componentList, 'discard');
		this.showLive();		
	},	

	changesAvailable: function(instance) {
		return instance.live;
	},

	apply: function() {
		if (this.live) {
			this.previewHtml = this.liveHtml;
			this.componentList.discardChanges();			
		}
	}
});

riot.listCount = 0; // Counter to generate element IDs

riot.AbstractWrapper = Class.create();
riot.AbstractWrapper.prototype = {
	initialize: function(el) {
		this.element = el;
		el.componentList = this;
		if (!el.id) el.id = 'riot-components-' + riot.listCount++;
		this.controllerId = el.readAttribute('riot:controllerId');
		this.childLists = [];
		var parent = el.up('.riot-components');
		if (parent) {
			this.parentList = parent.componentList;
			this.parentList.childLists.push(this);
		}
	},

	getComponents: function() {
		if (!this.components) {
			this.components = [];
			var elements = this.element.descendants();
			elements.push(this.element);
			for (var i = 0, len = elements.length; i < len; i++) {
				var e = elements[i];
				if (e.hasClassName('riot-component')) {
					if (e == this.element || this.element == e.up('.riot-components')) {
						var c = e.component || this.createComponent(e);
						this.components.push(c);
					}
				}
			}
		}
		return this.components;
	},
	
	createComponent: function(e) {
		return new riot.Component(this, e);
	},

	update: function() {
		ComponentEditor.getPreviewListHtml(this.controllerId, this.id, this.replaceHtml.bind(this));
	},

	replaceHtml: function(html) {
		this.element.update(html);
		this.updateComponents();
		this.onUpdate();
		riot.toolbar.restoreMode(this);
	},

	setTempHtml: function(html) {
		this.element.update(html);
		this.onUpdate();
	},

	onUpdate: function() {
		if (window.riotEditCallbacks) {
			var listElement = this.element;
			riotEditCallbacks.each(function(callback) { callback(listElement) });
		}
	},

	updateComponents: function() {
		this.components = null;
		this.getComponents();
		if (this.childLists) riot.toolbar.evictComponentLists(this.childLists);
		this.childLists = riot.createWrappers(this.element);
		riot.toolbar.registerComponentLists(this.childLists);
	},

	browse: function() {
	},

	edit: function(enable) {
		this.getComponents().invoke('edit', enable);
	},

	properties: function(enable) {
		this.element.toggleClassName('riot-mode-properties', enable);
		this.getComponents().invoke('setMode', enable ? 'properties' : null);
	},

	setDirty: function(dirty) {		
		if (this.parentList) {
			if (dirty) {
				this.parentList.setDirty(dirty);
			}			
		}
		else {
			this.dirty = dirty;
		}
		riot.toolbar.dirtyCheck(dirty);
	},

	getDirtyContainerIds: function() {
		return this.childLists.collect(function(cl) {
			if (cl.dirty) return cl.getDirtyContainerIds();
		}).flatten().compact();
	},

	discard: function(enable) {
		if (enable) {
			if (this.dirty && !this.parentList) this.publishWidget = new riot.DiscardWidget(this);
		}
		else {
			if (this.publishWidget) this.publishWidget.destroy();
			riot.toolbar.applyButton.disable();
		}
	},

	publish: function(enable) {
		if (enable) {
			if (this.dirty && !this.parentList) this.publishWidget = new riot.PublishWidget(this);
		}
		else  {
			if (this.publishWidget) this.publishWidget.destroy();
			riot.toolbar.applyButton.disable();
		}
	},

	apply: function() {
		if (this.publishWidget) this.publishWidget.apply();
	},

	publishChanges: function() {
		ComponentEditor.publish(this.id, this.getDirtyContainerIds(),
				this.update.bind(this));
		this.setDirty(false);
	},

	discardChanges: function() {		
		ComponentEditor.discard(this.id, this.getDirtyContainerIds(),
				this.update.bind(this));
		this.setDirty(false);
	}
}

riot.Entity = Class.extend(riot.AbstractWrapper, {
	initialize: function(el) {
		this.SUPER(el);
		this.listId = el.readAttribute('riot:listId');
		this.getComponents();
	},
	
	createComponent: function(e) {
		return new riot.EntityComponent(this, e);
	}
		
});

riot.EntityList = Class.extend(riot.Entity, {
	
	insert: function(enable) {
		if (enable) {
			this.insertButton = RBuilder.node('div', {className: 'riot-insert-button',
				onclick: this.createObject.bindAsEventListener(this)});
				
			this.element.prependChild(this.insertButton);
		}
		else {
			this.insertButton.remove();
		}
	},
	
	createObject: function() {
		EntityEditor.createObject(this.listId, this.update.bind(this));
		riot.toolbar.buttons.browse.click();
	}
	
});

riot.ComponentSet = Class.extend(riot.AbstractWrapper, {
	initialize: function(el) {
		this.SUPER(el);
		this.id = null;
		this.setDirty(this.getComponents().pluck('element').any(function(e) {
			return e.readAttribute('riot:dirty') != null;
		}));
	},

	getDirtyContainerIds: function() {
		return this.SUPER().concat(this.getComponents().pluck('id')).compact().uniq();
	}
});

riot.ComponentList = Class.extend(riot.AbstractWrapper, {
	initialize: function(el) {
		this.SUPER(el);
		this.id = el.readAttribute('riot:listId');;
		this.setDirty(el.readAttribute('riot:dirty'));		
		this.maxComponents = el.readAttribute('riot:maxComponents');
		this.minComponents = el.readAttribute('riot:minComponents');
		ComponentEditor.getValidTypes(this.controllerId,
				this.setValidTypes.bind(this));
	},

	setValidTypes: function(types) {
		this.types = types;
		if (types.length == 1) {
			this.fixedType = types[0].type;
		}
	},

	updatePositionClasses: function() {
		this.components = null;
		var last = this.getComponents().length - 1;
		this.getComponents().each(function(component, index) {
			component.updatePositionClasses(index + 1, index == last);
		});
	},

	insert: function(enable) {
		if (!this.maxComponents || this.getComponents().length < this.maxComponents) {
			if (enable) {
				this.insertButton = new riot.InsertButton(null, this);
			}
			else if (this.insertButton) {
				this.insertButton.remove();
				this.insertButton = null;
			}
			riot.activeInsertButton = null;
		}
	},

	validateMaxComponents: function() {
		if (this.insertButton && this.maxComponents && this.getComponents().length >= this.maxComponents) {
			this.insertButton.hide();
			riot.activeInsertButton = null;
		}
	},

	move: function(enable) {
		if (this.getComponents().length > 1) {
			this.element.toggleClassName('riot-mode-move', enable);
			if (enable) {
				var options = {
					tag: 'div',
					only: 'riot-component',
					overlap: 'vertical',
					constraint: 'vertical',
					scroll: window,
					scrollSpeed: 20
				};
				Sortable.create(this.element, options);
				this.getComponents().each(function(component) {
					component.element.addClassName('riot-moveable-component').observe('click', riot.stopEvent, true).disableClicks();
				});
				Draggables.addObserver(new riot.ComponentDragObserver(this));
			}
			else {
				this.getComponents().each(function(component) {
					component.element.removeClassName('riot-moveable-component').stopObserving('click', riot.stopEvent, true).restoreClicks();
					component.element.style.position = '';
				});
				Sortable.destroy(this.element);
				Draggables.removeObserver(this.element);
			}
		}
	},

	remove: function(enable) {
		if (enable && this.getComponents().length == this.minComponents) {
			return;
		}
		this.element.toggleClassName('riot-mode-remove', enable);
		this.getComponents().invoke('setMode', enable ? 'remove' : null);
	},

	componentRemoved: function() {
		this.updatePositionClasses();
		if (this.minComponents > 0 && this.getComponents().length == this.minComponents) {
			this.remove(false);
		}
	}

});

riot.createWrapper = function(el) {
	var wrapper = el.readAttribute('riot:wrapper');
	if (wrapper == 'entity') {
		return new riot.Entity(el);
	}
	if (wrapper == 'entityList') {
		return new riot.EntityList(el);
	}
	if (wrapper == 'componentSet') {
		return new riot.ComponentSet(el);
	}
	return new riot.ComponentList(el);
}

riot.createWrappers = function(el) {
	var lists = [];
	el = el || $(document.body);
	el.getElementsBySelector('.riot-components').each(function(e) {
		if(!e.componentList) {
			e.componentList = riot.createWrapper(e);
		}
		lists.push(e.componentList);
	});
	return lists;
}

riot.ComponentDragObserver = Class.create();
riot.ComponentDragObserver.prototype = {
	initialize: function(componentList) {
		this.componentList = componentList;
		this.element = componentList.element;
	},
	onStart: function(eventName, draggable, event) {
		draggable.element.addClassName('riot-drag');
		if (draggable.element.getStyle('clear') == 'none') {
			draggable.options.constraint = false;
		}
		this.nextEl = draggable.element.nextSibling;
	},
	onEnd: function(eventName, draggable, event) {
		var el = draggable.element;
		el.removeClassName('riot-drag');
		var nextEl = el.nextSibling;
		if(el.parentNode == this.element && nextEl != this.nextEl) {
			this.componentList.updatePositionClasses();
			var nextId = nextEl && nextEl.component ? nextEl.component.id : null;
			ComponentEditor.moveComponent(el.component.id, nextId);
			this.componentList.setDirty(true);
			if (nextEl) { nextEl.forceRerendering(); }
		}
		this.nextEl = null;
	}
}

riot.adoptFloatAndClear = function(el) {
	if (!el) return;
	var child = el.down();
	if (child) {
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
}

riot.getComponent = function(e) {
	e = e || this;
	var componentElement = Element.up(e, '.riot-component');
	if (componentElement && componentElement.component) {
		return componentElement.component;
	}
	return null;
}

riot.editProperties = function(e) {
	var component = riot.getComponent(e);
	if (component && !component.mode) {
		riot.toolbar.buttons.properties.click();
		component.editProperties();
	}
	return false;
}

dwr.engine.setErrorHandler(function(err, ex) {
	if (ex.javaClassName == 'org.riotfamily.components.context.RequestContextExpiredException') {
		location.reload();
	}
	else {
		alert(err);
	}
});

riot.toolbar.activate();
