riot.stopEvent = function(ev) {
	Event.stop(ev || window.event);
	return false;
}

riot.outline = {
	show: function(el, onclick, excludes) {
		riot.outline.cancelHide();
		if (riot.outline.suspended) return;
		riot.outline.element.hide().copyPosFrom(el, {offsetWidth: -4, offsetHeight: -4}).update().show().onclick = onclick;
		if (excludes) {
			excludes.each(function(e) {
				RBuilder.node('div')
					.setStyle({position: 'absolute', display: 'none'})
					.appendTo(riot.outline.element).copyPosFrom(e).show()
					.onmouseover = riot.outline.hide;
			});
		}
	},

	hide: function(ev) {
		riot.outline.element.hide().onclick = null;
	},

	scheduleHide: function(ev) {
		if (!ev || riot.outline.element != (ev.toElement || ev.relatedTarget)) {
			riot.outline.timeout = setTimeout(riot.outline.hide, 250);
		}
	},

	cancelHide: function() {
		if (riot.outline.timeout) {
			clearTimeout(riot.outline.timeout);
			riot.outline.timeout = null;
		}
	}
}
riot.outline.element = RBuilder.node('div', {
		className: 'riot-highlight',
		onmouseover: riot.outline.cancelHide,
		onmouseout: riot.outline.scheduleHide
	}).hide().appendTo(document.body)

riot.InplaceEditor = Class.create();
riot.InplaceEditor.prototype = {

	initialize: function(element, component, options) {
		this.element = $(element);
		this.component = component;
		this.key = this.element.readAttribute('riot:key');
		this.enabled = false;
		this.onclickHandler = this.onclick.bindAsEventListener(this);
		this.oninit(options);
		this.onMouseOver = this.showOutline.bindAsEventListener(this);
	},

	/* Subclasses may override this method to perform initalization upon creation */
	oninit: function(options) {
		this.options = options || {};
	},

	/* Enables or disables the editor by adding (or removing) an onclick listener */
	setEnabled: function(enabled) {
		this.enabled = enabled;
		if (enabled) {
			this.originalOnclickHandler = this.element.onclick;
			this.element.onclick = this.onclickHandler;
			this.element.addClassName('riot-editable-text');
			this.element.observe('mouseover', this.onMouseOver);
			this.element.observe('mouseout', riot.outline.scheduleHide);
		}
		else {
			if (riot.activeEditor == this) {
				this.close();
			}
			this.element.onclick = this.originalOnclickHandler;
			this.element.removeClassName('riot-editable-text');
			this.element.stopObserving('mouseover', this.onMouseOver);
			this.element.stopObserving('mouseout', riot.outline.scheduleHide);
		}
	},

	showOutline: function(event) {
		var excludes;
		if (!Prototype.Browser.IE) {
			excludes = [];
			this.element.previousSiblings().each(function(s) {
				if (s.getStyle('float') == 'right') {
					excludes = excludes.concat(s.getElementsByClassName('riot-editable-text'));
				}
			});
		}
		riot.outline.show(this.element, this.onclickHandler, excludes);
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
			this.component.updateText(this.key, text);
		}
		this.onsave(text);
	},

	/* Subclasses may override this method ... */
	onsave: function(text) {
	},

	/* This method is invoked when the active editor is disabled (either by
	 * enabling another editor or by switching to another tool).
	 * The default behaviour is to save all changes. Subclasses that provide
	 * an explicit save button (like the PopupTextEditor) may override this
	 * method.
	 */
	close: function() {
		if (riot.activeEditor == this) {
			riot.activeEditor = null;
		}
		this.save();
		this.onclose();
	},

	/* Callback that is invoked after the editor has been closed. */
	onclose: function() {
		if (this.element.onedit) this.element.onedit();
	}
}

