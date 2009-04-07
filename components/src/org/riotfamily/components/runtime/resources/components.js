/**
 * Returns a wrapper for the given element. A wrapper is either a ComponentList, 
 * Component or an editor. What kind of wrapper is returned depends on the 
 * given CSS selector. The function is invoked by 
 * ToolbarButton.getHandlerTargets() when a button's handler is to be applied.
 */
riot.getWrapper = function(el, selector) {
	if (selector == '.riot-component-list') {
		return riot.getComponentList(el);
	}
	if (selector == '.riot-component' || selector == '.riot-form') {
		return riot.getContent(el);
	}
	if (selector == '.riot-text-editor') {
		return riot.getTextEditor(el);
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
		var content = riot.findContent(el);
		if (editorType == 'text') {
			el.textEditor = new riot.InplaceTextEditor(el, content, {
				textTransform: el.readAttribute('riot:textTransform') == 'true'
			});
		}
		if (editorType == 'textarea') {
			el.textEditor = new riot.PopupTextEditor(el, content, {useInnerHtmlAsDefault: true});
		}
		if (editorType == 'richtext') {
			el.textEditor = new riot.RichtextEditor(el, content, {useInnerHtmlAsDefault: true, config: el.readAttribute('riot:config')});
		}
		if (editorType == 'richtext-chunks') {
			//TODO Check if content is a component!
			el.textEditor = new riot.RichtextEditor(el, content, {split: true, useInnerHtmlAsDefault: true, config: el.readAttribute('riot:config')});
		}
	}
	return el.textEditor;
}

/**
 * Returns the ContentContainer for the given element.
 */
riot.getContentContainer = function(el) {
	if (!el.contentContainer) {
		el.contentContainer = new riot.ContentContainer(el.readAttribute('riot:containerId'));
	}
	return el.contentContainer;
}

/**
 * Searches the element's ancestors (including the element itself) for an
 * element with the class 'riot-container' and returns the expando for that
 * element.
 */
riot.findContainer = function(el) {
	if (el && !el.hasClassName('riot-container')) {
		el = el.up('.riot-container');
	}
	if (el) {
		return riot.getContentContainer(el);
	}	
}

/**
 * Returns the ComponentList for the given element.
 */
riot.getComponentList = function(el) {
	if (!el.componentList) {
		el.componentList = new riot.ComponentList(el);
	}
	return el.componentList;
}

/**
 * Searches the element's ancestors (including the element itself) for an
 * element with the class 'riot-component-list' and returns the expando for that
 * element.
 */
riot.findComponentList = function(el) {
	el = el.up('.riot-component-list');
	if (el) {
		return riot.getComponentList(el);
	}
	return null;
}

riot.getContent = function(el) {
	if (!el.content) {
		if (el.hasClassName('riot-component')) {
			el.content = new riot.Component(el);
		}
		else {
			el.content = new riot.Content(el);
		}
	}
	return el.content;
}

/**
 * Searches the element's ancestors (including the element itself) for an
 * element with the class 'riot-component' and returns the expando for that
 * element.
 */
riot.findContent = function(el) {
	if (el && !el.hasClassName('riot-content')) {
		el = el.up('.riot-content');
	}
	if (el) {
		return riot.getContent(el);
	}	
}

riot.ContentContainer = Class.create({ 
	initialize: function(id) {
		this.id = id;
	},
	
	markAsDirty: function(callback) {
		ComponentEditor.markAsDirty(this.id, callback);
		riot.toolbar.enablePublishButtons();
	}
});

