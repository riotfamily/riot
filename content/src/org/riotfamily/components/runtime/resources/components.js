if (!window.riot) var riot = {}; // riot namespace

riot.components = (function() {
	
	/**
	 * Returns a text editor for the given element. What kind of editor is returned
	 * depends on the element's riot:editorType attribute.
	 */
	function getTextEditor(el) {
		if (!el.textEditor) {
			var editorType = el.readAttribute('riot:editorType');
			var content = findContent(el);
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
				el.textEditor = new riot.RichtextEditor(el, content, {split: true, useInnerHtmlAsDefault: true, config: el.readAttribute('riot:config')});
			}
		}
		return el.textEditor;
	}
	
	/**
	 * Returns the ComponentList for the given element.
	 */
	function getComponentList(el) {
		if (!el.componentList) {
			el.componentList = new ComponentList(el);
		}
		return el.componentList;
	}
	
	/**
	 * Searches the element's ancestors (including the element itself) for an
	 * element with the class 'riot-component-list' and returns the expando for that
	 * element.
	 */
	function findComponentList(el) {
		el = el.up('.riot-component-list');
		if (el) {
			return getComponentList(el);
		}
		return null;
	}

	function getContent(el) {
		if (!el.content) {
			var c = el.hasClassName('riot-component') ? el : el.up('.riot-component');
			if (c && c.readAttribute('riot:contentid') == el.readAttribute('riot:contentid')) {
				el.content = new Component(c);
			}
			else {
				el.content = new Content(el);
			}
		}
		return el.content;
	}
	
	/**
	 * Searches the element's ancestors (including the element itself) for an
	 * element with the class 'riot-content' and returns the expando for that
	 * element.
	 */
	function findContent(el) {
		if (el && !el.hasClassName('riot-content')) {
			el = el.up('.riot-content');
		}
		if (el) {
			return getContent(el);
		}	
	}
	
	/**
	 * Moves the float and clear style of the component's one and only child element
	 * to the the component's div.
	 */
	 function adoptFloatsAndClears(root) {
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

	
	function onHover(element, dropon, overlap) {
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

	
	// -----------------------------------------------------------------------
	
	var activeComponent = null;
	
	// -----------------------------------------------------------------------
	// ComponentList
	// -----------------------------------------------------------------------
	
	var ComponentList = Class.create({ 
		initialize: function(el) {
			this.element = el;
			this.id = el.readAttribute('riot:listId');
			if (!el.id) el.id = 'riot-list-' + this.id;
			this.config = window["riotComponentListConfig" + this.id];
			this.findComponentElements();
		},
		
		findComponentElements: function() {
			this.componentElements = this.element.childElements().findAll(function(el) { return el.hasClassName('riot-component'); });
		},
			
		insertOn: function() {
			if (!this.config.max || this.componentElements.length < this.config.max) {
				this.element.addClassName('riot-mode-insert').disableLinks();
				this.insertButton = new InsertButton(this);
			}
			if (this.componentElements.length == 0) {
				this.element.addClassName('riot-empty-list');
			}
		},
		
		insertOff: function() {
			if (this.insertButton) {
				this.element.removeClassName('riot-mode-insert')
					.removeClassName('riot-empty-list')
					.enableLinks();
				
				this.insertButton.remove();
				this.insertButton = undefined;
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
							drop.onHover = onHover;
						}
					});
				});
										
				Draggables.addObserver(new ComponentDragObserver(this));
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
						// Set "every-nth-remainder-x" or "not-every-nth-remainder-x" class
						var modExp = /(^|\s|-)(not-)?every-(\d+)(nd|rd|th)-remainder-(\d+)(-|\s|$)/;
						var match = desc.className.match(modExp);
						if (match) {
							var nth = match[3];
							var remainder = match[5];
							var every = ((i + 1) % nth == remainder) ? 'every' : 'not-every';
							desc.className = desc.className.replace(modExp, '$1' + every + '-$3$4-remainder-$5$6');
						}
						else {
							// Set "every-nth" or "not-every-nth" class
							modExp = /(^|\s|-)(not-)?every-(\d+)(nd|rd|th)(-|\s|$)/;
							match = desc.className.match(modExp);
							if (match) {
								var nth = match[3];
								var every = ((i + 1) % nth == 0) ? 'every' : 'not-every';
								desc.className = desc.className.replace(modExp, '$1' + every + '-$3$4$5');
							}
						}
					}
				});
			}
			adoptFloatsAndClears(this.element);
		},
		
		removeOn: function() {
			this.element.addClassName('riot-mode-remove');
			if (this.componentElements.length > this.config.min) {
				var list = this;
				var handler = list.removeComponent.bind(list);
				this.componentElements.map(getContent).each(function(c) {
					var cfg = this.getComponentConfig(c.type);
					var min = cfg ? cfg.min : 0;
					if (min == 0 || this.countComponents(c.type) > min) {
						c.setClickHandler(handler);
					}
				}, this);
			}
		},
		
		removeOff: function() {
			this.element.removeClassName('riot-mode-remove');
			this.componentElements.each(function(el) {
				var c = getContent(el);
				if (c) c.removeClickHandler();
			});
		},
		
		removeComponent: function(c) {
			ComponentEditor.deleteComponent(c.id);
			riot.toolbar.enablePreviewButton();
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
			return this.componentElements.map(getContent).select(function(c) {return c.type == type}).length;
		}
		
	});
	
	// -----------------------------------------------------------------------
	// Content
	// -----------------------------------------------------------------------
	
	Content = Class.create({ 
		initialize: function(el, id) {
			this.element = el;
			this.id = id || el.readAttribute('riot:contentId');
			this.form = el.readAttribute('riot:form');
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
				riot.outline.hide();
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
			Event.observe(this.targetElement, 'mouseenter', this.bShowOutline);
			Event.observe(this.targetElement, 'mouseleave', this.bHideOutline);
		},
		
		onClick: function(ev) {
			Event.stop(ev);
			this.clickHandler(this);
		},
		
		removeClickHandler: function() {
			if (this.targetElement) {
				Event.stopObserving(this.targetElement, 'click', this.bOnClick);
				Event.stopObserving(this.targetElement, 'mouseenter', this.bShowOutline);
				Event.stopObserving(this.targetElement, 'mouseleave', this.bHideOutline);
			}
			this.clickHandler = null;
			this.element.enableLinks();
		},
		
		retrieveText: function(key, callback) {
			ComponentEditor.getText(this.id, key, callback);
		},
		
		updateText: function(key, value, updateFromServer) {
			riot.toolbar.enablePreviewButton();
			ComponentEditor.updateText(this.id, key, value,	updateFromServer 
					? this.update.bind(this) : Prototype.emptyFunction);
		},
			
		propertiesOn: function() {
			this.element.up().addClassName('riot-mode-properties');
			this.setClickHandler(this.editProperties.bind(this));
		},
		
		propertiesOff: function() {
			this.element.up().removeClassName('riot-mode-properties');
			this.removeClickHandler();
		},
		
		editProperties: function() {
			if (activeComponent) {
				return;
			}
			var formUrl = riot.path + '/components/form/' + this.id + '/' + this.form + '?' 
					+ $H(riotComponentFormParams).toQueryString();

			activeComponent = this;
			this.dialog = new riot.window.Dialog({
				title: '${title.properties}', 
				url: formUrl,
				closeButton: true,
				autoSize: false,
				onClose: function() { activeComponent = null }
			});
		},
		
		propertiesChanged: function() {
			this.dialog.close();
			// Timeout as we otherwise get an 0x8004005 [nsIXMLHttpRequest.open] error
			// in Firefox 2.0. See https://bugzilla.mozilla.org/show_bug.cgi?id=249843
			setTimeout(function() {
				riot.toolbar.enablePreviewButton()
				this.update();
			}.bind(this), 1);
		},
		
		update: function() {
			window.location.reload();		
		}
	});

	// -----------------------------------------------------------------------
	// Component
	// -----------------------------------------------------------------------

	Component = Class.create(Content, {

		initialize: function($super, el) {
			$super(el);
			this.type = el.readAttribute('riot:componentType');
		},
		
		updateTextChunks: function(key, chunks) {
			ComponentEditor.updateTextChunks(this.id, key, chunks, this.renderChunks.bind(this));
			riot.toolbar.enablePreviewButton();
		},
		
		renderChunks: function(chunks) {
			for (var i = chunks.length - 1; i > 0 ; i--) {
				this.element.insert({after: chunks[i]});
			}
			this.replaceHtml(chunks[0]);
		},
		
		update: function() {
			ComponentEditor.renderComponent(this.id, this.replaceHtml.bind(this));
		},
		
		replaceHtml: function(html) {
			var tmp = new Element('div').update(html.stripScripts());
			var el = tmp.down();
			this.element.replaceBy(el);
			html.evalScripts.bind(html).defer();
			this.element = el;
			riot.toolbar.selectedButton.applyHandler(false);
			el.fire('component:updated');
			riot.toolbar.selectedButton.applyHandler(true);
		}
	});
	
	// -----------------------------------------------------------------------
	// InsertButton
	// -----------------------------------------------------------------------

	InsertButton = Class.create({

		initialize: function(componentList) {
			this.componentList = componentList;
			this.index = 0;
			
			// Filter types that exceed the max occurrence
			this.types = componentList.config.validTypes.select(function(config) {
				return !config.max || componentList.countComponents(config.type) < config.max;
			});
			
			this.element = new Element('div').setStyle({position: 'absolute'})
					.insert(new Element('div').addClassName('riot-insert-button')
					.observe('click', this.onclick.bindAsEventListener(this)));

			$(document.body).insert(this.element);
			
			var dest = this.componentList.element.down('.riot-component') || this.componentList.element;
			this.move(null, dest);
			
			var button = this;
			this.moveHandler = function(ev) {
				button.move(ev, this);
			};
			
			this.trackMouse();
		},
		
		trackMouse: function() {
			this.componentList.componentElements.invoke('observe', 'mousemove', this.moveHandler);			
		},
		
		stopMouseTracking: function() {
			this.componentList.componentElements.invoke('stopObserving', 'mousemove', this.moveHandler);
		},
		
		move: function(ev, el) {
			this.index = Math.max(0, this.componentList.componentElements.indexOf(el));
			var offset = this.componentList.config;
			var pos = el.cumulativeOffset();
			var left = pos.left;
			var top = pos.top;
			if (el.getStyle('float') != 'none') {
				if (ev && ev.pointerX() > left + el.getWidth() / 2) {
					left += el.getWidth() - offset.x;
					this.index++;
				}
				else {
					left += offset.x;
				}
				top += offset.y;
			}
			else {
				if (ev && ev.pointerY() > top + el.getHeight() / 2) {
					top += el.getHeight() - offset.y;
					this.index++;
				}
				else {
					top += offset.y;
				}
				left += offset.x;
			}
			this.element.setStyle({left: left + 'px', top: top + 'px'});
		},
		
		remove: function() {
			this.stopMouseTracking();
			this.element.remove();
			if (this.menu) {
				this.menu.element.remove();
			}
		},

		onclick: function() {
			if (this.types.length == 1) {
				// Only one option - insert instantly
				this.insert(this.types[0].type);
			}
			else {
				// Show type menu
				this.stopMouseTracking();
				if (!this.menu) {
					this.menu = new TypeMenu(this);
				}
				else {
					this.menu.show();
				}
			}
		},

		insert: function(type) {		
			ComponentEditor.insertComponent(this.componentList.id, this.index, 
					type, this.insertHtml.bind(this));
		},

		insertHtml: function(html) {
			var tmp = new Element('div').update(html);
			var el = this.componentElement = tmp.down();
			el.hide();
			if (this.index > 0) {
				if (this.index == this.componentList.componentElements.length) {
					this.componentList.element.insert({bottom: el});
				}
				else {
					this.componentList.componentElements[this.index].insert({before: el});
				}
			}
			else {
				this.componentList.element.insert({top: el});
			}
			
			this.componentList.findComponentElements();
			this.componentList.element.removeClassName('riot-empty-list');
			this.componentList.updatePositionClasses();
			this.id = el.readAttribute('riot:contentId');
			el.fire('component:updated');
			riot.toolbar.enablePreviewButton();
			new Effect.BlindDown(el, {
				duration: 0.5, 
				afterFinish: this.afterInsert.bind(this)
			});
		},
		
		afterInsert: function() {
			riot.toolbar.enablePreviewButton();
			riot.toolbar.selectedButton.applyHandler(false);
			riot.toolbar.selectedButton.applyHandler(true);
		}
	});

	// -----------------------------------------------------------------------
	// TypeMenu
	// -----------------------------------------------------------------------
	
	TypeMenu = Class.create({

		initialize: function(button) {
			this.button = button;	
			this.element = new Element('div').addClassName('riot-types').hide()
				.insert(new Element('div').addClassName('riot-close-button').hide().observe('click', this.close.bind(this)))
				.insert(new Element('div').addClassName('riot-type-list'));
			
			ComponentEditor.getComponentMetaData(button.types.pluck('type'), 
					this.setMetaData.bind(this));
			
			button.element.insert(this.element);
		},
		
		setMetaData: function(meta) {
			var menu = this;
			meta.each(function(m) {
				var icon = riot.resourcePath + 'style/images/icons/' + (m.icon || 'plugin') + '.png';
				var e = new Element('a', {href: 'javascript://'}).addClassName('type')
					.insert(new Element('span', {
						style: 'background-image:url(' + icon 
						+ ');_background-image:none;_filter:progid:'
						+ 'DXImageTransform.Microsoft.AlphaImageLoader(src=\''
						+ icon + '\', sizingMethod=\'crop\')'
					}).addClassName('icon')).insert(m.name);

				e.type = m.type;
				e.onclick = function() {
					menu.element.select('.active').invoke('removeClassName', 'active');
					this.addClassName('active');
					menu.button.insert(this.type);
					menu.element.hide();
				}
				menu.element.down('.riot-type-list').insert(e);	
			});
			this.show();
		},
		
		show: function() {
			new Effect.SlideDown(this.element, {
				duration: 0.2, 
				scaleX: true, 
				afterFinish: function(e) {
					if (Prototype.Browser.IE && typeof document.documentElement.style.maxHeight == 'undefined') {
						e.element.down().show();
					}
					else {
						new Effect.Appear(e.element.down(), {duration: 0.2});
					}
				}
			});
		},
		
		close: function() {
			this.element.hide().down().hide();
			this.button.trackMouse();
		}
		
	});
	
	// -----------------------------------------------------------------------
	// ComponentDragObserver
	// -----------------------------------------------------------------------
	
	ComponentDragObserver = Class.create({
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
			this.prevEl = draggable.element.previous('.riot-component');
		},
		
		onEnd: function(eventName, draggable, event) {
			var el = draggable.element;
			el.removeClassName('riot-drag');
			var prevEl = el.previous('.riot-component');
			if(el.parentNode == this.element && prevEl != this.prevEl) {
				this.componentList.findComponentElements();
				var prevId = null;
				if (prevEl) {
					prevId = getContent(prevEl).id;
					prevEl.forceRerendering();
				}
				var component = getContent(el);
				ComponentEditor.moveComponent(component.id, prevId);
				riot.toolbar.enablePreviewButton();
			}
			this.componentList.updatePositionClasses();
			this.prevEl = null;
		}
	});
	
	// -----------------------------------------------------------------------
	// Initialization
	// -----------------------------------------------------------------------
	
	var containersToPublish = [];
	
	dwr.engine.setTextHtmlHandler(function() {
		location.reload();
	});

	dwr.engine.setErrorHandler(function(err, ex) {
		if (ex.javaClassName == 'org.riotfamily.core.security.policy.PermissionDeniedException') {
			new riot.window.Dialog({
				url: riot.contextPath + ex.permissionRequestUrl,
				minHeight : 255,
				closeButton: true,
				autoSize: true
			});
		}
	});
	
	dwr.engine.setWarningHandler(function(err, ex) {
	    if (window.console && console.log) {
	        console.log(err);
	    }
	});
	
	// Make lists sortable after moveOn() has been invoked. This has to be
	// done in reverse order to support nested sortable lists.
	riot.toolbar.buttons.get('move').afterApply = function(enable) {
		if (enable) {
			$$('.riot-component-list').reverse().each(function(el) {
				getComponentList(el).makeSortable();
			});
		}
	};
	
	var previewFrame = new Element('iframe', {
		name: 'riotPreviewFrame', 
		id: 'riotPreviewFrame',
		src: riot.path + '/components/publish'
	});
	document.body.appendChild(previewFrame);
	adoptFloatsAndClears();
	
	function setState(state) {
		state.containerIds.each(function(id) {containersToPublish.push(id)});
		riot.toolbar.setState(state);
	}
	
	// =======================================================================
	// Public API
	// =======================================================================
	
	return {

		/**
		 * Returns a wrapper for the given element. A wrapper is either a ComponentList, 
		 * Component or an editor. What kind of wrapper is returned depends on the 
		 * given CSS selector. The function is invoked by 
		 * ToolbarButton.getHandlerTargets() when a button's handler is to be applied.
		 */
		 getWrapper: function(el, selector) {
			if (selector == '.riot-component-list') {
				return getComponentList(el);
			}
			if (selector == '.riot-component' || selector == '.riot-form') {
				return getContent(el);
			}
			if (selector == '.riot-text-editor') {
				return getTextEditor(el);
			}
			return null;
		},
		
		browseOn: function() {
			document.fire('components:browse');
		},
		
		browseOff: function() {
			document.fire('components:edit');
		},
		
		previewOn: function() {
			var h = Math.max(document.viewport.getHeight(), Element.getHeight(document.body));
			
			var html = $$('html').first();
			if (!html.originalOverflow) {
				html.originalOverflow = html.style.overflow;
			}
			html.style.overflow = 'hidden';

			var overlay = new Element('div', {id: 'riotLoadingOverlay'});
			overlay.style.height = h + 'px';
			document.body.appendChild(overlay);
			
			previewFrame.setStyle({display: 'block'});
			
			riotPreviewFrame.init(containersToPublish);
		},
		
		logoutOn: function() {
			ComponentEditor.logout(function() {
				location.reload();
			});
		},
		
		showPreviewFrame: function() {
			previewFrame.setStyle({visibility: 'visible'}); 
			$$('body > *:not(#riotPreviewFrame)').invoke('hide');
		},
		
		publish: function() {
			ComponentEditor.publish(containersToPublish);
			previewFrame.initialized = false;
			riot.toolbar.disablePreviewButton();
		},
		
		discard: function() {
			ComponentEditor.discard(containersToPublish, function() {
				window.location.reload();
			});
		},
		
		hidePreviewFrame: function() {
			previewFrame.setStyle({visibility: 'hidden', display: 'none'});
			$('riotLoadingOverlay').remove();
			var html = $$('html').first();
			html.style.overflow = html.originalOverflow;
			$$('body > *:not(#riotPreviewFrame)').invoke('show');
			riot.outline.hide();
			riot.toolbar.buttons.get('browse').click();
		},
		
		propertiesChanged: function() {
			activeComponent.propertiesChanged();
		},
		
		registerContainer: function(id) {
			containersToPublish.push(id);
		},
		
		init: function() {
			var containerIds = $$('a.riot-container').invoke('readAttribute', 'rel');
			ComponentEditor.getState(containerIds, setState);
		},
		
		editProperties: function(e) {
			var ce = Element.up(e, '.riot-component');
			if (ce && (!ce.content || !ce.content.mode)) {
				riot.toolbar.buttons.get('properties').click();
				getContent(ce).editProperties();
			}
			return false;
		}
	}
	
})();

