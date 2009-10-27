/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
 
if (typeof Prototype=='undefined') {
	throw("Txt2img requires the Prototype JavaScript library");
}

var Txt2ImgConfig = Class.create({

	initialize: function(generatorUrl, pixelUrl, selectors) {
		this.generatorUrl = generatorUrl;
		this.pixelImage = new Image();
		this.pixelImage.src = pixelUrl;
		this.selectors = selectors;
		try {
			this.createHoverRules();
		}
		catch(exception) {
		}
		document.observe('dom:loaded', this.insertImages.bind(this));
		document.observe('component:updated', this.insertImages.bind(this));
	},

	createHoverRules: function() {
		var processRule = function(sheet, rule) {
			if (rule.selectorText) {
				rule.selectorText.split(',').each(function(sel) {
					if (sel.include(':hover')) {
						if (rule.style.color) {
							var newSel = sel.replace(/:hover/, ' .txt2imgHover');
							var newStyle = 'color: ' + rule.style.color;
							if (sheet.insertRule) {
								sheet.insertRule(newSel + ' {' + newStyle + '}', sheet.cssRules.length);
							}
							else if (sheet.addRule) {
								sheet.addRule(newSel, newStyle);
							}
						}
					}
				});
			}
			else {
				if (rule.cssRules) {
					var a = $A(rule.cssRules);
					for (var i = 0; i < a.length; i++) {
						processRule(sheet, a[i]);
					}
				}
			}
		};
		$A(document.styleSheets).each(function(sheet) {
			var a = $A(sheet.rules || sheet.cssRules);
			for (var i = 0; i < a.length; i++) {
				processRule(sheet, a[i]);
			}
		});
	},

	insertImages: function() {
		new CssMatcher(this.selectors, this.processElement.bind(this)).match(document.body);
	},
	
	processElement: function(el, sel) {
		if (el.className == 'print-text') return;
		if (!el.txt2img) {
			el.txt2img = new Txt2ImgReplacement(this, el, sel);
		}
		el.txt2img.replace();
	}
	
});

var Txt2ImgReplacement = Class.create({
	
	initialize: function(config, el, sel) {
		this.config = config;
		this.el = $(el);
		this.sel = sel;
		this.el.observe('inplace:edited', this.replace.bind(this));
	},
	
	// Whether to use the alphaImageLoader or not (detects IE 6):
	useFilter: Prototype.Browser.IE && typeof document.documentElement.style.maxHeight == 'undefined',
	
	replace: function() {
		if (!this.el.down('img.replacement')) {
			this.updateText();
		}
	},
		
	updateText: function(text) {
		if (!text) {
			text = this.el.innerHTML.strip().gsub(/<br\/?>/i, '\n').stripTags();
		}
		this.text = text;
		this.update();
	},
	
	updateSelector: function() {
		this.sel = this.config.selectors.find(this.el.match.bind(this.el));
		this.update();
	},
	
	update: function() {
		if (this.text.length > 0) {
			var transform = this.el.getStyle('text-transform') || '';
			var width = 0;
			var display = this.el.getStyle('display');
			var isFloating = this.el.getStyle('float') != 'none';
			if ((display == 'block' && !isFloating) || display == 'inline-block') {
				width = this.el.offsetWidth - parseInt(this.el.getStyle('padding-left'))
						- parseInt(this.el.getStyle('padding-right'));
			}
	
			var color = this.el.getStyle('color');
			var hoverColor = this.getHoverColor();
	
			var hover = null;
			if (hoverColor != color) {
				hover = new Image();
				hover.src = this.getImageUrl(transform, width, hoverColor, true);
			}
			
			var img = new Image();
			img.onload = this.insertImage.bind(this, img, hover);
			img.src = this.getImageUrl(transform, width, color);
		}
	},
	
	getHoverColor: function() {
		var hoverEl = document.createElement('span');
		hoverEl.className = 'txt2imgHover';
		this.el.appendChild(hoverEl);
		var hoverColor = Element.getStyle(hoverEl, 'color');
		Element.remove(hoverEl);
		return hoverColor;
	},
	
	getImageUrl: function(transform, width, color, hover) {
		var url = this.config.generatorUrl;
		url += url.include('?') ? '&' : '?';
		url += 'text=' + this.encode(this.text) + '&transform=' + transform
				+ '&width=' + width + '&selector=' + this.encode(this.sel)
				+ '&color=' + this.encode(color);
				
		if (hover) {
			url += '&hover=true';
		}
		return url;
	},

	encode: function(s) {
		// correctly encode non-ASCII characters - doesn't encode ~!*()'
		s = encodeURIComponent(s);
		
		// We uses escape() to escape the remaining chars. In order to prevent
		// double-escaping of %-chars, we temporarily convert them to slashes,
		// which are ignored by escape()
		s = escape(s.replace(/%/g, '/'));
		
		// We have to convert % characters because the AlphaImageLoader decodes
		// correctly encoded URIs and converts %23 back to # (and %26 back to &)
		// thereby corrupting the URL. 
		return s.replace(/[%\/]/g, '@');
	},
	
	setImageSrc: function(el, src) {
		if (this.useFilter) {
			el.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"
				+ src + "', sizingMethod='scale')";
		}
		else {
			el.src = src;
		}
	},
	
	insertImage: function(image, hover) {
		var img;
		img = document.createElement('img');
		img.style.verticalAlign = 'top';
		img.style.border = 'none';
		if (this.useFilter) {
			img.src = this.config.pixelImage.src;
			img.style.width = image.width + 'px';
			img.style.height = image.height + 'px';
		}
		this.setImageSrc(img, image.src);
		img.className = 'replacement';

		var a = this.el.up('a') || this.el;
		if (a._txt2ImgOver) a.stopObserving('mouseover', a._txt2ImgOver);
		if (a._txt2ImgOut) a.stopObserving('mouseout', a._txt2ImgOut);
		
		if (hover) {
			a._txt2ImgOver = this.setImageSrc.bind(this, img, hover.src);
			a._txt2ImgOut = this.setImageSrc.bind(this, img, image.src);
			a.observe('mouseover', a._txt2ImgOver);
			a.observe('mouseout', a._txt2ImgOut);
			a.addClassName('txt2img');
		}

		var printText = document.createElement("span");
		printText.className = "print-text";
		printText.innerHTML = this.el.innerHTML;
		this.el.update();
		this.el.appendChild(img);
		this.el.appendChild(printText);
		this.el.addClassName('txt2img');
		this.el.style.visibility = 'visible';
	}
	
});