riot.ComponentList = Class.create({ 
	initialize: function(el) {
		this.element = el;
		this.id = el.readAttribute('riot:listId');
		if (!el.id) el.id = 'riot-list-' + this.id;
		this.config = window["riotComponentListConfig" + this.id];
		this.findComponentElements();
	},
	
	findComponentElements: function() {
		//Note: Element.findChildren is defined in dragdrop.js
		this.componentElements = Element.findChildren(this.element, 'riot-component', false, 'div') || [];
	},
		
	insertOn: function() {
		if (!this.config.max || this.componentElements.length < this.config.max) {
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
					el.disableLinks();
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
					el.enableLinks();
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
					var firstExp = /(^|\s|-)(not-)?first(-|\s|$)/; 
					if (firstExp.test(desc.className)) {
						var firstClass = (i == 0) ? 'first' : 'not-first'; 
						desc.className = desc.className.replace(firstExp, '$1' + firstClass + '$3');
					}
					// Set "last" or "not-last" class
					var lastExp = /(^|\s|-)(not-)?last(-|\s|$)/; 
					if (lastExp.test(desc.className)) {
						var lastClass = (i == last) ? 'last' : 'not-last'; 
						desc.className = desc.className.replace(lastExp, '$1' + lastClass + '$3');
					}
					// Set "even" or "odd" class
					var zebraExp = /(^|\s|-)(even|odd)(-|\s|$)/; 
					if (zebraExp.test(desc.className)) {
						var zebraClass = (i % 2 == 0) ? 'even' : 'odd'; 
						desc.className = desc.className.replace(zebraExp, '$1' + zebraClass + '$3');
					}
					// Set "every-nth" or "not-every-nth" class
					var modExp = /(^|\s)(not-)?every-(\d+)(nd|rd|th)(-|\s|$)/;
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
		if (this.componentElements.length > this.config.min) {
			var list = this;
			var handler = list.removeComponent.bind(list);
			this.componentElements.map(riot.getContent).each(function(c) {
				var min = this.getComponentConfig(c.type).min;
				if (min == 0 || this.countComponents(c.type) > min) {
					c.setClickHandler(handler);
				}
			}, this);
		}
	},
	
	removeOff: function() {
		this.element.removeClassName('riot-mode-remove');
		this.componentElements.each(function(el) {
			var c = riot.getContent(el);
			if (c) c.removeClickHandler();
		});
	},
	
	removeComponent: function(c) {
		ComponentEditor.deleteComponent(c.id);
		c.markAsDirty();
		this.componentElements = this.componentElements.without(c.element);
		riot.outline.hide();
		riot.outline.suspended = true;
		var list = this;
		new Effect.Remove(c.element, function(el) {
			el.remove();
			list.updatePositionClasses();
			riot.outline.suspended = false;
		});
		if (this.config.min > 0 && this.componentElements.length == this.config.min) {
			this.removeOff();
		} 
	},
	
	getComponentConfig: function(type) {
		return this.config.validTypes.find(function(c) {return c.type == type});
	},
	
	countComponents: function(type) {
		return this.componentElements.map(riot.getContent).select(function(c) {return c.type == type}).length;
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
				nextId = riot.getContent(nextEl).id;
				nextEl.forceRerendering();
			}
			var component = riot.getContent(el);
			ComponentEditor.moveComponent(component.id, nextId);
			component.markAsDirty();
		}
		this.componentList.updatePositionClasses();
		this.nextEl = null;
	}
});


