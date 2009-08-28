riot.stopEvent = function(ev) {
	Event.stop(ev || window.event);
	return false;
}

riot.outline = {
	elements: {
		top: RBuilder.node('div', {className: 'riot-highlight riot-highlight-top'}).hide().appendTo(document.body),
		right: RBuilder.node('div', {className: 'riot-highlight riot-highlight-right'}).hide().appendTo(document.body),
		bottom: RBuilder.node('div', {className: 'riot-highlight riot-highlight-bottom'}).hide().appendTo(document.body),
		left: RBuilder.node('div', {className: 'riot-highlight riot-highlight-left'}).hide().appendTo(document.body)
	},
	
	show: function(el, onclick, excludes) {
		if (!window.riot || riot.outline.suspended) return;
		riot.outline.cancelHide();

		// el.offsetHeight may be 0, descend until an element with a height is found.		
		var h = el.offsetHeight;
		while (el && h == 0) {
			el = el.down();
			var h = el.offsetHeight;
		}
		if (!el) return;
		
		var w = el.offsetWidth;
		var pos = el.cumulativeOffset();
		
		riot.outline.elements.top.style.top = (pos.top - 1) + 'px';
		riot.outline.elements.bottom.style.top = (pos.top + h) + 'px';
		riot.outline.elements.right.style.left = (pos.left + w) + 'px';
		riot.outline.elements.top.style.width =	riot.outline.elements.bottom.style.width = (w + 2) + 'px';
		riot.outline.elements.left.style.top = riot.outline.elements.right.style.top = pos.top + 'px';
		riot.outline.elements.left.style.height = riot.outline.elements.right.style.height = h + 'px';
		riot.outline.elements.top.style.left = riot.outline.elements.left.style.left = riot.outline.elements.bottom.style.left = (pos.left - 1) + 'px';
		
		riot.outline.divs.invoke('show');
	},

	hide: function(ev) {
		if (window.riot && riot.outline) { 
			riot.outline.divs.invoke('hide');
		}
	},
	
	scheduleHide: function(ev) {
		riot.outline.timeout = setTimeout(riot.outline.hide, 1);
	},

	cancelHide: function() {
		if (riot.outline.timeout) {
			clearTimeout(riot.outline.timeout);
			riot.outline.timeout = null;
		}
	}
}
riot.outline.divs = $H(riot.outline.elements).values();

