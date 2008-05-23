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
	initialize: function(element, component, options) {
		this.element = $(element);
		this.component = component;
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
});

riot.InplaceTextEditor = Class.create(riot.InplaceEditor, {

	oninit: function(options) {
		this.options = options || {};
		this.inline = this.element.getStyle('display') == 'inline';
		this.input = this.inline ? RBuilder.node('input', {type: 'text'})
				: RBuilder.node('textarea', {wrap: 'off'});

		this.input.setStyle({
			position: 'absolute', overflow: 'hidden',
			top: 0,	left: 0, border: 0, padding: 0, margin: 0,
			backgroundColor: 'transparent', zIndex: 10000
		});

		this.input.onkeypress = this.input.onkeyup = this.updateElement.bindAsEventListener(this);
		this.input.onblur = this.close.bindAsEventListener(this);
		this.input.cloneStyle(this.element, [
			'font-size', 'font-weight', 'font-family', 'font-style',
			'color', 'background-color', 'text-align', 'text-decoration',
			'letter-spacing', 'line-height', 'padding-left', 'padding-top']);

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
			this.component.retrieveText(this.key, this.setText.bind(this));
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
			this.component.updateText(this.key, text, true);
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
				this.component.updateTextChunks(this.key, chunks);
			}
			else {
				this.component.updateText(this.key, this.cleanUp(text), true);
			}
			this.onsave(text);
		}
	},
	
	cleanUp: function(str) {
 		str = str.replace(/<!(?:--[\s\S]*?--\s*)?>\s*/g, '');
 		return str.replace(/<\s*?br\s*?>/ig, '<br />');
	}

});