riot.Content = Class.create({ 
	initialize: function(el, id) {
		this.element = el;
		this.id = id || el.readAttribute('riot:contentId');
		this.container = riot.findContainer(el);
		this.form = el.readAttribute('riot:form');
		this.autoSizePopup = el.readAttribute('riot:autoSizePopup') != 'false';
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
		this.element.disableLinks();
		this.clickHandler = clickHandler;
		var c = this.element.childElements();
		this.targetElement = c.length == 1 ? c[0] : this.element;
		if (this.targetElement.getStyle('visibility') == 'hidden') {
			this.targetElement = this.element;
		}
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
		this.element.enableLinks();
	},
	
	retrieveText: function(key, callback) {
		ComponentEditor.getText(this.id, key, callback);
	},
	
	updateText: function(key, value, updateFromServer) {
		this.markAsDirty();
		ComponentEditor.updateText(this.id, key, value,	updateFromServer 
				? this.update.bind(this) : Prototype.emptyFunction);
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
		var formUrl = riot.path + '/components/form/' + this.container.id 
				+ '/' + this.id + '/' + this.form + '?' 
				+ $H(riotComponentFormParams).toQueryString();
		
		riot.popup = new riot.Popup('${title.properties}', formUrl, function() {
			var win = this.content.contentWindow || this.content.window;
			win.save();
		}, this.autoSizePopup);
		
		// The ComponentFormSuccessView.ftl will invoke
		// parent.riot.popup.component.propertiesChanged()
		// ... so we need to set a reference:
		riot.popup.component = this;
	},
	
	propertiesChanged: function() {
		riot.popup.close();
		// Timeout as we otherwise get an 0x8004005 [nsIXMLHttpRequest.open] error
		// in Firefox 2.0. See https://bugzilla.mozilla.org/show_bug.cgi?id=249843
		setTimeout(function() {
			this.markAsDirty(this.update.bind(this));
		}.bind(this), 1);
	},
	
	update: function() {
		window.location.reload();		
	},
	
	markAsDirty: function(callback) {
		riot.findContainer(this.element).markAsDirty(callback);
	}
});


riot.Component = Class.create(riot.Content, {

	initialize: function($super, el) {
		$super(el);
		this.type = el.readAttribute('riot:componentType');
	},
	
	updateTextChunks: function(key, chunks) {
		ComponentEditor.updateTextChunks(this.id, key, chunks, this.update.bind(this));
		this.markAsDirty();
	},
	
	update: function() {
		ComponentEditor.renderComponent(this.id, this.replaceHtml.bind(this));
	},
	
	replaceHtml: function(html) {
		var tmp = RBuilder.node('div');
		tmp.innerHTML = html.stripScripts();
		var el = tmp.down();
		this.element.replaceBy(el);
		html.evalScripts.bind(html).defer();
		this.element = el;
		riot.toolbar.selectedButton.applyHandler(false);
		if (window.riotEditCallbacks) {
			riotEditCallbacks.each(function(callback) {
				callback(el);
			});
		}
		riot.toolbar.selectedButton.applyHandler(true);
	}

});


/**
 * Button to append components to a list.
 */
riot.InsertButton = Class.create({

	initialize: function(componentList) {
		this.componentList = componentList;
		this.types = componentList.config.validTypes.select(function(config) {
			return !config.max || componentList.countComponents(config.type) < config.max;
		});
		this.componentList.element.insert({after: this.element = 
			new Element('div', {className: 'riot-button-face'})
			.wrap(new Element('div', {className: 'riot-insert-button'}))
			.observe('click', this.onclick.bindAsEventListener(this))});
	},
	
	remove: function() {
		this.element.remove();
	},

	onclick: function() {
		if (riot.activeInsertButton) {
			riot.activeInsertButton.element.removeClassName('riot-insert-button-active');
		}
		if (this.types.length == 1) {
			this.insert(this.types[0]);
			riot.toolbar.removeInspector();
		}
		else {
			this.element.addClassName('riot-insert-button-active');
			riot.activeInsertButton = this;
			this.inspector = new riot.TypeInspector(
					this.types, this.insert.bind(this));

			riot.toolbar.setInspector(this.inspector.element);
		}
	},

	insert: function(config) {		
		ComponentEditor.insertComponent(this.componentList.id, -1, config.type, 
				Object.toJSON(config.defaults), this.oninsert.bind(this));

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
		this.id = el.readAttribute('riot:contentId');
		if (window.riotEditCallbacks) {
			riotEditCallbacks.each(function(callback) {
				callback(el);
			});
		}
		riot.findContainer(el).markAsDirty();
		riot.toolbar.enablePublishButtons();
		if (this.componentList.config.max && this.componentList.componentElements.length == this.componentList.config.max) {
			this.componentList.insertOff();			
		}
	},

	onupdate: function(html) {
		var tmp = RBuilder.node('div');
		tmp.innerHTML = html.stripScripts();
		var el = tmp.down();
		this.componentElement.replaceBy(el);
		html.evalScripts.bind(html).defer();
		this.componentList.findComponentElements();
		this.componentElement = el;
		if (window.riotEditCallbacks) {
			riotEditCallbacks.each(function(callback) {
				callback(el);
			});
		}
	},
	
	changeType: function(config) {
		ComponentEditor.setType(this.id, config.type, Object.toJSON(config.defaults), this.onupdate.bind(this));
	}
});

