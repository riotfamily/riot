var RiotImageReplacement = Class.create();
RiotImageReplacement.prototype = {
	initialize: function(generatorUrl, pixelUrl, selectors) {
		this.selectors = selectors;
		this.generatorUrl = generatorUrl;
		this.useFilter = false;
		/*@cc_on
		/*@if (@_jscript_version < 5.7)
			this.useFilter = true;
		/*@end
		@*/
		this.pixelImage = new Image();
		this.pixelImage.src = pixelUrl;
		this.createHoverRules();
		Event.onDOMReady(this.insertImages.bind(this));
		if (window.riotEditCallbacks) {
			addRiotEditCallback(this.insertImages.bind(this));
		}
	},

	createHoverRules: function() {
		$A(document.styleSheets).each(function(sheet) {
		    $A(sheet.rules || sheet.cssRules).each(function(rule) {
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
		    });
		});
	},

	insertImages: function(el) {
		for (var i = 0, len = this.selectors.length; i < len; i++) {
			this.processSelector(this.selectors[i], el);
		}
	},

	processSelector: function(sel, el) {
		var elements = new Selector(sel).findElements(el);
		for (var i = 0, len = elements.length; i < len; i++) {
			this.processElement(sel, elements[i]);
		}
	},

	processElement: function(sel, el) {
		if (el.down('img.replacement') || el.className == 'print-text') {
			return;
		}
		el.onedit = this.processElement.bind(this, sel, el);
		var text = el.innerHTML;
		text = text.strip().gsub(/<br\/?>/i, '\n').stripTags();
		var transform = el.getStyle('text-transform') || '';
		var width = 0;
		if (el.getStyle('display') == 'block') {
			width = el.offsetWidth - parseInt(el.getStyle('padding-left'))
					- parseInt(el.getStyle('padding-right'));
		}

		var color = el.getStyle('color');

		var hoverEl = $(document.createElement('span'));
		hoverEl.className = 'txt2imgHover';
		el.appendChild(hoverEl);
		var hoverColor = hoverEl.getStyle('color');
		hoverEl.remove();

		var hover = null;
		if (hoverColor != color) {
			hover = new Image();
			hover.src = this.getImageUrl(text, transform, width, sel, hoverColor);
		}

		var img = new Image();
		img.onload = this.insertImage.bind(this, el, img, hover);
		img.src = this.getImageUrl(text, transform, width, sel, color);
	},

	getImageUrl: function(text, transform, width, sel, color) {
		var url = this.generatorUrl;
		url += url.include('?') ? '&' : '?';
		return url + 'text=' + this.base64(text) + '&transform=' + transform
				+ '&width=' + width + '&selector=' + this.base64(sel)
				+ '&color=' + this.base64(color);
	},

	base64: function(s) {
		// We have to use Base64 encoding because the AlphaImageLoader decodes
		// correctly encoded URIs and converts %23 back to # (and %26 back to &)
		// thereby corrupting the URL.
		var chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
		var c1, c2, c3;
		var e1, e2, e3, e4;
		var result = '';
		for (var i = 0, len = s.length; i < len;) {
		   c1 = s.charCodeAt(i++);
		   c2 = s.charCodeAt(i++);
		   c3 = s.charCodeAt(i++);
		   e1 = c1 >> 2;
		   e2 = ((c1 & 3) << 4) | (c2 >> 4);
		   e3 = ((c2 & 15) << 2) | (c3 >> 6);
		   e4 = c3 & 63;
		   if (isNaN(c2)) {
		      e3 = e4 = 64;
		   }
		   else if (isNaN(c3)) {
		      e4 = 64;
		   }
		   result = result + chars.charAt(e1) + chars.charAt(e2) + chars.charAt(e3) + chars.charAt(e4);
		}
		return encodeURIComponent(result);
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

	insertImage: function(el, image, hover) {
		var img;
		img = document.createElement('img');
		img.style.verticalAlign = 'top';
		if (this.useFilter) {
			img.src = this.pixelImage.src;
			img.style.width = image.width + 'px';
			img.style.height = image.height + 'px';
		}
		this.setImageSrc(img, image.src);
		img.className = 'replacement';

		if (hover) {
			img.onmouseover = this.setImageSrc.bind(this, img, hover.src);
			img.onmouseout = this.setImageSrc.bind(this, img, image.src);
		}

		var printText = document.createElement("span");
		printText.style.display = 'none';
		printText.className = "print-text";
		printText.innerHTML = el.innerHTML;

		el.update();
		el.appendChild(img);
		el.appendChild(printText);
	}
}

if (!Event.onDOMReady) {
	Object.extend(Event, {
		_domReady: function() {
		    if (arguments.callee.done) return;
		    arguments.callee.done = true;
		    if (this._timer) clearInterval(this._timer);
		    for (var i = 0; i < this._readyCallbacks.length; i++) {
		    	this._readyCallbacks[i]();
		    }
		    this._readyCallbacks = null;
		},
		onDOMReady: function(f) {
			if (!this._readyCallbacks) {
				var domReady = this._domReady.bind(this);
				if (document.addEventListener) {
		        	document.addEventListener("DOMContentLoaded", domReady, false);
				}
				/*@cc_on @*/
				/*@if (@_win32)
				    document.write("<script id=__ie_onload defer src=javascript:void(0)><\/script>");
				    document.getElementById("__ie_onload").onreadystatechange = function() {
				        if (this.readyState == "complete") domReady();
				    };
				/*@end @*/
		        if (/WebKit/i.test(navigator.userAgent)) {
					this._timer = setInterval(function() {
						if (/loaded|complete/.test(document.readyState)) domReady();
					}, 10);
		        }
				Event.observe(window, 'load', domReady);
				Event._readyCallbacks =  [];
		    }
			Event._readyCallbacks.push(f);
		}
	});
}
