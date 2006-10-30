riot.InplaceEditor = function() {};
riot.InplaceEditor.prototype = {

	initialize: function(element, component, options) {
		this.element = element;
		this.component = component;
		this.key = element.getAttribute('riot:key');
		this.enabled = false;
		this.onclickHandler = this.onclick.bindAsEventListener(this);
		this.oninit(options);
	},
	
	/* Subclasses may override this method to perform initalization upon creation */
	oninit: function(options) {
		this.options = options || {};
	},
	
	/* Enables or disables the editor by adding (or removing) an onclick listener */
	setEnabled: function(enabled) {
		this.enabled = enabled;
		if (enabled) {
			Event.observe(this.element, 'click', this.onclickHandler, true);
			Element.addClassName(this.element, 'riot-editable-text');
		}
		else {
			if (riot.activeEditor == this) {
				this.close();
			}
			Event.stopObserving(this.element, 'click', this.onclickHandler, true);
			Element.removeClassName(this.element, 'riot-editable-text');
		}
	},
	
	/* Handler that is invoked when an enabled editor is clicked */
	onclick: function(ev) {
		Event.stop(ev);
		riot.toolbar.selectedComponent = this.component;
		riot.activeEditor = this;
		this.edit();
	},
	
	/* Acquires the current text and invokes setText() */
	edit: function() {
		this.setText(this.element.innerHTML);
	},

	/* Stores the given text as property and invokes showEditor() */	
	setText: function(text) {
		this.text = text;
		this.showEditor();
	},
	
	/* Subclasses must implement this method to show a widget that edits 
	 * the current text. 
	 */
	showEditor: function() {
	},
	
	/* Subclasses must implement this method to return the edited text. */
	getText: function() {
		return null;
	},
			
	save: function() {
		var text = this.getText();
		if (this.text != text) {
			ComponentEditor.updateText(this.component.componentList.controllerId,
					this.component.id, this.key, text, this.onupdate.bind(this));
					
			riot.toolbar.setDirty(this.component.componentList, true);
		}
		this.onsave(text);
	},
	
	/* Subclasses may override this method ... */
	onsave: function(text) {
	},
	
	/* Callback that is invoked after the text as been sucessfully submitted. */
	onupdate: function(html) {
	},
	
	suspend: function() {
	},
	
	resume: function() {
	},
	
	/* This method is invoked when the active editor is disabled (either by
	 * enabling another editor or by switching to another tool).
	 * The default behaviour is to save all changes. Subclasses that provide
	 * an explicit save button (like the TextileEditor) may override this 
	 * method.
	 */
	close: function() {
		this.save();
	}
}