riot.Popup = Class.create({
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
		this.keyDownHandler = this.handleKeyDown.bindAsEventListener(this);
		Event.observe(document, 'keydown', this.keyDownHandler);
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

		var top = Math.max(5, Math.round(document.viewport.getHeight() / 2 - this.div.clientHeight / 2));
		var left = Math.round(document.viewport.getWidth() / 2 - this.div.clientWidth / 2);

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
	},

	close: function() {
		Event.stopObserving(document, 'keydown', this.keyDownHandler);
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
		$super('${title.editorPopup}', this.textarea, editor.save.bind(editor), editor.help);
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
		return this.tinymce.getContent().strip();
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
	var inst = tinymce.EditorManager.editors[editorId];
	var style = doc.createElement('style');
	style.type = 'text/css';
	var head = doc.getElementsByTagName('head')[0];
	head.appendChild(style);

	var e = riot.activeEditor.element;
	var clone = $(e.cloneNode(false));
	clone.hide().insertSelfBefore(e);
	
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

riot.initSwfUpload = function(readyCallback) {
	if (!riot.swfUpload) {
		riot.swfUpload = new SWFUpload({
			upload_url: riot.path + '/components/upload',
			flash_url: Resources.basePath + 'swfupload/swfupload.swf',
			file_types: '*.jpg;*.gif;*.png',
			file_types_description: 'Images',
			file_size_limit: 0,
			swfupload_loaded_handler: readyCallback,
			file_queued_handler: function() {
				riot.swfUpload.startUpload();
			}
		});
	}
	else {
		readyCallback();
	}
}

riot.ImageEditor = Class.create(riot.InplaceEditor, {
	initialize: function($super, el, component, options) {
		$super(el, component, options);
		this.key = this.element.readAttribute('riot:key');
		var aw = el.parentNode.offsetWidth;
		if (this.options.maxWidth == 'auto') this.options.maxWidth = aw;
		if (this.options.minWidth == 'auto') this.options.minWidth = aw;
	},
	
	setToken: function(token) {
		this.token = token;
		this.editOn();
	},
	
	editImagesOn: function() {
		this.element.disableLinks();
		ComponentEditor.generateToken(this.component.controller.contextKey, this.setToken.bind(this));
	},
	
	editImagesOff: function() {
		this.editOff(); 
		ComponentEditor.invalidateToken(this.token);
	},
	
	edit: function() {
		riot.outline.hide();
		riot.swfUpload.setPostParams({token: this.token, fileStore: this.options.fileStore});		
		riot.swfUpload.uploadComplete_handler = this.uploadComplete.bind(this);
		riot.swfUpload.selectFile();
	},
	
	uploadComplete: function(file, json) {		
		riot.outline.suspended = true;
		var file = eval(json);
		this.imageId = file.id;
		this.cropper = new Cropper.UI(this, file.uri);
	},
	
	crop: function(w, h, x, y, sw) {
		this.component.cropImage(this.key, this.imageId,
				w, h, x, y, sw, this.update.bind(this));
	},
	
	update: function(path) {
		if (this.options.updateFromServer) {
			riot.outline.suspended = false;
			this.cropper.destroy();		
			this.cropper = null;
			this.component.update();			
		}
		else {
			var img = new Image();
			img.onload = this.onload.bind(this, img, path);
			img.src = riot.contextPath + path;
			this.component.markDirty();
		}
	},
	
	onload: function(img, path) {
		riot.outline.suspended = false;
		var src = img.src;
		if (this.options.srcTemplate && path.indexOf('http') != 0) { 
			src = this.options.srcTemplate.replace('*', path);
		}
		if (this.element.tagName == 'IMG') {   
			this.element.width = img.width;
			this.element.height = img.height;
			this.element.src = src;
		}
		else {
			this.element.style.width = img.width + 'px';
			this.element.style.height = img.height + 'px';
			this.element.style.backgroundImage = 'url(' + src + ')';
		}
		this.cropper.destroy();		
		this.cropper = null;
	}
});

var Cropper = {
	elementOffset: function(el) {
		var p = Position.cumulativeOffset(el);
		return new Cropper.Pos(p[0], p[1]);
	},
	elementSize: function(el) {
		return new Cropper.Pos(el.offsetWidth, el.offsetHeight);
	}
};

Cropper.Pos = Class.create({
	initialize: function(x, y) {
		this.x = Math.round(x || 0);
		this.y = Math.round(y || 0);
	},

	setFromMouse: function(event) {
		this.x = Event.pointerX(event); this.y = Event.pointerY(event);
	},

	mouseDelta: function(event, rtl) {
		if (rtl) {
			return new Cropper.Pos(this.x - Event.pointerX(event), Event.pointerY(event) - this.y);
		}
		else {
			return new Cropper.Pos(Event.pointerX(event) - this.x, Event.pointerY(event) - this.y);
		}
	},

	moveBy: function(x, y) {
		this.x = Math.round(this.x + x);
		this.y = Math.round(this.y + y);
	},

	keepWithin: function(minX, minY, maxX, maxY) {
		if (maxX < minX) maxX = minX; if (maxY < minY) maxY = minY;
		if (this.x < minX) this.x = minX; else if (this.x > maxX) this.x = maxX;
		if (this.y < minY) this.y = minY; else if (this.y > maxY) this.y = maxY;
	},

	applyOffset: function(el) {
		el.style.left = -this.x + 'px';
		el.style.top = -this.y + 'px';
	},

	applySize: function(el) {
		el.style.width = this.x + 'px';
		el.style.height = this.y + 'px';
	}
});

Cropper.UI = Class.create({

	initialize: function(editor, src) {
		this.editor = editor;
		this.src = src;
		
		this.initialSize = Cropper.elementSize(editor.element);
		this.element = RBuilder.node('div', {className:'cropper'})
			.cloneStyle(editor.element, ['margin', 'position', 'top', 'left']);
		
		this.element.insertSelfAfter(this.editor.element);
		
		this.preview = RBuilder.node('div')
			.setStyle({MozUserSelect: 'none', overflow: 'hidden', position: 'relative'})
			.cloneStyle(editor.element,	['border'])
			.appendTo(this.element);

		this.rightAligned = editor.element.getStyle('float') == 'right' 
				|| editor.element.originalFloat == 'right';
		
		var cursor = this.rightAligned ? 'sw-resize' : 'se-resize'; 
		this.resizeHandle = RBuilder.node('div', {className: cursor}).setStyle({
			position: 'absolute', bottom: 0, zIndex: 100, cursor: cursor, overflow: 'hidden'
		});
		this.resizeHandle.style[this.rightAligned ? 'left' : 'right'] = '0'; 
		this.resizeHandle.className = cursor;
		
		this.resizeHandle.appendTo(this.preview);
		Event.observe(this.resizeHandle, 'mousedown', this.onMouseDownResize.bindAsEventListener(this));

		this.elementPos = Cropper.elementOffset(this.element);
		this.mousePos = new Cropper.Pos();
		this.click = new Cropper.Pos();
		this.offset = new Cropper.Pos();

		this.img = RBuilder.node('img', {unselectable: 'on'})
				.setStyle({position: 'absolute'}).appendTo(this.preview);
		
		// Enable zooming using the mouse wheel:
		var bOnMouseWheel = this.onMouseWheel.bindAsEventListener(this);
		if (Prototype.Browser.Gecko) {
			this.img.addEventListener("DOMMouseScroll", bOnMouseWheel, true);
		}
		else {
			this.img.onmousewheel = bOnMouseWheel;
			window.focus(); // Fix for IE6 
		}

		Event.observe(this.img, 'mousedown', this.mouseDownHandler = this.onMouseDown.bindAsEventListener(this));
		Event.observe(document, 'mousemove', this.mouseMoveHandler = this.onMouseMove.bindAsEventListener(this));
		Event.observe(document, 'mouseup', this.mouseUpHandler = this.onMouseUp.bindAsEventListener(this));
		
		this.img.style.width = 'auto';
		this.img.style.height = 'auto';
		
		var image = new Image();
		image.onload = this.onLoadImage.bind(this, image);
		
		if (src.indexOf('http') == 0) {
			image.src = src;
		}
		else {
			image.src = riot.contextPath + src;
		}
	},
	
	onLoadImage: function(image) {
		this.editor.element.hide();
		this.img.src = image.src; 
		this.imageSize = new Cropper.Pos(image.width, image.height);
		
		// Make sure min and max are not greater than the image dimensions:
		this.min = new Cropper.Pos(this.editor.options.minWidth || 10, this.editor.options.minHeight || 10);
		this.min.keepWithin(0, 0, this.imageSize.x, this.imageSize.y);
		
		this.minZoom = this.min.x;
		
		this.max = new Cropper.Pos(this.editor.options.maxWidth || 10000, this.editor.options.maxHeight || 10000);
		this.max.keepWithin(0, 0, this.imageSize.x, this.imageSize.y);
		
		if (this.min.x == this.max.x && this.min.y == this.max.y) {
			this.resizeHandle.hide();
		}
		this.setSize(this.initialSize);
	},

	onMouseWheel: function(ev) {
		var delta;
		if (ev.wheelDelta) {
			delta = -ev.wheelDelta / 40;
		}
		else {
			delta = ev.detail || 0;
			if (delta < -3) delta = -3;
			if (delta > 3) delta = 3;
		}
		var z = this.img.width + this.imageSize.x / 100 * delta;
		z = Math.max(this.minZoom, z);
		z = Math.min(this.imageSize.x, z);
		this.zoom(z);
		Event.stop(ev);
	},

	setSize: function(size) {
		size.keepWithin(this.min.x, this.min.y, this.max.x, this.max.y);
		size.applySize(this.preview);

		this.offset.keepWithin(0, 0, this.img.width - size.x, this.img.height - size.y);
		this.offset.applyOffset(this.img);

		this.minZoom = Math.max(size.x, Math.ceil(this.imageSize.x * (size.y / this.imageSize.y)));
		
		if (this.img.width < this.minZoom) this.zoom(this.minZoom);
	},

	zoom: function(newWidth) {
		if (isNaN(newWidth)) return;
		var scale = newWidth / this.img.width;
		var originalScale = newWidth / this.imageSize.x;
		var newHeight = Math.round(this.imageSize.y * originalScale);
		newWidth = Math.round(newWidth);
		
		if (this.mode != 'resize') {
			this.elementPos = Cropper.elementOffset(this.element);
			var center = new Cropper.Pos(this.mousePos.x - this.elementPos.x, this.mousePos.y - this.elementPos.y);
			var g = new Cropper.Pos(this.offset.x + center.x, this.offset.y + center.y);
			this.offset.moveBy(g.x * scale - g.x, g.y * scale - g.y);
		}
		this.offset.keepWithin(0, 0, newWidth - this.preview.offsetWidth, newHeight - this.preview.offsetHeight);
		this.offset.applyOffset(this.img);
		this.img.style.width = newWidth + 'px';
		this.img.style.height = newHeight + 'px';
	},

	onMouseDown: function(event) {
		this.mode = 'pan';
		Event.stop(event);
	},

	onMouseDownResize: function(event) {
		this.mode = 'resize';
		this.elementPos = Cropper.elementOffset(this.element);
		if (this.rightAligned) this.elementPos.x += this.preview.offsetWidth;
		if (document.all) {
			this.resizeHandle.style.cursor = 'auto';
			this.img.style.cursor = 'auto';
		}
		else {
			this.img.style.cursor = document.body.style.cursor = this.resizeHandle.style.cursor;
		}
		Event.stop(event);
	},

	onMouseUp: function(event) {
		if (this.mode == null) {
			this.crop();
		}
		this.mode = null;
		if (document.all) {
			this.resizeHandle.style.cursor = 'se-resize';
		}
		this.img.style.cursor = this.img.width > this.preview.offsetWidth || this.img.height > this.preview.offsetHeight ? 'move' : 'auto';
		document.body.style.cursor = 'auto';
	},

	onMouseMove: function(event) {
		if (this.mode == 'resize') {
			this.setSize(this.elementPos.mouseDelta(event, this.rightAligned));
			Event.stop(event);
		}
		else if (this.mode == 'pan') {
			var delta = this.mousePos.mouseDelta(event);
			this.offset.moveBy(-delta.x, -delta.y);
			this.offset.keepWithin(0, 0, this.img.width - this.preview.offsetWidth, this.img.height - this.preview.offsetHeight);
			this.offset.applyOffset(this.img);
			Event.stop(event);
		}
		this.mousePos.setFromMouse(event);
	},
	
	destroy: function() {
		Event.stopObserving(this.img, 'mousedown', this.mouseDownHandler);
		Event.stopObserving(document, 'mousemove', this.mouseMoveHandler);
		Event.stopObserving(document, 'mouseup', this.mouseUpHandler);
		this.element.remove();
		this.editor.element.show();
	},

	crop: function() {
		this.resizeHandle.hide();
		var w = parseInt(this.preview.style.width);
		var h = parseInt(this.preview.style.height);
		if (isFinite(w) && isFinite(h)) {
			this.editor.crop(w, h, this.offset.x, this.offset.y,
					this.img.width);
		}
	}
	
});

