riot.Component = Class.create();
riot.Component.prototype = {

	initialize: function(componentList, el) {
		this.componentList = componentList;
		this.element = el;
		el.component = this;
		this.handlers = {
			'properties': this.editProperties.bindAsEventListener(this)
		};
		if (!el.hasClassName('riot-single-component')) {
			this.handlers.remove = this.removeComponent.bindAsEventListener(this);
		}
		this.onMouseOver = this.showOutline.bindAsEventListener(this);
		this.onMouseOut = this.hideOutline.bindAsEventListener(this);
		
		this.id = el.getAttribute('riot:containerId');
		if (this.id) {
			this.type = el.getAttribute('riot:componentType');
			this.form = el.getAttribute('riot:form');
			this.setupElement();
		}
	},
	
	edit: function(enable) {
		this.editing = enable;
		this.editors.each(function(editor) {
			editor.setEnabled(enable);
		});
	},
	
	created: function(info) {
		this.id = info.id;
		this.typeChanged(info);
		this.componentList.updatePositionClasses();
		this.componentList.validateMaxComponents();
	},
		
	setMode: function(mode) {
		if (mode != null) {
			if (!this.handlers[mode] || (mode == 'properties' && !this.form)) {
				this.mode = null;
				return;
			}
			this.element.disableHandlers('onclick');
			Event.observe(this.element, 'click', riot.stopEvent, true);
			Event.observe(this.element, 'mouseup', this.handlers[mode]);
			Event.observe(this.element, 'mouseover', this.onMouseOver);
			Event.observe(this.element, 'mouseout', this.onMouseOut);
		}
		else {
			if (isSet(this.mode)) {
				this.element.restoreHandlers('onclick');
				Event.stopObserving(this.element, 'click', riot.stopEvent, true);
				Event.stopObserving(this.element, 'mouseup', this.handlers[this.mode]);
				Event.stopObserving(this.element, 'mouseover', this.onMouseOver);
				Event.stopObserving(this.element, 'mouseout', this.onMouseOut);
			}
		}
		this.mode = mode;
	},
	
	showOutline: function(event) {
		if (Prototype.Browser.IE) {
			if (riot.hoverTimeout) clearTimeout(riot.hoverTimeout);
			Position.clone(this.element, riot.hover, {offsetTop: -2, offsetLeft: -2});
			riot.hover.onclick = this.handlers[this.mode];
			riot.hover.show();
		}
		else {
			this.element.addClassName('riot-outline');
		}
		Event.stop(event);
	},
	
	hideOutline: function(event) {
		if (Prototype.Browser.IE) {
			if (riot.hover != event.toElement) {
				riot.hoverTimeout = setTimeout(riot.hideHover, 250);
			}
		}
		else {
			this.element.removeClassName('riot-outline');
		}
		Event.stop(event);
	},
	
	removeComponent: function(event) {
		var e = event || window.event;
		if (e) Event.stop(e);
		riot.hideHover();
		new Effect.Remove(this.element, function(el) {
			el.remove();
			el.component.componentList.componentRemoved();
		});
		ComponentEditor.deleteComponent(this.id);
		riot.toolbar.setDirty(this.componentList, true);
		return false;
	},
		
	setType: function(type) {
		ComponentEditor.setType(this.componentList.controllerId, this.id, type, this.typeChanged.bind(this));
	},
	
	typeChanged: function(info) {
		this.element.removeClassName('riot-component-' + this.type);
		this.element.addClassName('riot-component-' + info.type);
		this.type = info.type;
		this.form = info.form;
		this.setHtml(info.html);
		if (info.onChangeScript) {
			eval(info.onChangeScript);
		}
		riot.toolbar.setDirty(this.componentList, true);
	},
			
	updateText: function(key, value) {
		ComponentEditor.updateText(this.componentList.controllerId, this.id, key, value, this.setHtml.bind(this));
		riot.toolbar.setDirty(this.componentList, true);
	},
	
	setHtml: function(html) {
		this.element.innerHTML = html.stripScripts();
		this.setupElement();
		setTimeout(function() { html.evalScripts() }, 10);
		this.repaint();
	},
	
	editProperties: function(ev) {
		ev = ev || window.event;
		if (ev) Event.stop(ev);
		this.element.removeClassName('riot-highlight');
		if (this.form) {
			var formUrl = riot.contextPath + this.form + '?instantPublish=' + riot.toolbar.instantPublishMode;
			var iframe = RBuilder.node('iframe', {src: formUrl, className: 'properties', width: 1, height: 1});
			riot.popup = new riot.Popup('${properties-inspector.title}', iframe, function() {
				var win = iframe.contentWindow ? iframe.contentWindow : iframe.window;
				win.save();
			});
			riot.popup.component = this;
		}
		return false;
	},
	
	propertiesChanged: function() {
		riot.toolbar.setDirty(this.componentList, true);
		riot.toolbar.buttons.browse.click();
		ComponentEditor.getComponent(this.componentList.controllerId, this.id, 
			function(info) {
				this.setHtml(info.html);
				if (info.onChangeScript) {
					eval(info.onChangeScript);
				}
				riot.popup.close();
			}.bind(this));
	},
	
	repaint: function() {
		var next = this.element.next();
		if (next) {	next.forceRerendering(); }
	},
	
	setupElement: function() {
		this.editors = [];
		var desc = this.element.descendants();
		desc.push(this.element);
		for (var i = 0; i < desc.length; i++) {
			var e = desc[i];
			try {
				var editorType = e.getAttribute('riot:editorType');
				if (editorType) {
					if (e == this.element || this.element == Element.up(e, '.riot-component')) {
						if (editorType == 'text' || editorType == 'multiline') {
							this.editors.push(new riot.InplaceTextEditor(e, this, {multiline: true}));
						}
						else if (editorType == 'textarea') {
							this.editors.push(new riot.PopupTextEditor(e, this));
						}
						else if (editorType == 'richtext') {
							this.editors.push(new riot.RichtextEditor(e, this, {useInnerHtmlAsDefault: true}));
						}
						else if (editorType == 'richtext-chunks') {
							this.editors.push(new riot.RichtextEditor(e, this, {split: true, useInnerHtmlAsDefault: true}));
						}
					}
				}
			}
			catch (ex) {
				// getAttribute('riot:editorType') fails in IE for TABLE elements (and maybe others?)
			}
		}
		
		// Adopt float and clear styles from the child element(s) ...
		var c = this; 
		c.element.immediateDescendants().each(function(child) {
			var cssFloat = child.getStyle('float');
			if (cssFloat != 'none') {
				c.element.style.zIndex = 1;
				c.element.style.cssFloat = cssFloat;
				c.element.style.styleFloat = cssFloat;
				child.style.cssFloat = 'none';
				child.style.styleFloat = 'none';
			}
			var cssClear = child.getStyle('clear');
			if (cssClear != 'none') {
				c.element.style.clear = cssClear;
			}
		});
		
		if (this.editing) {
			this.edit(true);
		}
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
	},
	
	onupdate: function(infos) {
		this.setHtml(infos[0].html);
		var prevEl = this.element;
		for (var i = 1; i < infos.length; i++) {
			var e = RBuilder.node('div', {className: 'riot-component'});
			e.insertSelfAfter(prevEl);
			var c = new riot.Component(this.componentList, e);
			c.id = infos[i].id;
			c.editing = true;
			c.typeChanged(infos[i]);
			prevEl = e;
		}
		this.componentList.updatePositionClasses();
		riot.toolbar.setDirty(this.componentList, true);
	}
	
};