riot.InplaceTextEditor = Class.extend(riot.InplaceEditor, {

	oninit: function(options) {
		this.options = options || {};
		Element.makePositioned(this.element);
		this.input = document.createElement(this.options.multiline 
				? 'textarea' : 'input');
				
		if (this.options.multiline) this.input.wrap = 'off';
		
		Element.setStyle(this.input, {
			position: 'absolute', overflow: 'hidden',
			top: 0,	left: 0, border: 0, padding: 0, margin: 0,
			backgroundColor: 'transparent'
		});
		
		this.input.onkeyup = this.updateElement.bindAsEventListener(this);
		this.input.onblur = this.save.bindAsEventListener(this);
	
		Element.cloneStyles(this.element, this.input, [
			'font-size', 'font-weight', 'font-family', 'font-style', 
			'color', 'background-color', 'text-align', 'text-decoration', 
			'letter-spacing', 'padding-left', 'padding-top']);
		
		if (this.options.multiline) {
			Element.cloneStyles(this.element, this.input, ['line-height']);
		}
		
		this.input.style.boxSizing = this.input.style.MozBoxSizing = 'border-box';
		
		Element.hide(this.input);
		Element.insertAfter(this.input, this.element);
	},
		
	edit: function() {
		this.setText(this.element.innerHTML.trim()
			.replace(/\s+/g, ' ')
			.replace(/<br[^>]*>/gi, '\n')
			.stripTags()
			.replace(/&amp;/g, '&')
			.replace(/&lt;/g, '<')
			.replace(/&gt;/g, '>')
		);
	},
		
	showEditor: function() {
		this.paddingLeft = 0;
		this.paddingTop = 0;
		if (browserInfo.ie || browserInfo.opera) { 
			var cm = document['compatMode'];
			if (cm != 'BackCompat' && cm != 'QuirksMode') {
				this.paddingLeft = parseInt(Element.getStyle(this.element, 'padding-left'));
				this.paddingTop = parseInt(Element.getStyle(this.element, 'padding-top'));
			}
		}
		Position.clone(this.element, this.input, {
			setWidth: false,
			setHeight: false
		});
		this.resize();
		Element.invisible(this.element);
		Element.show(this.input);
		this.input.focus();
		this.input.value = this.text;
	},
	
	getText: function() {
		return this.input.value
			.replace(/</g, '&lt;')
			.replace(/>/g, '&gt;')
			.replace(/&/g, '&amp;')
			.replace(/\n/g, '<br />');
	},
		
	onsave: function(text) {
		this.element.innerHTML = text;
		Element.hide(this.input);
		Element.visible(this.element);
	},

	updateElement: function() {
		this.element.innerHTML = this.getText().replace(/<br[^>]*>/gi, '<br />&nbsp;');
		this.resize();
	},

	resize: function() {
		this.input.style.width  = (this.element.offsetWidth - this.paddingLeft) + 'px';
    	this.input.style.height = (this.element.offsetHeight - this.paddingTop) + 'px';
	}
});


riot.PopupTextEditor = Class.extend(riot.InplaceEditor, {

	edit: function() {
		ComponentEditor.getText(this.component.id, this.key, 
			this.setText.bind(this));
	},
		
	showEditor: function() {
		this.popup = new riot.TextareaPopup(this);
		this.popup.open();
	},
	
	close: function() {
		this.popup.close();
		riot.activeEditor = null;
	},
	
	getText: function() {
		return this.popup.getText();
	},
	
	onsave: function() {
		this.close();
	},
	
	onupdate: function(html) {
		this.component.setHtml(html);
	},
	
	suspend: function(message) {
		this.popup.suspend(message);
	},
	
	resume: function() {
		this.popup.resume();
	}
	
});

riot.RichtextEditor = Class.extend(riot.PopupTextEditor, {
	showEditor: function() {
		Resources.loadScriptSequence([
			{src: 'tiny_mce/tiny_mce.js', test: 'tinyMCE'},
			{src: 'tiny_mce/strict_mode_fix.js', test: 'tinyMCE.strictModeFixed'}
		]);
		Resources.waitFor('tinyMCE.strictModeFixed', this.openPopup.bind(this));
	},
	
	openPopup: function() {
		this.popup = new riot.TinyMCEPopup(this);
		this.popup.open();
	},
	
	save: function() {
		if (this.options.split) {
			var text = this.getText();
			if (this.text != text) {
				var chunks = [];
				var n = Element.create('div');
				n.innerHTML = text;
				$A(n.childNodes).each(function(c) {
					if (c.nodeType == 1) {
						chunks.push('<' + c.nodeName + '>' + c.innerHTML 
								+ '</' + c.nodeName + '>');
					}
				});
				
				ComponentEditor.updateTextChunks(
						this.component.componentList.controllerId,
						this.component.id, this.key, chunks, 
						this.component.onupdate);
			}
			this.onsave(text);
		}
		else {
			this.superclass.save();
		}
	}

});

riot.TextileEditor = Class.extend(riot.PopupTextEditor, {
	help: function() {
		var win = window.open(Resources.resolveUrl('help/textile/toc.html'), 'textile_toc', 
				'width=250,height=550,top=10,left=20,scrollbars=yes');
				
		win.focus();
	}
});

riot.MarkdownEditor = Class.extend(riot.PopupTextEditor, {
	help: function() {
		var win = window.open(Resources.resolveUrl('help/markdown/help.html'), 'markdown_help', 
				'width=250,height=550,top=10,left=20,scrollbars=yes');
				
		win.focus();
	}
});