riot.InplaceEditor = Class.create({
	initialize: function(element, content, options) {
		this.element = $(element);
		this.content = content;
		this.key = this.element.readAttribute('riot:key');
		this.onclickHandler = this.onclick.bindAsEventListener(this);
		this.oninit(options);
		this.bShowOutline = this.showOutline.bindAsEventListener(this);
		this.bHideOutline = this.hideOutline.bindAsEventListener(this);
	},

	/* Subclasses may override this method to perform initalization upon creation */
	oninit: function(options) {
		this.options = options || {};
	},

	editOn: function() {
		this.element.disableLinks();
		this.element.onclick = this.onclickHandler;
		this.element.addClassName('riot-editable-text');
		this.element.observe('mouseover', this.bShowOutline);
		this.element.observe('mouseout', this.bHideOutline);
	},
	
	editOff: function() {
		if (riot.activeEditor == this) {
			this.close();
		}
		this.element.onclick = null;
		this.element.enableLinks();
		this.element.removeClassName('riot-editable-text');
		this.element.stopObserving('mouseover', this.bShowOutline);
		this.element.stopObserving('mouseout', this.bHideOutline);
	},

	showOutline: function(ev) {
		if (window.Event) Event.stop(ev);
		if (window.riot) riot.outline.show(this.element);
	},
	
	hideOutline: function(ev) {
		if (ev) Event.stop(ev);
		if (window.riot) riot.outline.scheduleHide();
	},

	/* Handler that is invoked when an enabled editor is clicked */
	onclick: function(ev) {
		Event.stop(ev);
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
			this.content.updateText(this.key, text);
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
});

riot.InplaceTextEditor = Class.create(riot.InplaceEditor, {

	oninit: function(options) {
		this.options = options || {};
		this.inline = this.element.getStyle('display') == 'inline';
		this.input = this.inline
				? new Element('input', {type: 'text'})
				: new Element('textarea', {wrap: 'off'});

		this.input.className = 'riot-inplace-text-editor';
		this.input.setStyle({
			position: 'absolute', overflow: 'hidden',
			top: 0,	left: 0, border: 0, padding: 0, margin: 0, borderWidth: 0,
			backgroundColor: 'transparent', zIndex: 10000,
			resize: 'none'
		});

		this.input.onkeypress = this.input.onkeyup = this.updateElement.bindAsEventListener(this);
		this.input.onblur = this.close.bindAsEventListener(this);
		
		var styles = ['font-size', 'font-weight', 'font-family', 'font-style',
			'color', 'background-color', 'background-image', 
			'background-repeat', 'text-decoration',
			'letter-spacing', 'line-height', 'padding-left', 'padding-top'];
		
		if (this.element.getStyle('display') == 'block') {
			styles.push('text-align');
		}
		
		if (Prototype.Browser.IE) {
			styles.push('background-position-x');
			styles.push('background-position-y');
		}
		else {
			styles.push('background-position');
		}
		this.input.cloneStyle(this.element, styles);

		if (this.options.textTransform) {
			this.input.cloneStyle(this.element, ['text-transform']);
		}
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
		else {
			var i = 0;
			if (Prototype.Browser.WebKit) {
				i = -3;
			}
			else if (Prototype.Browser.Gecko) {
				i = -1;
			}
			this.input.style.textIndent = i + 'px';
		}
		
		this.extraWidth = 0;
		if (this.inline) {
			// If editing an inline element we have to add some extra width 
			// to the textarea. Otherwise each new character would cause a
			// linebreak. Twice the font-size should be enough ...
			this.extraWidth = parseInt(this.element.getStyle('font-size')) * 2;
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

	close: function($super) {
		$super();
		this.input.remove();
		this.lastText = null;
	},

	getText: function() {
		var newText = this.input.value
			.replace(/&/g, '&amp;')
			.replace(/</g, '&lt;')
			.replace(/>/g, '&gt;')
			.replace(/\n/g, '<br />');
			
		if (newText.blank()) {
			return this.text;
		}
		return newText;
	},

	onsave: function(text) {
		this.element.innerHTML = text;
		this.input.hide();
		this.element.makeVisible();
	},

	updateElement: function() {
		var text = this.input.value;
		if (!this.lastText || this.lastText != text) {
			this.lastText = text;
			var html = text.replace(/&/g, '&amp;')
				.replace(/</g, '&lt;')
				.replace(/>/g, '&gt;')
				.replace(/\n$/, '\n ') 
				.replace(/( (?= )|^ | $)/mg, '&nbsp;')
				.replace(/\n/g, '<br />');
				
			this.element.update(html);
			this.resize();
		}
	},

	resize: function() {
		Position.clone(this.element, this.input, { setWidth: false, setHeight: false });
		this.input.style.width = (this.element.offsetWidth - this.paddingLeft + this.extraWidth) + 'px';
		this.input.style.height = (this.element.offsetHeight - this.paddingTop) + 'px';
	}
});


riot.PopupTextEditor = Class.create(riot.InplaceEditor, {

	edit: function() {
		if (!riot.activePopup) {
			this.content.retrieveText(this.key, this.setText.bind(this));
		}
	},

	setText: function($super, text) {
		if ((!text || text.length == 0) && this.options.useInnerHtmlAsDefault) {
			text = this.element.innerHTML;
		}
		$super(text);
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
	
	save: function() {
		var text = this.getText();
		if (this.text != text) {
			this.content.updateText(this.key, text, true);
		}
		this.onsave(text);
	},

	onsave: function() {
		this.close();
	}
});

riot.RichtextEditor = Class.create(riot.PopupTextEditor, {
	showEditor: function() {
		tinyMCE_GZ = {loaded: true};
		Resources.loadScript('tiny_mce/tiny_mce_src.js', 'tinymce');
		Resources.waitFor('tinymce.WindowManager', this.openPopup.bind(this));
	},

	openPopup: function() {
		var settings = Object.extend(riot.TinyMCEProfiles[this.options.config || 'default'], riot.fixedTinyMCESettings); 
		this.popup = new riot.TinyMCEPopup(this, settings);
	},

	save: function() {
		var text = this.getText();
		if (this.text != text) {
			if (this.options.split) {
				var chunks = [];
				var n = RBuilder.node('div');
				n.innerHTML = text;
				for (var i = 0; i < n.childNodes.length; i++) { 
					var c = n.childNodes[i];
					if (c.nodeType == 1) {
						chunks.push('<' + c.nodeName.toLowerCase() + '>'
								+ this.cleanUp(c.innerHTML)
								+ '</' + c.nodeName.toLowerCase() + '>');
					}
					else if (c.nodeType == 3) {
						chunks.push('<p>' + c.nodeValue + '</p>');
					}
				}
				if (chunks.length == 0) {
					chunks.push('<p></p>');
				}
				this.content.updateTextChunks(this.key, chunks);
			}
			else {
				this.content.updateText(this.key, this.cleanUp(text), true);
			}
			this.onsave(text);
		}
		else {
			this.close();
		}
	},
	
	onclose: function() {
	},
	
	cleanUp: function(str) {
 		str = str.replace(/<!(?:--[\s\S]*?--\s*)?>\s*/g, '');
 		return str.replace(/<\s*?br\s*?>/ig, '<br />');
	}

});

riot.Popup = Class.create({
	initialize: function(title, content, ok, autoSize) {
		this.ok = ok;
		this.autoSize = autoSize;
		this.overlay = new Element('div', {id: 'riot-overlay'}).setStyle({display: 'none'});
		
		if (typeof content == 'string') {
			this.content = new Element('iframe', {src: content, width: 1, height: 1}).observe('load', this.open.bind(this));
		}
		else {
			this.content = content;
		}
		
		this.div = new Element('div', {id: 'riot-popup'}).setStyle({position: 'absolute'})
			.insert(new Element('div', {'class': 'riot-close-button'}).observe('click', this.close.bind(this)))
			.insert(new Element('div', {'class': 'headline'}).insert(title))
			.insert(this.content)
			.insert(ok ? new Element('div', {'class': 'button-ok'}).observe('click', ok.bind(this)).insert('Ok') : '')
			.makeInvisible();
		
		this.keyDownHandler = this.handleKeyDown.bindAsEventListener(this);
		Event.observe(document, 'keydown', this.keyDownHandler);
		
		document.body.appendChild(this.overlay);
		document.body.appendChild(this.div);
	},

	hideElements: function(name) {
		var exclude = this.div;
		$$(name).each(function (e) {
			if (!e.childOf(exclude) && e.getStyle('visibility') != 'hidden') {
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
		var initialWidth = document.body.offsetWidth;
		if (Prototype.Browser.IE) {
			this.hideElements('select');
		}
		this.root = $$(document.compatMode && document.compatMode == 'BackCompat' ? 'body' : 'html').first().makeClipping();
		this.hideElements('object');
		this.hideElements('embed');

		var top = 50;
		var left = 50;
		if (this.autoSize) {
			var doc = this.content.contentWindow || this.content.contentDocument;
			if (doc) {
				if (doc.document) {
					doc = doc.document;
				}
				doc.viewport = document.viewport;
				doc.body.parentNode.style.border = 'none';
				var w = 600;
				Element.extend(doc.body).select('.element').each(function (el) {
					if (el.up().getWidth() > w) {
						w = el.up().getWidth();
					}
				});
				w += 32;
				w = Math.min(Math.round(document.viewport.getWidth() - 32), w);
				var offsetH = Math.max(doc.body.offsetHeight,document.documentElement.clientHeight) + 32;
				var h = Math.min(Math.round(doc.viewport.getHeight() * 0.8), offsetH);
				this.content.style.height = h + 'px';
				this.div.style.width = w + 'px';
			}
			top = Math.max(5, Math.round(document.viewport.getHeight() / 2 - this.div.clientHeight / 2));
			left = Math.round(document.viewport.getWidth() / 2 - this.div.clientWidth / 2);
		}
		else {
			this.div.style.width = (document.viewport.getWidth() - 100) + 'px';
			this.content.style.height = (document.viewport.getHeight() - 150) + 'px';
		}

		this.div.hide();
		this.div.style.position = '';
		if (this.div.getStyle('position') != 'fixed') {
			var scroll = document.viewport.getScrollOffsets();
			top += scroll.top;
			left += scroll.left;
		}
		this.div.style.top = top + 'px';
		this.div.style.left = left + 'px';
		
		var h = Math.max(document.viewport.getHeight(), document.body.getHeight());
		this.overlay.style.height = h + 'px';
		this.overlay.show();
		riot.outline.suspended = true;
		riot.outline.hide();
		this.div.makeVisible().show();

		// The call to makeClipping() above removes the scrollbars - add a margin to prevent visual shift. 
		var margin = (document.body.offsetWidth - initialWidth) + 'px'; 
		document.body.style.marginRight = margin;
		this.overlay.style.paddingRight = margin; 
	},

	close: function() {
		Event.stopObserving(document, 'keydown', this.keyDownHandler);
		if (riot.activePopup == this) {
			if (Prototype.Browser.IE) {
				this.showElements('select');
			}
			// Reset the margin
			document.body.style.marginRight = 0;
			this.root.undoClipping();
			if (Prototype.Browser.WebKit) {
				// Force re-rendering of scrollbars in Safari
				window.scrollBy(0,-1);
				window.scrollBy(0,1);
			}
			this.showElements('object');
			this.showElements('embed');
			this.div.remove();
			this.overlay.remove();
			riot.outline.suspended = false;
			riot.activePopup = null;
		}
	},

	/* Handler that is invoked when a key has been pressed */
	handleKeyDown: function(ev) {
		if (ev.keyCode == Event.KEY_ESC) {
			Event.stop(ev);
			this.close();
		}
	}

});

riot.TextareaPopup = Class.create(riot.Popup, {

	initialize: function($super, editor) {
		this.textarea = RBuilder.node('textarea', {value: editor.text || ''}),
		$super('${title.editorPopup}', this.textarea, editor.save.bind(editor), true);
		var availableTextareaHeight = document.viewport.getHeight() - 82;
		if (availableTextareaHeight < this.textarea.getHeight()) {
			this.textarea.style.height = availableTextareaHeight + 'px';
		}
	},

	setText: function(text) {
		this.textarea.value = text;
		this.textarea.focus();
	},

	getText: function() {
		return this.textarea.value;
	}

});

riot.TinyMCEPopup = Class.create(riot.TextareaPopup, {
	initialize: function($super, editor, settings) {
		$super(editor);
		this.div.addClassName('riot-richtext');
		if (this.textarea.value == '') {
			this.textarea.value = '<p>&nbsp;</p>';
		}
		this.textarea.makeInvisible();
		this.open();

		tinymce.dom.Event._pageInit();
		tinyMCE.init(Object.extend({
			elements: this.textarea.identify(),
			auto_focus: this.textarea.id,
			init_instance_callback: this.setInstance.bind(this)
		}, settings));
	},

	setInstance: function(tinymce) {
		this.tinymce = tinymce;
	},
	
	close: function($super) {
		tinymce.EditorManager.remove(this.tinymce);
		$super();
	},

	setText: function(text) {
		this.tinymce.setContent(text);
	},

	getText: function() {
		var html = this.tinymce.getContent();
		html = html.replace(/<!--(.|\n)*?-->/g, '');
		html = html.replace(/&lt;!--(.|\n)*?(smso-|@page)(.|\n)*?--&gt;/g, '');
		html = html.replace(/<p>\s*<\/p>/g, '');
		return html.strip();
	}

});

riot.stylesheetMaker = {

	properties: {
		'*': ['font-family', 'font-size', 'font-weight', 'font-style',
			'line-height', 'text-decoration', 'color', 'background-color',
			'background-image', 'background-position', 'background-repeat',
			'margin-top', 'margin-right', 'margin-bottom', 'margin-left',
			'padding-top', 'padding-right', 'padding-bottom', 'padding-left'],
		'a': ['border-bottom'],
		'hr': ['width', 'height'],
		'ul li': ['list-style-type', 'list-style-position', 'list-style-image']
	},

	selectors: ['body', 'p', 'a', 'strong', 'em', 'h1', 'h2',
		'h3', 'h4', 'hr', 'ul', 'ul li', 'ol', 'ol li'],

	getRule: function(selector, styles) {
		var css = '';
		for (prop in styles) {
			if (typeof(prop) == 'function') { continue }
			if (Prototype.Browser.IE && prop == 'font-size' 
					&& selector == 'body' 
					&& styles[prop].match(/(em|%)$/)) { 
				continue;
			}
			css += prop + ':' + styles[prop]
			if (selector == 'a' && (prop == 'color' || prop == 'text-decoration')) {
				css += ' !important';
			}
			css += ';'
		}
		return selector + ' {' + css + '}\n';
	},

	getStyles: function(el, props) {
		var result = {};
		for (var i = 0; i < props.length; i++) {
			result[props[i]] = Element.getStyle(el, props[i]);
		}
		return result;
	},

	getRules: function(el, classes) {
		var rules = '';
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
				styles['background-color'] = RElement.getBackgroundColor(p) + ' !important';
			}
			rules += this.getRule(selector, styles);
		}
		if (classes) {
			for (var i = 0; i < classes.length; i++) {
				var e = document.createElement('span');
				e.className = classes[i];
				el.appendChild(e);
				var styles = this.getStyles(e, this.properties['*']);
				rules += this.getRule('.' + classes[i], styles);
			}
		}
		return rules;
	},
	
	copyStyles: function(el, doc, classes) {
		var rules = this.getRules(el, classes);
		var style = doc.createElement('style');
		style.type = 'text/css';
		var head = doc.getElementsByTagName('head')[0];
		if (style.styleSheet) {
			style.styleSheet.cssText = rules;
		}
		else {
			style.appendChild(doc.createTextNode(rules));
		}
		head.appendChild(style);
	}
}

riot.setupTinyMCEContent = function(editorId, body, doc) {
	var inst = tinymce.EditorManager.editors[editorId];
	
	var e = riot.activeEditor.element;
	var clone = $(e.cloneNode(false));
	e.insert({before: clone.hide()});
	
	var classNames = null;
	var styles = inst.settings.theme_advanced_styles;
	if (styles) {
		classNames = styles.split(';').collect(function(pair) {return pair.split('=')[1]});
	}
	riot.stylesheetMaker.copyStyles(clone, doc, classNames);
	
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

	var editorWidth = body.scrollWidth ? body.scrollWidth : inst.contentWindow.innerWidth;
	var contentWidth = riot.activeEditor.element.offsetWidth;
	var margin = editorWidth - contentWidth;
	if (margin > 0) {
		body.style.marginRight = (margin - 5) + 'px';
		body.style.backgroundImage = 'url(' + Resources.resolveUrl(bgImage) + ')';
		body.style.backgroundRepeat = 'repeat-y';
		body.style.backgroundPosition = (contentWidth + 5) + 'px';
		body.style.backgroundAttachment = 'fixed';
	}
}

riot.fixedTinyMCESettings = {
	mode: 'exact',
	width: '100%',
	language: riot.language,
	skin: 'riot',
	theme: 'advanced',
	add_unload_trigger: false,
	strict_loading_mode: true,
	use_native_selects: true,
	setupcontent_callback: riot.setupTinyMCEContent,
	relative_urls: false,
	theme_advanced_layout_manager: 'RowLayout',
	theme_advanced_containers_default_align: 'left',
	theme_advanced_container_mceeditor: 'mceeditor',
	theme_advanced_containers: 'buttons1,mceeditor'
}

riot.TinyMCEProfiles = {};

ComponentEditor.getTinyMCEProfiles(function(profiles) {
	riot.TinyMCEProfiles = profiles;
});