riot.InsertButton = Class.create();
riot.InsertButton.prototype = {
	initialize: function(component, componentList) {
		this.component = component;
		this.componentList = componentList || component.componentList;
		this.element = RBuilder.node('div', {className: 'riot-insert-button', 
				onclick: this.onclick.bindAsEventListener(this)}).hide();
	},
	
	show: function() {
		this.element.show();
	},
	
	hide: function() {
		this.element.hide();
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
		var e = RBuilder.node('div', {className: 'riot-component'});
		e.insertSelfBefore(this.element);
		var c = new riot.Component(this.componentList, e);
		ComponentEditor.insertComponent(this.componentList.controllerId, 
				this.componentList.id, -1, type, null, c.created.bind(c));
		
		if (this.inspector) {
			this.inspector.onchange = c.setType.bind(c);
		}		
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
				if (isSet(inspector.activeTypeButton)) {
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
			RBuilder.node('div', {className: 'headline'}, '${type-inspector.title}'), 
			select
		);
	}
}


riot.PublishWidget = Class.create();
riot.PublishWidget.prototype = {
	initialize: function(componentList) {
		this.componentList = componentList;
		this.element = RBuilder.node('div', {className: 'riot-publish-widget'},
			this.wrapper = RBuilder.node('div', {},
				RBuilder.node('div', {className: 'riot-tabs'},
					RBuilder.node('div', {className: 'riot-tab-preview', onclick: this.showPreviewVersion.bind(this)}, 'Preview'),
					RBuilder.node('div', {className: 'riot-tab-live', onclick: this.showLiveVersion.bind(this)}, 'Live')
				),
				RBuilder.node('div', {className: 'riot-publish-header'}, 
					RBuilder.node('a', {className: 'riot-publish-button', href: '#', onclick: componentList.publishChanges.bind(componentList)},
						RBuilder.node('span', {className: 'icon'}), 
						RBuilder.node('span', {className: 'text'}, '${publish-dialog.publish}')
					),
					RBuilder.node('a', {className: 'riot-discard-button', href: '#', onclick: componentList.discardChanges.bind(componentList)},
						RBuilder.node('span', {className: 'icon'}), 
						RBuilder.node('span', {className: 'text'}, '${publish-dialog.discard}')
					)
				)
			)
		);
		this.liveElement = RBuilder.node('div', {}, 'Retrieving live version ...');
		this.footer = RBuilder.node('div', {className: 'riot-publish-footer'});
	},
	
	show: function() {
		this.liveHtmlRetrieved = false;
		this.showPreviewVersion();
		this.element.insertSelfBefore(this.componentList.element);
		this.liveElement.insertSelfAfter(this.element);
		this.footer.insertSelfAfter(this.componentList.element);
	},
	
	hide: function() {
		if (this.element.parentNode) {
			this.element.remove();
			this.liveElement.remove();
			this.footer.remove();
			this.componentList.element.show();
		}
	},
	
	showPreviewVersion: function() {
		this.wrapper.className = 'riot-preview-version';
		this.liveElement.hide();
		this.componentList.element.show();
	},
	
	showLiveVersion: function() {
		this.wrapper.className = 'riot-live-version';
		this.componentList.element.hide();
		this.liveElement.show();
		if (!this.liveHtmlRetrieved) {
			this.liveHtmlRetrieved = true;
			ComponentEditor.getLiveListHtml(this.componentList.controllerId,
					this.componentList.id, this.setLiveHtml.bind(this));
		}
	},
	
	setLiveHtml: function(html) {
		this.liveElement.innerHTML = html;
	}
}