riot.TextareaPopup = Class.create();
riot.TextareaPopup.prototype = {

	initialize: function(editor) {
		this.div = Element.create('div', {className: 'riot-popup riot-editor-popup'},
			editor.help ? Element.create('div', {className: 'riot-help-button', onclick: editor.help}) : null, 
			this.closeButton = Element.create('div', {className: 'riot-close-button', onclick: this.close.bind(this)}), 
			Element.create('div', {className: 'headline'}, '${editor-popup.title}'), 
			this.textarea = Element.create('textarea', {value: editor.text || ''}), 
			this.okButton = Element.create('div', {className: 'button-ok', onclick: editor.save.bind(editor)}, 'Ok')
		);
		Element.invisible(this.div);
		document.body.appendChild(this.div);
	},
	
	open: function() {
		riot.toolbar.showDialog(this.div);
	},
	
	close: function() {
		riot.toolbar.closeDialog();
	},
	
	suspend: function(message) {
		this.suspended = Element.create('div', {className: 'suspended'}, message);
		Element.hide(this.okButton);
		Element.hide(this.closeButton);
		this.div.appendChild(this.suspended);
	},
	
	resume: function() {
		Element.remove(this.suspended);
		Element.show(this.okButton);
		Element.show(this.closeButton);
	},
	
	setText: function(text) {
		this.textarea.value = text;
		this.textarea.focus();
	},
	
	getText: function() {
		return this.textarea.value;
	}
	
}

riot.TinyMCEPopup = Class.extend(riot.TextareaPopup, {
	initialize: function(editor) {
		this.superclass.initialize(editor);
		if (this.textarea.value == '') {
			this.textarea.value = '<p>&nbsp;</p>';
		}
		Element.invisible(this.textarea);
		riot.initTinyMCE();
		Resources.waitFor('tinyMCELang["lang_theme_block"]', 
				this.addMCEControl.bind(this));
	},
	
	addMCEControl: function() {
		tinyMCE.addMCEControl(this.textarea);
	},

	close: function() {
		tinyMCE.instances = tinyMCE.instances.without(tinyMCE.selectedInstance);
		this.superclass.close();
	},
	
	setText: function(text) {
		tinyMCE.setContent(text);
	},
	
	getText: function() {
		return tinyMCE.getContent();
	}
	
});

riot.stylesheetMaker = {

	properties: {
		'*': ['font-family', 'font-size', 'font-weight', 'font-style',
			'line-height', 'text-decoration', 'color', 'background-color',
			'margin-top', 'margin-right', 'margin-bottom', 'margin-left',
			'padding-top', 'padding-right', 'padding-bottom', 'padding-left'],
		'a': ['border-bottom'],
		'hr': ['width', 'height'],
		'ul li': ['list-style-type', 'list-style-position', 'list-style-image',
			'background-image', 'background-position', 'background-repeat']
	},
			
	selectors: ['body', 'p', 'a', 'strong', 'em', 'h1', 'h2', 
		'h3', 'h4', 'hr', 'ul', 'ul li', 'ol', 'ol li'],
		
	addRule: function(selector, styles, sheet) {
		var css = '';
		for (prop in styles) {
			if (typeof(prop) == 'function') { continue; }
			css += prop + ':' + styles[prop] 
			if (selector == 'a' && (prop == 'color' || prop == 'text-decoration')) {
				css += ' !important';
			}
			css += ';'
		}		
		if (sheet.insertRule) {
			var rule = selector + ' {' + css + '}';
			sheet.insertRule(rule, sheet.cssRules.length);
		}
		else if (sheet.addRule) {
			sheet.addRule(selector, css);
		}
	},

	getStyles: function(el, props) {
		var result = {};
		for (var i = 0; i < props.length; i++) {
			result[props[i]] = Element.getStyle(el, props[i]);
		}
		return result;
	},

	copyStyles: function(el, doc, classes) {
		var sheet = doc.styleSheets[doc.styleSheets.length - 1];	
		for (var i = 0; i < this.selectors.length; i++) {
			var selector = this.selectors[i];
			var p = el; 
			var names = selector.split(/\s/);
			for (var n = 0; n < names.length; n++) {
				if (names[n] != 'body') {
					var e = document.createElement(names[n]);
					if (names[n] == 'a') e.href = '#';
					p.appendChild(e);
					p = e;
				}
			}
			
			var styles = this.getStyles(p, this.properties['*']);
			var props = this.properties[selector];
			if (isDefined(props)) {
				Object.extend(styles, this.getStyles(p, props));
			}
			if (selector == 'body') {
				styles['background-color'] = Element.getBackgroundColor(p);
			}
			this.addRule(selector, styles, sheet);
		}
		if (classes) {
			for (var i = 0; i < classes.length; i++) {
				var e = document.createElement('span');
				e.className = classes[i];
				el.appendChild(e);
				var styles = this.getStyles(e, this.properties['*']);
				this.addRule('.' + classes[i], styles, sheet);
			}
		}
	}
}

