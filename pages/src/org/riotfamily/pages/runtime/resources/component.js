riot.Component = Class.create();
riot.Component.prototype = {

	initialize: function(componentList, el) {
		this.componentList = componentList;
		this.element = el;
		
		el.component = this;
		el.style.position = 'relative';
			
		this.handlers = {
			'remove': this.remove.bindAsEventListener(this),
			'changeType': this.changeType.bindAsEventListener(this),
			'properties': this.properties.bindAsEventListener(this)
		};

		Object.bindMethods(this, [
			'created',
			'setHtml',
			'typeChanged',
			'createTypeInspector',
			'propertiesChanged',
			'onupdate'
		]);
		
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
		riot.toolbar.buttons.browse.click();
		if (!this.componentList.fixedType) {
			this.changeType();
		}
	},
	
	setMode: function(mode) {
		if (mode == null) {
			Element.stopHighlighting(this.element);
			if (isSet(this.mode)) {
				Event.stopObserving(this.element, 'click', this.handlers[this.mode]);
			}
		}
		else {
			if (mode == 'properties' && !this.formId) {
				this.mode = null;
				return;
			}
			Element.hoverHighlight(this.element, 'riot-highlight');
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
	
	changeType: function(event) {
		var e = event || window.event;
		if (e) Event.stop(e);
		Element.removeClassName(this.element, 'riot-highlight');
		ComponentEditor.getValidTypes(this.componentList.controllerId, 
				this.createTypeInspector);
	},
	
	properties: function(event) {
		var e = event || window.event;
		if (e) Event.stop(e);
		Element.removeClassName(this.element, 'riot-highlight');
		if (this.formId) {
			var win = window.open(riot.path + '/pages/form/' + this.id
				+ '?instantPublish=' + riot.toolbar.instantPublishMode, 
				'componentProperties', 
				'width=760,height=400,dependent=yes,toolbar=no,location=no,' +
				'menubar=no,status=no,scrollbars=yes,resizable=yes');
				
			WindowCallback.register(win, this.propertiesChanged);
		}
	},
			
	createTypeInspector: function(types) {
		var select = Element.create('div', {className: 'type-inspector'});
		for (var i = 0; i < types.length; i++) {
			var e = Element.create('div', {className: 'type'}, types[i].description);
			e.type = types[i].type;
			if (e.type == this.type) {
				this.activeTypeButton = e;
				e.className = 'active-type';
			}
			e.component = this;
			e.onclick = function() {
				var c = this.component;
				Element.removeClassName(c.element, 'riot-component-' + c.type);
				Element.addClassName(c.element, 'riot-component-' + this.type);
				c.type = this.type;
				if (isSet(c.activeTypeButton)) {
					c.activeTypeButton.className = 'type';
				}
				this.className = 'active-type'
				c.activeTypeButton = this;
				
				ComponentEditor.setType(c.componentList.controllerId, c.id, c.type, c.typeChanged);
			}
			select.appendChild(e);
		}
		
		var inspector = Element.create('div', {},
			Element.create('div', {className: 'riot-close-button', onclick: riot.toolbar.hideInspector.bind(riot.toolbar)}), 
			Element.create('h2', {}, '${type-inspector.title}'), 
			select
		);

		riot.toolbar.setInspector(inspector, this);
	},
		
	typeChanged: function(info) {
		this.type = info.type;
		this.formId = info.formId;
		this.setHtml(info.html);
		riot.toolbar.setDirty(this.componentList, true);
	},
			
	updateText: function(key, value) {
		ComponentEditor.updateText(this.componentList.controllerId, this.id, key, value, this.setHtml);
		riot.toolbar.setDirty(this.componentList, true);
	},
	
	setHtml: function(html) {
		this.element.innerHTML = html.stripScripts();
		this.setupElement();
		setTimeout(function() { html.evalScripts() }, 10);
		this.repaint();
	},
	
	propertiesChanged: function() {
		riot.toolbar.setDirty(this.componentList, true);
		ComponentEditor.getHtml(this.componentList.controllerId, this.id, this.setHtml);
	},
	
	repaint: function() {
		var next = Element.getNextSiblingElement(this.element);
		if (next != null) {	Element.repaint(next); }
	},
	
	setupElement: function() {
		var editors = this.editors = new Array();
		var component = this;
		
		Element.getDescendants(this.element).each(function(e) {
			var editorType = e.getAttribute('riot:editorType');
			if (editorType == 'text') {
				editors.push(new riot.InplaceTextEditor(e, component));
			}
			else if (editorType == 'multiline') {
				editors.push(new riot.InplaceTextEditor(e, component, {multiline: true}));
			}
			else if (editorType == 'textarea') {
				editors.push(new riot.PopupTextEditor(e, component));
			}
			else if (editorType == 'textile') {
				editors.push(new riot.TextileEditor(e, component));
			}
			else if (editorType == 'markdown') {
				editors.push(new riot.MarkdownEditor(e, component));
			}
			else if (editorType == 'richtext') {
				editors.push(new riot.RichtextEditor(e, component));
			}
		});
		if (this.editing) {
			this.edit(true);
		}
	},
	
	updatePositionClasses: function(position, last) {
		document.getElementsByClassName('component-\\w*', this.element).each(function(el) {
			el.className = el.className.replace(/component-\w*/, 'component-' + position);
			Element.toggleClassName(el, 'last-component', last);
		});
	},
	
	onupdate: function(infos) {
		this.setHtml(infos[0].html);
		var prevEl = this.element;
		for (var i = 1; i < infos.length; i++) {
			var e = Element.create('div', {className: 'riot-component'});
			Element.insertAfter(e, prevEl);
			var c = new riot.Component(this.componentList, e);
			c.id = infos[i].id;
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
		this.element = document.createElement('riot');
		this.element.className = 'riot-insert-button';
		this.element.onclick = this.onclick.bindAsEventListener(this);
		Element.hide(this.element);
	},
	
	show: function() {
		Element.show(this.element);
	},
	
	hide: function() {
		Element.hide(this.element);
	},
	
	onclick: function() {
		var e = Element.create('div', {className: 'riot-component'});
		Element.insertBefore(e, this.element);
		var c = new riot.Component(this.componentList, e);
		ComponentEditor.insertComponent(this.componentList.controllerId, 
				this.componentList.id, -1, null, null, c.created);
	}
};

riot.PublishWidget = Class.create();
riot.PublishWidget.prototype = {
	initialize: function(componentList) {
		this.componentList = componentList;
		this.element = Element.create('div', {className: 'riot-publish-widget'},
			this.wrapper = Element.create('div', {},
				Element.create('div', {className: 'riot-tabs'},
					Element.create('div', {className: 'riot-tab-preview', onclick: this.showPreviewVersion.bind(this)}, 'Preview'),
					Element.create('div', {className: 'riot-tab-live', onclick: this.showLiveVersion.bind(this)}, 'Live')
				),
				Element.create('div', {className: 'riot-publish-header'}, 
					Element.create('a', {className: 'riot-publish-button', href: '#', onclick: componentList.publishChanges.bind(componentList)},
						Element.create('span', {className: 'icon'}), 
						Element.create('span', {className: 'text'}, '${publish-dialog.publish}')
					),
					Element.create('a', {className: 'riot-discard-button', href: '#', onclick: componentList.discardChanges.bind(componentList)},
						Element.create('span', {className: 'icon'}), 
						Element.create('span', {className: 'text'}, '${publish-dialog.discard}')
					)
				)
			)
		);
		this.liveElement = Element.create('div', {}, 'Retrieving live version ...');
		this.footer = Element.create('div', {className: 'riot-publish-footer'});
	},
	
	show: function() {
		this.liveHtmlRetrieved = false;
		this.showPreviewVersion();
		Element.insertBefore(this.element, this.componentList.element);
		Element.insertAfter(this.liveElement, this.element);
		Element.insertAfter(this.footer, this.componentList.element);
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
		this.fixedType = el.getAttribute('riot:fixedType') == 'true';
		if (!el.id) el.id = 'ComponentList' + this.id;
	},
	
	getComponents: function() {
		if (!this.components) {
			this.components = [];
			var _this = this;
			document.getElementsByClassName('riot-component', this.element).each(function (e) {
				var listElement = Element.getAncestorWithClassName(e, 'riot-components');
				if (listElement == _this.element) {
					var c = e.component || new riot.Component(_this, e);
					_this.components.push(c);
				}
			});
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
		}
	},

	move: function(enable) {
		if (this.getComponents().length > 1) {
			if (enable) {
				Sortable.create(this.element, {tag: 'div', only: 'riot-component', scroll: window, scrollSpeed: 20});
				this.getComponents().each(function(component) {
					Element.addClassName(component.element, 'riot-moveable-component');
					component.element.onclick = Event.stop;
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
		Element.toggleClassName(this.element, 'riot-mode-remove', enable);
		this.getComponents().invoke('setMode', enable ? 'remove' : null);
	},
	
	changeType: function(enable) {
		if (!this.fixedType) {
			Element.toggleClassName(this.element, 'riot-mode-changeType', enable);
			this.getComponents().invoke('setMode', enable ? 'changeType' : null);
		}
	},
	
	properties: function(enable) {
		Element.toggleClassName(this.element, 'riot-mode-properties', enable);
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
			if (nextEl) { Element.repaint(nextEl); }
		}
		this.nextEl = null;
	}
}

riot.editProperties = function(e) {
	e = e || this;
	var componentElement = Element.getAncestorWithClassName(e, 'riot-component');
	if (componentElement) {
		riot.toolbar.buttons.properties.click();
		componentElement.component.properties();
	}
}