riot.ComponentList = Class.create();
riot.ComponentList.prototype = {
	initialize: function(el) {
		this.element = el;
		el.componentList = this;
		this.id = el.getAttribute('riot:listId');
		this.controllerId = el.getAttribute('riot:controllerId');
		this.maxComponents = el.getAttribute('riot:maxComponents');
		this.minComponents = el.getAttribute('riot:minComponents');
		if (!el.id) el.id = 'ComponentList' + this.id;
		ComponentEditor.getValidTypes(this.controllerId, 
				this.setValidTypes.bind(this));
	},
	
	setValidTypes: function(types) {
		this.types = types;
		if (types.length == 1) {
			this.fixedType = types[0].type;
		}
	},
	
	getComponents: function() {
		if (!this.components) {
			this.components = [];
			var elements = this.element.getElementsByClassName('riot-component');
			for (var i = 0; i < elements.length; i++) {
				var e = elements[i];
				var listElement = e.up('.riot-components');
				if (listElement == this.element) {
					var c = e.component || new riot.Component(this, e);
					this.components.push(c);
				}
			}
		}
		return this.components;
	},
	
	updatePositionClasses: function() {
		this.components = null;
		var last = this.getComponents().length - 1;
		this.getComponents().each(function(component, index) {
			component.updatePositionClasses(index + 1, index == last);
		});
	},
			
	publishChanges: function() {
		riot.toolbar.setDirty(this, false);
		ComponentEditor.publishList(this.id, 
				this.publishWidget.hide.bind(this.publishWidget));
				
		return false;
	},
	
	discardChanges: function() {
		riot.toolbar.setDirty(this, false);
		ComponentEditor.discardListAndGetPreviewHtml(this.controllerId,
				this.id, this.replaceHtml.bind(this));
				
		return false;
	},
	
	replaceHtml: function(html) {
		this.element.innerHTML = html;
		this.publishWidget.hide();
		this.components = null;
		this.insertButton = null;
	},
	
	browse: function() {
	},
	
	edit: function(enable) {
		this.getComponents().invoke('edit', enable); 
		if (!enable) {
			riot.activeEditor = null;
		}
	},
	
	insert: function(enable) {
		if (!isSet(this.maxComponents) || this.getComponents().length < this.maxComponents) {
			if (enable) {
				if (!this.insertButton) {
					this.insertButton = new riot.InsertButton(null, this);
					this.element.appendChild(this.insertButton.element);
				}
				this.insertButton.show();
			}
			else {
				this.insertButton.hide();
			}
			riot.activeInsertButton = null;
		}
	},
	
	validateMaxComponents: function() {
		if (this.insertButton && isSet(this.maxComponents) && this.getComponents().length >= this.maxComponents) {
			this.insertButton.hide();
			riot.activeInsertButton = null;
		}
	},

	move: function(enable) {
		if (this.getComponents().length > 1) {
			this.element.toggleClassName('riot-mode-move', enable);
			if (enable) {
				Sortable.create(this.element, {tag: 'div', only: 'riot-component', 
						overlap: 'vertical',
						constraint: 'vertical', 
						scroll: window, scrollSpeed: 20
				});
				this.getComponents().each(function(component) {
					component.element.addClassName('riot-moveable-component').observe('click', riot.stopEvent, true).disableHandlers('onclick');
				});
				Draggables.addObserver(new riot.ComponentDragObserver(this));
			}
			else {
				this.getComponents().each(function(component) {
					component.element.removeClassName('riot-moveable-component').stopObserving('click', riot.stopEvent, true).restoreHandlers('onclick');
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
	},
		
	properties: function(enable) {
		this.element.toggleClassName('riot-mode-properties', enable);
		this.getComponents().invoke('setMode', enable ? 'properties' : null);
	},
	
	publish: function(enable) {
		if (!this.publishWidget) this.publishWidget = new riot.PublishWidget(this);
		if (enable) {
			this.publishWidget.show();
		}
		else {
			this.publishWidget.hide();
		}
	}
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
			riot.toolbar.setDirty(this.componentList, true);
			if (nextEl) { nextEl.forceRerendering(); }
		}
		this.nextEl = null;
	}
}

riot.editProperties = function(e) {
	e = e || this;
	var componentElement = Element.up(e, '.riot-component');
	if (componentElement && (!componentElement.component || !componentElement.component.mode)) {
		riot.toolbar.buttons.properties.click();
		componentElement.component.properties();
	}
	return false;
}

dwr.engine.setErrorHandler(function(err, ex) {
	if (err == 'Request context has expired') { // RequestContextExpiredException
		location.reload();
	}
	else {
		alert(err);
	}
});

riot.toolbar.activate();
