riot.Component = Class.create();
riot.Component.prototype = {

	initialize: function(componentList, el) {
		this.componentList = componentList;
		this.element = el;
		
		el.component = this;
		el.style.position = 'relative';
			
		this.handlers = {
			'remove': this.remove.bindAsEventListener(this),
			'properties': this.properties.bindAsEventListener(this)
		};
		
		this.id = el.getAttribute('riot:containerId');
		if (this.id) {
			this.type = el.getAttribute('riot:componentType');
			this.formId = el.getAttribute('riot:formId');
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
		if (mode == null) {
			RElement.stopHighlighting(this.element);
			if (isSet(this.mode)) {
				Event.stopObserving(this.element, 'click', this.handlers[this.mode]);
			}
		}
		else {
			if (mode == 'properties' && !this.formId) {
				this.mode = null;
				return;
			}
			RElement.hoverHighlight(this.element, 'riot-highlight');
			Event.observe(this.element, 'click', this.handlers[mode]);
		}
		this.mode = mode;
	},
	
	remove: function(event) {
		var e = event || window.event;
		if (e) Event.stop(e);
		this.setMode(null);
		new Effect.Remove(this.element, function(el) {
			Element.remove(el);
			el.component.componentList.updatePositionClasses();
		});
		ComponentEditor.deleteComponent(this.id);
		riot.toolbar.setDirty(this.componentList, true);
	},
		
	setType: function(type) {
		ComponentEditor.setType(this.componentList.controllerId, this.id, type, this.typeChanged.bind(this));
	},
	
	typeChanged: function(info) {
		Element.removeClassName(this.element, 'riot-component-' + this.type);
		Element.addClassName(this.element, 'riot-component-' + info.type);
		this.type = info.type;
		this.formId = info.formId;
		this.setHtml(info.html);
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
	
	properties: function(event) {
		var e = event || window.event;
		if (e) Event.stop(e);
		Element.removeClassName(this.element, 'riot-highlight');
		if (this.formId) {
			var formUrl = riot.path + '/pages/form/' + this.id + '?instantPublish=' + riot.toolbar.instantPublishMode;
			var iframe = RBuilder.node('iframe', {	src: formUrl, className: 'properties', width: 1, height: 1});
			riot.popup = new riot.Popup('${properties-inspector.title}', iframe, function() {
				var win = iframe.contentWindow ? iframe.contentWindow : iframe.window;
				win.save();
			});
			riot.popup.component = this;
		}
	},
	
	propertiesChanged: function() {
		riot.toolbar.setDirty(this.componentList, true);
		ComponentEditor.getHtml(this.componentList.controllerId, this.id, 
			function(html) {
				this.setHtml(html)
				riot.popup.close();
			}.bind(this));
	},
	
	repaint: function() {
		var next = RElement.getNextSiblingElement(this.element);
		if (next != null) {	RElement.repaint(next); }
	},
	
	setupElement: function() {
		this.editors = [];
		var desc = this.element.descendants();
		for (var i = 0; i < desc.length; i++) {
			var e = desc[i];
			try {
				var editorType = e.getAttribute('riot:editorType');
				if (editorType) {
					var componentElement = RElement.getAncestorByClassName(e, 'riot-component');
					if (componentElement == this.element) {
						if (editorType == 'text' || editorType == 'multiline') {
							this.editors.push(new riot.InplaceTextEditor(e, this, {multiline: true}));
						}
						else if (editorType == 'textarea') {
							this.editors.push(new riot.PopupTextEditor(e, this));
						}
						else if (editorType == 'richtext') {
							this.editors.push(new riot.RichtextEditor(e, this));
						}
						else if (editorType == 'richtext-chunks') {
							this.editors.push(new riot.RichtextEditor(e, this, {split: true}));
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
	},
	
	updatePositionClasses: function(position, last) {
		if (this.type != 'inherit') {
			RElement.getDescendantsByClassName(this.element, 'component-\\w*').each(function(el) {
				el.className = el.className.replace(/component-\w*/, 'component-' + position);
				RElement.toggleClassName(el, 'last-component', last);
			});
		}
	},
	
	onupdate: function(infos) {
		this.setHtml(infos[0].html);
		var prevEl = this.element;
		for (var i = 1; i < infos.length; i++) {
			var e = RBuilder.node('div', {className: 'riot-component'});
			RElement.insertAfter(e, prevEl);
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
		Element.show(this.element);
	},
	
	hide: function() {
		Element.hide(this.element);
	},
	
	onclick: function() {
		if (riot.activeInsertButton) {
			Element.removeClassName(riot.activeInsertButton.element, 
					'riot-insert-button-active');
		}
		if (this.componentList.fixedType) {
			this.insert(this.componentList.fixedType);
			riot.toolbar.removeInspector();
		}
		else {
			Element.addClassName(this.element, 'riot-insert-button-active');
			riot.activeInsertButton = this;
			this.inspector = new riot.TypeInspector(this.componentList.types, null, 
					this.insert.bind(this));
					
			riot.toolbar.setInspector(this.inspector.element);
		}
	},
	
	insert: function(type) {
		var e = RBuilder.node('div', {className: 'riot-component'});
		RElement.insertBefore(e, this.element);
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
			var e = RBuilder.node('div', {className: 'type'}, types[i].description);
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
		RElement.insertBefore(this.element, this.componentList.element);
		RElement.insertAfter(this.liveElement, this.element);
		RElement.insertAfter(this.footer, this.componentList.element);
	},
	
	hide: function() {
		if (this.element.parentNode) {
			Element.remove(this.element);
			Element.remove(this.liveElement);
			Element.remove(this.footer);
			Element.show(this.componentList.element);
		}
	},
	
	showPreviewVersion: function() {
		this.wrapper.className = 'riot-preview-version';
		Element.hide(this.liveElement);
		Element.show(this.componentList.element);
	},
	
	showLiveVersion: function() {
		this.wrapper.className = 'riot-live-version';
		Element.hide(this.componentList.element);
		Element.show(this.liveElement);
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
			var elements = document.getElementsByClassName('riot-component', this.element);
			for (var i = 0; i < elements.length; i++) {
				var e = elements[i];
				var listElement = RElement.getAncestorByClassName(e, 'riot-components');
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
			RElement.toggleClassName(this.element, 'riot-mode-move', enable);
			if (enable) {
				Sortable.create(this.element, {tag: 'div', only: 'riot-component', scroll: window, scrollSpeed: 20});
				this.getComponents().each(function(component) {
					Element.addClassName(component.element, 'riot-moveable-component');
					component.element.onclick = function(event) {
						Event.stop(event || window.event);
					}
				});
				Draggables.addObserver(new riot.ComponentDragObserver(this));
			}
			else {
				this.getComponents().each(function(component) {
					Element.removeClassName(component.element, 'riot-moveable-component');
					component.element.onclick = null;
				});
				Sortable.destroy(this.element);
				Draggables.removeObserver(this.element);
			}
		}
	},

	remove: function(enable) {
		RElement.toggleClassName(this.element, 'riot-mode-remove', enable);
		this.getComponents().invoke('setMode', enable ? 'remove' : null);
	},
	
	changeType: function(enable) {
		if (!this.fixedType) {
			RElement.toggleClassName(this.element, 'riot-mode-changeType', enable);
			this.getComponents().invoke('setMode', enable ? 'changeType' : null);
		}
	},
	
	properties: function(enable) {
		RElement.toggleClassName(this.element, 'riot-mode-properties', enable);
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
		Element.addClassName(draggable.element, 'riot-drag');
		this.nextEl = draggable.element.nextSibling;
	},
	onEnd: function(eventName, draggable, event) {
		var el = draggable.element;
		Element.removeClassName(el, 'riot-drag');
		var nextEl = el.nextSibling;
		if(el.parentNode == this.element && nextEl != this.nextEl) {
			this.componentList.updatePositionClasses();
			var nextId = nextEl && nextEl.component ? nextEl.component.id : null;
			ComponentEditor.moveComponent(el.component.id, nextId);
			riot.toolbar.setDirty(this.componentList, true);
			if (nextEl) { RElement.repaint(nextEl); }
		}
		this.nextEl = null;
	}
}

riot.editProperties = function(e) {
	e = e || this;
	var componentElement = RElement.getAncestorByClassName(e, 'riot-component');
	if (componentElement && (!componentElement.component || !componentElement.component.mode)) {
		riot.toolbar.buttons.properties.click();
	}
}