riot.InplaceTextEditor = Class.extend(riot.InplaceEditor, {

	oninit: function(options) {
		this.options = options || {};
		this.inline = this.element.getStyle('display') == 'inline';
		this.input = this.inline ? RBuilder.node('input', {type: 'text'})
				: RBuilder.node('textarea', {wrap: 'off'});

		this.input.setStyle({
			position: 'absolute', overflow: 'hidden',
			top: 0,	left: 0, border: 0, padding: 0, margin: 0,
			backgroundColor: 'transparent'
		});

		this.input.onkeyup = this.updateElement.bindAsEventListener(this);
		this.input.onblur = this.close.bindAsEventListener(this);
		this.input.cloneStyle(this.element, [
			'font-size', 'font-weight', 'font-family', 'font-style',
			'color', 'background-color', 'text-align', 'text-decoration',
			'letter-spacing', 'line-height', 'padding-left', 'padding-top',
			'text-transform']);

		this.input.style.boxSizing = this.input.style.MozBoxSizing = 'border-box';
		this.input.hide();
	},

	edit: function() {
		this.setText(this.element.innerHTML.strip()
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
		if (Prototype.Browser.IE || Prototype.Browser.Opera) {
			var cm = document['compatMode'];
			if (cm != 'BackCompat' && cm != 'QuirksMode') {
				this.paddingLeft = parseInt(this.element.getStyle('padding-left'));
				this.paddingTop = parseInt(this.element.getStyle('padding-top'));
			}
		}
		document.body.appendChild(this.input);
		this.resize();
		this.element.makeInvisible();
		riot.outline.hide();
		this.input.show();
		this.input.focus();
		this.input.value = this.text;
		this.updateElement();
	},

	close: function() {
		this.SUPER();
		this.input.remove();
		this.lastText = null;
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
		this.input.hide();
		this.element.makeVisible();
	},

	updateElement: function() {
		var text = this.getText();
		if (!this.lastText || this.lastText != text) {
			this.lastText = text;
			var html = text.replace(/<br[^>]*>/gi, '<br />&nbsp;');
			if (this.inline) {
				html = html.replace(/\s/gi, '&nbsp;') + 'W'; // ... should be the widest character
			}
			this.element.update(html);
			this.resize();
		}
	},

	resize: function() {
		Position.clone(this.element, this.input, { setWidth: false, setHeight: false });
		this.input.style.width  = (this.element.offsetWidth - this.paddingLeft) + 'px';
   		this.input.style.height = (this.element.offsetHeight - this.paddingTop) + 'px';
	}
});


riot.PopupTextEditor = Class.extend(riot.InplaceEditor, {

	edit: function() {
		if (!riot.activePopup) {
			ComponentEditor.getText(this.component.id, this.key,
				this.setText.bind(this));
		}
	},

	setText: function(text) {
		if ((!text || text.length == 0) && this.options.useInnerHtmlAsDefault) {
			text = this.element.innerHTML;
		}
		this.SUPER(text);
	},

	showEditor: function() {
		this.popup = new riot.TextareaPopup(this);
		this.popup.open();
	},

	close: function() {
		this.popup.close();
		riot.activeEditor = null;
		this.onclose();
	},

	getText: function() {
		return this.popup.getText();
	},

	onsave: function() {
		this.close();
	}
});

riot.RichtextEditor = Class.extend(riot.PopupTextEditor, {
	showEditor: function() {
		Resources.loadScriptSequence([
			{src: 'tiny_mce/tiny_mce_src.js', test: 'tinyMCE'},
			{src: 'tiny_mce/strict_mode_fix.js', test: 'tinyMCE.strictModeFixed'}
		]);
		Resources.waitFor('tinyMCE.strictModeFixed', this.openPopup.bind(this));
	},

	openPopup: function() {
		var p = this.popup = new riot.TinyMCEPopup(this);
		Resources.waitFor(function() { return p.ready }, p.open.bind(p));
	},

	save: function() {
		var text = this.getText();
		if (this.text != text) {
			if (this.options.split) {
				var chunks = [];
				var n = RBuilder.node('div');
				n.innerHTML = text;
				$A(n.childNodes).each(function(c) {
					if (c.nodeType == 1) {
						chunks.push('<' + c.nodeName + '>' + c.innerHTML
								+ '</' + c.nodeName + '>');
					}
					else if (c.nodeType == 3) {
						chunks.push('<p>' + c.nodeValue + '</p>');
					}
				});
				if (chunks.length == 0) {
					chunks.push('<p></p>');
				}
				this.component.updateTextChunks(this.key, chunks);
			}
			else {
				this.component.updateText(this.key, text, true);
			}
			this.onsave(text);
		}
	}

});

riot.Popup = Class.create();
riot.Popup.prototype = {
	initialize: function(title, content, ok, help) {
		this.ok = ok;
		this.overlay = RBuilder.node('div', {id: 'riot-overlay', style: {display: 'none'}});
		this.div = RBuilder.node('div', {id: 'riot-popup', style: {position: 'absolute'}},
			help ? RBuilder.node('div', {className: 'riot-help-button', onclick: help}) : null,
			this.closeButton = RBuilder.node('div', {className: 'riot-close-button', onclick: this.close.bind(this)}),
			RBuilder.node('div', {className: 'headline'}, title),
			this.content = content,
			this.okButton = RBuilder.node('div', {className: 'button-ok', onclick: ok}, 'Ok')
		);
		this.div.makeInvisible();
		document.body.appendChild(this.overlay);
		document.body.appendChild(this.div);
	},

	hideElements: function(name) {
		var exclude = this.div;
		$$(name).each(function (e) {
			if (!e.childOf(exclude)) {
				e.makeInvisible();
				e.hidden = true;
			}
		});
	},

	showElements: function(name) {
		$$(name).each(function (e) {
			if (e.hidden) {
				e.makeVisible();
				e.hidden = false;
			}
		});
	},

	open: function() {
		if (riot.activePopup) {
			return;
		}
		riot.activePopup = this;
		if (Prototype.Browser.IE) {
			this.hideElements('select');
			this.root = $$(document.compatMode && document.compatMode == 'BackCompat' ? 'body' : 'html').first().makeClipping();
		}
		this.hideElements('object');
		this.hideElements('embed');

		var top = Math.round(Viewport.getInnerHeight() / 2 - this.div.clientHeight / 2);
		var left = Math.round(Viewport.getInnerWidth() / 2 - this.div.clientWidth / 2);

		this.div.hide();
		this.div.style.position = '';
		if (this.div.getStyle('position') != 'fixed') {
			top += Viewport.getScrollTop();
			left += Viewport.getScrollLeft();
		}
		this.div.style.top = top + 'px';
		this.div.style.left = left + 'px';
		this.overlay.style.height = Viewport.getPageHeight() + 'px';
		this.overlay.show();
		riot.outline.suspended = true;
		riot.outline.hide();
		this.div.makeVisible().show();
	},

	close: function() {
		if (riot.activePopup == this) {
			if (Prototype.Browser.IE) {
				this.showElements('select');
				this.root.undoClipping();
			}
			this.showElements('object');
			this.showElements('embed');
			this.div.remove();
			this.overlay.remove();
			riot.outline.suspended = false;
			riot.activePopup = null;
		}
	}
}

riot.TextareaPopup = Class.extend(riot.Popup, {

	initialize: function(editor) {
		this.textarea = RBuilder.node('textarea', {value: editor.text || ''}),
		this.SUPER('${title.editorPopup}', this.textarea, editor.save.bind(editor), editor.help);
	},

	setText: function(text) {
		this.textarea.value = text;
		this.textarea.focus();
	},

	getText: function() {
		return this.textarea.value;
	}

});

riot.TinyMCEPopup = Class.extend(riot.TextareaPopup, {
	initialize: function(editor) {
		this.SUPER(editor);
		this.div.addClassName('riot-richtext');
		if (this.textarea.value == '') {
			this.textarea.value = '<p>&nbsp;</p>';
		}
		this.textarea.makeInvisible();
		this.textarea.style.position = 'absolute';
		riot.initTinyMCE();
		Resources.waitFor('tinyMCELang["lang_theme_block"]',
				this.addMCEControl.bind(this));
	},

	addMCEControl: function() {
		tinyMCE.addMCEControl(this.textarea);
		this.ready = true;
	},

	close: function() {
		tinyMCE.instances = tinyMCE.instances.without(tinyMCE.selectedInstance);
		tinyMCE.selectedInstance = null;
		this.SUPER();
	},

	setText: function(text) {
		tinyMCE.setContent(text);
	},

	getText: function() {
		return tinyMCE.getContent().strip();
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
			if (typeof(prop) == 'function') { continue }
			if (Prototype.Browser.IE && prop == 'font-size' && selector == 'body' && styles[prop].match(/(em|%)$/)) { continue }
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
			if (this.properties[selector]) {
				Object.extend(styles, this.getStyles(p, this.properties[selector]));
			}
			if (selector == 'body') {
				styles['background-color'] = RElement.getBackgroundColor(p);
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
	var clone = $(e.cloneNode(false));
	clone.hide().insertSelfBefore(e);
	riot.stylesheetMaker.copyStyles(clone, doc, riot.tinyMCEStyles);
	clone.remove();

	body.style.paddingLeft = '5px';

	// Add a print margin ...

	var bg = RElement.getBackgroundColor(e);
	var brightness = 0;
	if (bg == 'transparent') {
		brightness = 255;
	}
	else {
		$R(0,2).each(function(i) { brightness += parseInt(bg.parseColor().slice(i*2+1,i*2+3), 16) });
		brightness /= 3;
	}
	var bgImage = brightness > 227 ? 'margin.gif' : 'margin_hi.gif';

	var editorWidth = tinyMCE.isMSIE ? body.scrollWidth : riot.activeEditor.width;
	var componentWidth = riot.activeEditor.element.offsetWidth;
	var margin = editorWidth - componentWidth;
	if (margin > 0) {
		body.style.paddingRight = (margin - 5) + 'px';
		body.style.backgroundImage = 'url(' + Resources.resolveUrl(bgImage) + ')';
		body.style.backgroundRepeat = 'repeat-y';
		body.style.backgroundPosition = (componentWidth + 5) + 'px';
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
	setupcontent_callback: riot.setupTinyMCEContent,
	init_instance_callback: riot.initTinyMCEInstance,
	relative_urls: false,
	gecko_spellcheck: true,
	hide_selects_on_submit: false,
	theme: 'advanced',
	theme_advanced_layout_manager: 'RowLayout',
	theme_advanced_containers_default_align: 'left',
	theme_advanced_containers: 'buttons1, mceEditor, mceStatusbar',
	theme_advanced_container_buttons1: 'formatselect,italic,sup,bullist,numlist,outdent,indent,hr,link,unlink,anchor,code,undo,redo,charmap',
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
