/**
 * Returns a wrapper for the given element. A wrapper is either an  EntityList, 
 * ComponentList, Component or an editor. What kind of wrapper
 * is returned depends on the given CSS selector. The function is invoked by
 * ToolbarButton.getHandlerTargets() when a button's handler is to be applied.
 */
riot.getWrapper = function(el, selector) {
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

/**
 * Returns a text editor for the given element. What kind of editor is returned
 * depends on the element's riot:editorType attribute.
 */
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

/**
 * Retruns an image editor for the given element.
 */
riot.getImageEditor = function(el) {
	if (!el.imageEditor) {
		el.imageEditor = new riot.ImageEditor(el, riot.findComponent(el), {
			srcTemplate: el.readAttribute('riot:srcTemplate'),
			minWidth: el.readAttribute('riot:minWidth'),
			maxWidth: el.readAttribute('riot:maxWidth'),
			minHeight: el.readAttribute('riot:minHeight'),
			maxHeight: el.readAttribute('riot:maxHeight'),
			updateFromServer: el.readAttribute('riot:updateFromServer')
		});
	}
	return el.imageEditor;
}

/**
 * Returns either an EntityComponent, ListComponent or Component wrapper for
 * the given element. What type of wrapper is returned depends on the element's
 * CSS class.
 */
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

/**
 * Searches the element's ancestors (including the element itself) for an
 * element with the class 'riot-component' and returns the wrapper for that
 * element.
 */
riot.findComponent = function(el) {
	if (!el.hasClassName('riot-component')) {
		el = el.up('.riot-component');
	}
	if (!el) return;
	return riot.getComponent(el);
}

/**
 * Searches the element's ancestors (including the element itself) for an
 * element with the class 'riot-component-list' and returns the wrapper for that
 * element.
 */
riot.findComponentList = function(el) {
	el = el.up('.riot-component-list');
	if (el) {
		return riot.getComponentList(el);
	}
	return null;
}

/**
 * Returns the ComponentList wrapper for the given element.
 */
riot.getComponentList = function(el) {
	if (!el.componentList) {
		el.componentList = new riot.ComponentList(el);
	}
	return el.componentList;
}

riot.ComponentList = Class.create({ 
	initialize: function(el) {
		this.element = el;
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
		
	markDirty: function() {
		this.dirty = true;
	},
	
	insertOn: function() {
		if (!this.maxComponents || this.componentElements.length < this.maxComponents) {
			this.element.addClassName('riot-mode-insert');
			this.insertButton = new riot.InsertButton(this);
			riot.activeInsertButton = null;
		}
	},
	
	insertOff: function() {
		if (this.insertButton) {
			this.element.removeClassName('riot-mode-insert');
			this.insertButton.remove();
			this.insertButton = null;
			riot.activeInsertButton = null;
		}
	},

	moveOn: function() {
		if (this.componentElements.length > 1) {
			this.element.addClassName('riot-mode-move');
			this.element.style.position = 'relative';
			this.componentElements.each(function(el) {
				if (!el.down('.riot-component-list')) {
					//el.disableEvents();
				}
			});
		}
	},
	
	makeSortable: function() {
		if (this.componentElements.length > 1) {
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
				el.addClassName('riot-moveable-component');
				Droppables.drops.find(function(drop) {
					if (drop.element == el) {
						drop.onHover = riot.onHover;
					}
				});
			});
									
			Draggables.addObserver(new riot.ComponentDragObserver(this));
		}
	},
	
	moveOff: function() {
		if (this.componentElements.length > 1) {
			this.element.removeClassName('riot-mode-move');
			Sortable.destroy(this.element);
			Draggables.removeObserver(this.element);
			this.componentElements.each(function(el) {
				el.removeClassName('riot-moveable-component');
				if (!el.down('.riot-component-list')) {
					//el.enableEvents();
				}
			});
			this.updatePositionClasses();
		}
	},
	
	updatePositionClasses: function() {		
		var last = this.componentElements.length - 1;
		var prev = null;
		for (var i = 0; i <= last; i++) {
			var el = this.componentElements[i];
			el.floatStyle = null;
			el.descendants().each(function(desc) {
				if (desc.className && desc.up('.riot-component') == el) {
					// Set "first" or "not-first" class					
					var firstExp = /(^|\s)(not-)?first(\s|$)/; 
					if (firstExp.test(desc.className)) {
						var firstClass = (i == 0) ? 'first' : 'not-first'; 
						desc.className = desc.className.replace(firstExp, '$1' + firstClass + '$3');
					}
					// Set "last" or "not-last" class
					var lastExp = /(^|\s)(not-)?last(\s|$)/; 
					if (lastExp.test(desc.className)) {
						var lastClass = (i == last) ? 'last' : 'not-last'; 
						desc.className = desc.className.replace(lastExp, '$1' + lastClass + '$3');
					}
					// Set "even" or "odd" class
					var zebraExp = /(^|\s)(even|odd)(\s|$)/; 
					if (zebraExp.test(desc.className)) {
						var zebraClass = (i % 2 == 0) ? 'even' : 'odd'; 
						desc.className = desc.className.replace(zebraExp, '$1' + zebraClass + '$3');
					}
					// Set "every-nth" or "not-every-nth" class
					var modExp = /(^|\s)(not-)?every-(\d+)(nd|rd|th)(\s|$)/;
					var match = desc.className.match(modExp);
					if (match) {
						var nth = match[3];
						var every = ((i + 1) % nth == 0) ? 'every' : 'not-every';
						desc.className = desc.className.replace(modExp, '$1' + every + '-$3$4$5');
					}
				}
			});
		}
		riot.adoptFloatsAndClears(this.element);
	},
	
	removeOn: function() {
		this.element.addClassName('riot-mode-remove');
		if (this.componentElements.length > this.minComponents) {
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
		var list = this;
		new Effect.Remove(c.element, function(el) {
			el.remove();
			list.updatePositionClasses();
			riot.outline.suspended = false;
		});
		this.markDirty();
		if (this.minComponents > 0 && this.componentElements.length == this.minComponents) {
			this.removeOff();
		} 
	}
	
});

riot.onHover = function(element, dropon, overlap) {
	var x = parseInt(element.style.left);
	var y = parseInt(element.style.top);
	var w = element.offsetWidth;
	var h = element.offsetHeight;
	var outside = y > h || x > w || y < -h || x < -w;
	if (!outside) {
		return;
	} 

    if (!dropon.floatStyle) dropon.floatStyle = dropon.getStyle('float');
    if (dropon.floatStyle != 'none') {
    	overlap = Position.overlap('horizontal', dropon);
    }
    
    Sortable.onHover(element, dropon, overlap);
}

riot.ComponentDragObserver = Class.create({
	initialize: function(componentList) {
		this.componentList = componentList;
		this.element = componentList.element;
	},
	
	onStart: function(eventName, draggable, event) {
		var el = draggable.element;
		el.addClassName('riot-drag');
		// If the dragged element is floating, remove the vertical movement constraint
		if (el.getStyle('float') != 'none') {
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
			var nextId = null;
			if (nextEl) {
				nextId = riot.getComponent(nextEl).id;
				nextEl.forceRerendering();
			}
			ComponentEditor.moveComponent(riot.getComponent(el).id, nextId);
			this.componentList.markDirty();
		}
		this.componentList.updatePositionClasses();
		this.nextEl = null;
	}
});

riot.EntityList = Class.create({

	initialize: function(el) {
		this.element = el;
		this.id = el.readAttribute('riot:listId');
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
		this.id = el.readAttribute('riot:componentId');
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
	
	cropImage: function(key, imageId, w, h, x, y, sw, callback) {
		ComponentEditor.cropImage(this.id, key, imageId,
				w, h, x, y, sw, callback);
	},
	
	markDirty: function() {
		this.dirty = true;
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
		window.location.reload();
	}
});

riot.ListComponent = Class.create(riot.Component, {

	updateTextChunks: function(key, chunks) {
		ComponentEditor.updateTextChunks(this.id, key, chunks, this.update.bind(this));
		this.markDirty();
	},
	
	update: function() {
		ComponentEditor.renderComponent(this.id, this.replaceHtml.bind(this));
	},
	
	replaceHtml: function(html) {
		var tmp = RBuilder.node('div');
		tmp.update(html);
		var el = tmp.down();
		this.element.replaceBy(el);
		this.element = el;
		if (window.riotEditCallbacks) {
			riotEditCallbacks.each(function(callback) {
				callback(el);
			});
		}
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
	
	cropImage: function(key, imageId, w, h, x, y, sw, callback) {
		EntityEditor.cropImage(this.listId, this.objectId, key, imageId,
				w, h, x, y, sw, callback);
	},
	
	update: function() {
		window.location.reload();
	}
});

/**
 * Button to append components to a list.
 */
riot.InsertButton = Class.create({

	initialize: function(componentList) {
		this.componentList = componentList;
		this.element = RBuilder.node('div', {className: 'riot-insert-button',
				onclick: this.onclick.bindAsEventListener(this)});

		ComponentEditor.getValidTypes(componentList.id, this.setValidTypes.bind(this));
	},
	
	setValidTypes: function(types) {
		this.types = types;
		this.element.insertSelfAfter(this.componentList.element);
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

	oninsert: function(html) {
		var tmp = RBuilder.node('div');
		tmp.update(html);
		var el = this.componentElement = tmp.down();
		this.componentList.element.appendChild(el);
		this.componentList.findComponentElements();
		this.componentList.updatePositionClasses();
		this.componentId = el.readAttribute('riot:componentId');
		if (window.riotEditCallbacks) {
			riotEditCallbacks.each(function(callback) {
				callback(el);
			});
		}
	},

	onupdate: function(html) {
		var tmp = RBuilder.node('div');
		tmp.update(html);
		var el = tmp.down();
		this.componentElement.replaceBy(el);
		this.componentList.findComponentElements();
		this.componentElement = el;
		if (window.riotEditCallbacks) {
			riotEditCallbacks.each(function(callback) {
				callback(el);
			});
		}
	},
	
	changeType: function(type) {
		ComponentEditor.setType(this.componentId, type, this.onupdate.bind(this));
	}
});

/**
 * Inpector panel to choose a component type.
 */
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

riot.setLiveHtml = function(html) {
	if (riot.publishWidgets) { 
		// If apply has been clicked quicker than the lists have been loaded, do nothing ...
		for (var i = 0; i < riot.publishWidgets.length; i++) {
			riot.publishWidgets[i].setLiveHtml(html[i]);
		}
	}
}

/**
 * DWR error handler that reloads the page if the RequestContext has expired
 */ 
dwr.engine.setErrorHandler(function(err, ex) {
	if (ex.javaClassName == 'org.riotfamily.components.context.RequestContextExpiredException') {
		location.reload();
	}
	else {
		alert(err);
	}
});

/**
 * Moves the float and clear style of the component's one and only child element
 * to the the component's div.
 */
riot.adoptFloatsAndClears = function(root) {
	Selector.findChildElements(root || document, ['.riot-component']).each(function(el) {
		var c = el.childElements();
		if (c.length == 1) {
			var child = c[0];
			if (child.originalFloat) {
				// Styles were already adopted. Reset the float as it might 
				// have changed meanwhile
				child.style.cssFloat = child.style.styleFloat = '';
			}
			var cssFloat = child.getStyle('float');
			if (cssFloat != 'none') {
				if (!el.style.zIndex) el.style.zIndex = 1;
				el.style.cssFloat = el.style.styleFloat = cssFloat;
				child.style.cssFloat = child.style.styleFloat = 'none';
				// Store the original float which is used by inplace.js ...
				child.originalFloat = cssFloat;
			}
			var cssClear = child.getStyle('clear');
			el.style.clear = cssClear;
		}
	});
}

riot.init = function() {
	var b = riot.toolbar.buttons;
	
	// Make lists sortable after moveOn() has been invoked. This has to be
	// done in reverse order to support nested sortable lists.
	b.get('move').afterApply = function(enable) {
		if (enable) {
			$$('.riot-component-list').reverse().each(function(el) {
				riot.getComponentList(el).makeSortable();
			});
		}
	};
	
	b.get('editImages').precondition = riot.initSwfUpload;
};

riot.discardOn = function() {
	if (!riot.previewFrame.initialized) {
		var params = $H(window.location.search.parseQuery());
		params.set('org.riotfamily.components.EditModeUtils.liveMode', 'true');
		riotPreviewFrame.location.href = window.location.pathname + '?' + params.toQueryString();
		riot.previewFrame.initialized = true;
	}
	riot.previewFrame.setStyle({
		display: 'block',
		visibility: 'visible'
	}); 
	$$('body > *:not(#riotPreviewFrame)').invoke('hide');
	riot.applyFunction = ComponentEditor.discard;
}

riot.discardOff = function() {
	riot.previewFrame.hide();
	$$('body > *:not(#riotPreviewFrame)').invoke('show');
}

riot.publishOn = function() {
	riot.toolbar.applyButton.enable();
	riot.applyFunction = ComponentEditor.publish;
}

riot.publishOff = function() {
	riot.toolbar.applyButton.disable();
}

riot.applyOn = function() {
	riot.toolbar.buttons.get('browse').click();
	var listIds = $$('.riot-component-list')
		.collect(riot.getComponentList)
		.pluck('id');
			
	var containerIds = $$('.riot-single-component')
		.collect(riot.getComponent)
		.pluck('id'); 
	
	riot.applyFunction(listIds, containerIds);
	riot.previewFrame.initialized = false;
}

riot.previewFrame = RBuilder.node('iframe', {name: 'riotPreviewFrame', id: 'riotPreviewFrame'}).appendTo(document.body);
riot.init();
riot.adoptFloatsAndClears();
riot.toolbar.activate();