riot.setupTinyMCEContent = function(editorId, body, doc) {
	var style = doc.createElement('style');
	style.type = 'text/css';
	var head = doc.getElementsByTagName('head')[0];
	head.appendChild(style);
		
	var e = riot.activeEditor.element;
	var clone = e.cloneNode(false);
	Element.hide(clone);
	Element.insertBefore(clone, e);
	riot.stylesheetMaker.copyStyles(clone, doc, riot.tinyMCEStyles);
	Element.remove(clone);
	
	body.style.paddingLeft = '5px';
	
	// Add a print margin ...

	var bg = Element.getBackgroundColor(e).parseColor();
	var brightness = 0;
	$R(0,2).each(function(i) { brightness += parseInt(bg.slice(i*2+1,i*2+3), 16) });
	brightness /= 3;
	var bgImage = brightness > 227 ? 'margin.gif' : 'margin_hi.gif';

	var editorWidth = riot.activeEditor.width;
	var componentWidth = riot.activeEditor.component.element.offsetWidth;
	var margin = editorWidth - componentWidth;
	if (margin > 0) {
		body.style.paddingRight = margin + 'px';
		body.style.backgroundImage = 'url(' + Resources.resolveUrl(bgImage) + ')';
		body.style.backgroundRepeat = 'repeat-y';
		body.style.backgroundPosition = componentWidth + 'px';
		body.style.backgroundAttachment = 'fixed';
	}
}

riot.initTinyMCEInstance = function(inst) {
	//Reset -5px margin set by TinyMCE in strict_loading_mode
	riot.activeEditor.width = inst.iframeElement.offsetWidth;
	inst.iframeElement.style.marginBottom = '0';
}

riot.initTinyMCE = function() {
	if (!riot.tinyMCEInitialized) {
		tinyMCE.init(riot.tinyMCEConfig);
		riot.tinyMCEInitialized = true;
	}
}

riot.tinyMCEConfig = {
	mode: 'none',
	add_unload_trigger: false,
	strict_loading_mode: true,
	setupcontent_callback: 'riot.setupTinyMCEContent',
	init_instance_callback: 'riot.initTinyMCEInstance',
	relative_urls: false,
	theme: 'advanced',
	theme_advanced_layout_manager: 'RowLayout',
	theme_advanced_containers_default_align: 'left',
	theme_advanced_containers: 'buttons1, mceEditor, mceStatusbar',
	theme_advanced_container_buttons1: 'formatselect,bold,italic,sup,bullist,numlist,outdent,indent,hr,link,unlink,anchor,code,undo,redo,charmap',
	theme_advanced_blockformats: 'p,h3,h4',
	valid_elements: '+a[href|target|name],-strong/b,-em/i,h3/h2/h1,h4/h5/h6,p,br,hr,ul,ol,li,blockquote,sub,sup,span[class<mailto]'
}
	
ComponentEditor.getEditorConfigs(function(configs) {
	if (configs && configs.tinyMCE) {
		Object.extend(riot.tinyMCEConfig, configs.tinyMCE);
		var styles = riot.tinyMCEConfig.theme_advanced_styles;
		if (styles) {
			riot.tinyMCEStyles = styles.split(';').collect(function(pair) {
		      return pair.split('=')[1];
		    });
		}
	}
});