/**
 * Inpector panel to choose a component type.
 */
riot.TypeInspector = Class.create({

	initialize: function(configs, onchange) {
		this.configs = configs;
		this.onchange = onchange;
		this.element = RBuilder.node('div', {},
			RBuilder.node('div', {className: 'riot-close-button', onclick: riot.toolbar.hideInspector.bind(riot.toolbar)}),
			RBuilder.node('div', {className: 'headline', innerHTML: '${title.typeInspector}'}),
			this.select = RBuilder.node('div', {className: 'type-inspector'})
		);
		ComponentEditor.getComponentLabels(configs.pluck('type'), this.setLabels.bind(this));
	},
	
	setLabels: function(labels) {
		for (var i = 0; i < this.configs.length; i++) {
			var e = RBuilder.node('div', {className: 'type'},
				RBuilder.node('span', {className: 'type-' + this.configs[i].type, innerHTML: labels[i]})
			);
			e.config = this.configs[i];
			var inspector = this;
			e.onclick = function() {
				if (inspector.activeTypeButton) {
					inspector.activeTypeButton.className = 'type';
				}
				this.className = 'active-type'
				inspector.activeTypeButton = this;
				inspector.onchange(this.config);
			}
			this.select.appendChild(e);
		}
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
};

riot.discardOn = function() {
	if (!riot.previewFrame.initialized) {
		var params = $H(window.location.search.parseQuery());
		params.set('org.riotfamily.components.EditModeUtils.liveMode', 'true');
		riotPreviewFrame.location.href = window.location.pathname + '?' + params.toQueryString();
		riot.previewFrame.initialized = true;
	}
	else {
		riot.toolbar.applyButton.enable();
	}
	riot.previewFrame.setStyle({
		display: 'block',
		visibility: 'visible'
	}); 
	$$('body > *:not(#riotPreviewFrame)').invoke('hide');
	var html = $$('html').first();
	if (!html.originalOverflow) {
		html.originalOverflow = html.style.overflow;
	}
	html.style.overflow = 'hidden';
	riot.applyFunction = function(containerIds) {
		ComponentEditor.discard(containerIds, function() {window.location.reload();});
	};
}

riot.discardOff = function() {
	riot.toolbar.applyButton.disable();
	riot.previewFrame.hide();
	var html = $$('html').first();
	html.style.overflow = html.originalOverflow;
	$$('body > *:not(#riotPreviewFrame)').invoke('show');
	riot.outline.hide();
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
	var containerIds = riotContainerIds || $$('.riot-container')
		.collect(riot.getContentContainer)
		.pluck('id'); 
	
	riot.applyFunction(containerIds);	
	riot.previewFrame.initialized = false;	
	riot.toolbar.disablePublishButtons();	
}

riot.editProperties = function(e) {
	e = e || this;
	var componentElement = Element.up(e, '.riot-component');
	if (componentElement && (!componentElement.component || !componentElement.component.mode)) {
		riot.toolbar.buttons.get('properties').click();
		componentElement.component.properties();
	}
	return false;
}

dwr.engine.setTextHtmlHandler(function() {
	location.reload();
});

dwr.engine.setErrorHandler(function(err, ex) {
	if (ex.javaClassName == 'org.riotfamily.riot.security.PermissionDeniedException') {
		riot.popup = new riot.Popup('${title.permissionDenied}', riot.contextPath + ex.permissionRequestUrl, null, true);
	}
});


riot.previewFrame = RBuilder.node('iframe', {name: 'riotPreviewFrame', id: 'riotPreviewFrame'}).appendTo(document.body);
riot.init();
riot.adoptFloatsAndClears();