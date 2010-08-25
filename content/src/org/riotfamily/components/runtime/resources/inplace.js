riot.stopEvent = function(ev) {
	Event.stop(ev || window.event);
	return false;
}

riot.outline = {
	elements: {
		top: new Element('div').addClassName('riot-highlight riot-highlight-top').hide(),
		right: new Element('div').addClassName('riot-highlight riot-highlight-right').hide(),
		bottom: new Element('div').addClassName('riot-highlight riot-highlight-bottom').hide(),
		left: new Element('div').addClassName('riot-highlight riot-highlight-left').hide()
	},
	
	show: function(el, onclick, excludes) {
		if (!window.riot || riot.outline.suspended) return;

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
	}
}

riot.outline.divs = Object.values(riot.outline.elements);
riot.outline.divs.each(function(e) {document.body.appendChild(e)});

riot.InplaceEditor = Class.create({
	initialize: function(element, content, options) {
		this.element = $(element);
		this.content = content;
		this.key = this.element.readAttribute('riot:key');
		this.onclickHandler = this.onclick.bindAsEventListener(this);
		this.bShowOutline = this.showOutline.bindAsEventListener(this);
		this.bHideOutline = this.hideOutline.bindAsEventListener(this);
		this.options = options || {};
	},

	editOn: function() {
		this.element.disableLinks();
		this.element.onclick = this.onclickHandler;
		this.element.addClassName('riot-editable-text');
		this.element.observe('mouseenter', this.bShowOutline);
		this.element.observe('mouseleave', this.bHideOutline);
	},
	
	editOff: function() {
		if (riot.activeEditor == this) {
			this.close();
		}
		this.element.onclick = null;
		this.element.enableLinks();
		this.element.removeClassName('riot-editable-text');
		this.element.stopObserving('mouseenter', this.bShowOutline);
		this.element.stopObserving('mouseleave', this.bHideOutline);
	},

	showOutline: function(ev) {
		if (window.Event) Event.stop(ev);
		if (window.riot) riot.outline.show(this.element);
	},
	
	hideOutline: function(ev) {
		if (ev) Event.stop(ev);
		if (window.riot) riot.outline.hide();
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

	setText: function(text) {
		this.text = text;
		this.show();
	},

	/* Subclasses must implement this method to show a widget to edit the current text */
	show: function() {
	},

	hide: function() {
	},
	
	/* Subclasses must implement this method to return the edited text. */
	getText: function() {
		return this.text;
	},

	save: function() {
		var text = this.getText();
		if (this.text != text) {
			this.content.updateText(this.key, text);
			this.text = text;
		}
	},

	/** 
	 * This method is invoked when the active editor is disabled (either by
	 * enabling another editor or by switching to another tool).
	 * The default behaviour is to save all changes.
	 */
	close: function() {
		if (riot.activeEditor == this) {
			riot.activeEditor = null;
		}
		this.save();
		this.hide();
		this.element.fire('inplace:edited');
	}

});

riot.InplaceTextEditor = Class.create(riot.InplaceEditor, {

	initialize: function($super, element, content, options) {
		$super(element, content, options);
		this.inline = this.element.getStyle('display') == 'inline';
		this.input = this.inline
				? new Element('input', {type: 'text'})
				: new Element('textarea');

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

	show: function() {
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

	hide: function() {
		this.element.innerHTML = this.text;
		this.element.makeVisible();
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
		this.content.retrieveText(this.key, this.setText.bind(this));
	},

	setText: function($super, text) {
		if ((!text || text.length == 0) && this.options.useInnerHtmlAsDefault) {
			text = this.element.innerHTML;
		}
		$super(text);
	},

	show: function() {
		this.textarea = new Element('textarea');
		this.textarea.value = this.text || '';
		this.dialog = new riot.window.Dialog({
			title: '${title.editorPopup}', 
			content: this.textarea,
			closeButton: true,
			onClose: this.close.bind(this),
			minWidth: 600,
			minHeight: document.viewport.getHeight() - 150
		});
		this.textarea.style.height = this.dialog.getContentHeight() + 'px';
		this.textarea.focus();
	},

	getText: function() {
		return this.textarea.value;
	}
	
});

riot.RichtextEditor = Class.create(riot.PopupTextEditor, {
	show: function($super) {
		tinyMCE_GZ = {loaded: true};
		var $this = this;
		riot.Resources.loadScript('tinymce/tiny_mce_src.js', 'tinymce');
		riot.Resources.waitFor('tinymce.WindowManager', function() {
			$super();
			$this.initEditor();
		});
	},

	initEditor: function() {
		this.dialog.box.addClassName('riot-richtext');
		if (this.textarea.value == '') {
			this.textarea.value = '<p>&nbsp;</p>';
		}
		this.textarea.makeInvisible();
		var settings = Object.extend(
			riot.TinyMCEProfiles[this.options.config || 'default'],
			riot.fixedTinyMCESettings);
		
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
	
	hide: function() {
		tinymce.EditorManager.remove(this.tinymce);
	},

	getText: function() {
		return this.cleanUp(this.tinymce.getContent());
	},
	
	save: function() {
		if (this.tinymce.isDirty()) {
			var text = this.getText();
			if (this.options.split) {
				var chunks = [];
				var n = new Element('div').update(text);
				for (var i = 0; i < n.childNodes.length; i++) { 
					var c = n.childNodes[i];
					if (c.nodeType == 1) {
						var html = this.cleanUp(c.innerHTML);
						if (html.length > 0) {
							var tag = c.nodeName.toLowerCase();
							if (c.className) {
								chunks.push('<'+tag+' class="'+c.className+'">' + html + '</'+tag+'>');
							}
							else {
								chunks.push('<'+tag+'>' + html + '</'+tag+'>');
							}
						}
					}
					else if (c.nodeType == 3) {
						var s = c.nodeValue.strip();
						if (s.length > 0) {
							chunks.push('<p>' + s + '</p>');
						}
					}
				}
				if (chunks.length == 0) {
					chunks.push('<p></p>');
				}
				this.content.updateTextChunks(this.key, chunks);
			}
			else {
				this.content.updateText(this.key, text, true);
			}
		}
	},
	
	cleanUp: function(html) {
 		return html.replace(/<!--(.|\n)*?-->/g, '')
 			.replace(/&lt;!--(.|\n)*?(smso-|@page)(.|\n)*?--&gt;/g, '')
 			.replace(/<p>(\s|&nbsp;)*<\/p>/g, '')
 			.replace(/<\s*?br\s*?>/ig, '<br />')
 			.strip();
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
		body.style.backgroundImage = 'url(' + riot.Resources.resolveUrl(bgImage) + ')';
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