var ElementMatcher = Class.create({
	initialize: function(s) {
		var m = /([^.#]*)#?([^.]*)\.?(.*)/.exec(s);
		this.tagName = m[1] != '' ? m[1].toUpperCase() : null;
		this.id = m[2] != '' ? m[2] : null;
		this.className = m[3] != '' ? m[3] : null;
		this.classNameRegExp = new RegExp("(^|\\s)" + this.className + "(\\s|$)");
	},

	match: function(el) {
		if (this.tagName && this.tagName != el.tagName) return false;
		if (this.id && this.id != el.id) return false;
		if (this.className && !this.checkClassName(el)) return false;
		return true;
	},

	checkClassName: function(el) {
		var c = el.className;
		if (c.length == 0) return false;
		return c == this.className || c.match(this.classNameRegExp);
	},

	inspect: function() {
			return this.tagName + '#' + this.id + '.' + this.className;
	}
});

var CssSelector = Class.create({
	initialize: function(sel) {
		this.text = sel;
		this.matchers = [];
		var part = sel.split(/\s+/);
		for (var i = 0; i < part.length; i++) {
			this.matchers.push(new ElementMatcher(part[i]));
		}
		this.level = 0;
		this.matcher = this.matchers[0];
		this.prev = [];
	},

	match: function(el) {
		if (this.matcher.match(el)) {
			if (this.el) {
				this.prev.push(this.el);
			}
			this.el = el;
			this.matcher = this.matchers[++this.level];
			return this.level == this.matchers.length;
		}
		return false;
	},

	leave: function(el) {
		if (el == this.el) {
			this.el = this.prev.pop();
			this.matcher = this.matchers[--this.level];
		}
	}
});

/**
 * Txt2img uses a custom CSS matcher instead of prototype's $$() function
 * to minimize the performance impact in IE 6. Please note that therefore 
 * CSS 3 selectors are not supported.
 */
var CssMatcher = Class.create({
	initialize: function(selectors, handler) {
		this.sel = selectors.collect(function(s) {return new CssSelector(s)});
		this.handler = handler;
		this.callback = this.processElement.bind(this);
		this.counter = 0;
	},
	
	match: function(el) {
		this.rootEl = el;
		this.el = el;
		this.stack = [];
		this.processElement();
	},
	
	nextElement: function(node) {
		while (node && (node.nodeType != 1 || node.nodeName == 'SCRIPT'
				|| node.nodeName == 'NOSCRIPT' || node.nodeName == 'OBJECT'
				|| node.nodeName == 'TEXTAREA' || node.nodeName == 'IFRAME')) {
			node = node.nextSibling;
		}
		return node;
	},
	
	processElement: function() {
		this.counter++;
		var matched = false;
		for (var i = 0; i < this.sel.length; i++) {
			if (this.sel[i].match(this.el)) {
				this.handler(this.el, this.sel[i].text);
				matched = true;
				break;
			}
		}
		var nextEl = this.nextElement(this.el.firstChild);
		if (!matched && nextEl) {
			this.el = nextEl;
			this.stack.push(nextEl);
		}
		else {
			this.stack.pop();
			for (var i = 0; i < this.sel.length; i++) {
				this.sel[i].leave(this.el);
			} 
			
			nextEl = this.nextElement(this.el.nextSibling);
			if (nextEl) {
				this.el = nextEl;
				this.stack.push(nextEl);
			}
			else {
				while (!nextEl && this.stack.length > 0) {
					var p = this.stack.pop();
					for (var i = 0; i < this.sel.length; i++) {
						this.sel[i].leave(p);
					}
					nextEl = this.nextElement(p.nextSibling);
				}
				if (nextEl) {
					this.el = nextEl;
					this.stack.push(nextEl);
				}
				else {
					return;
				}
			}
		}
		
		if (this.el) {
			// Yield execution back to the browser's renderer after 50 elements
			// in order to prevent a complete UI freeze in IE.
			if (this.counter % 50 == 0)	{
				setTimeout(this.callback, 1);
			}
			else {
				this.processElement();
			}
		}
	}
});
