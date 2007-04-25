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
	
	insertImages: function() {
		this.selectors.each(this.processSelectors.bind(this));
	},
	
	processSelectors: function(sel) {
		var _this = this;
		$$(sel).each(this.processElement.bind(this, sel));
	},
	
	processElement: function(sel, el) {
		el.onedit = this.processElement.bind(this, sel, el);
		var text = el.innerHTML;
		if (el.getStyle('text-transform') == 'uppercase') {
			text = text.toUpperCase();
		}
		text = text.gsub(/<br\/?>/i, '\n').stripTags();
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
			hover.src = this.getImageUrl(text, width, sel, hoverColor);
		}
		
		var img = new Image();
		img.onload = this.insertImage.bind(this, el, img, hover);
		img.src = this.getImageUrl(text, width, sel, color);
	},
	
	getImageUrl: function(text, width, sel, color) {
		return this.generatorUrl + '?text=' + escape(text) 
				+ '&width=' + width + '&selector=' + escape(sel)
				+ '&color=' + escape(color);
	},
	
	setImageSrc: function(el, src) {
		if (this.useFilters) {
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
		    this._readyCallbacks.each(function(f) { f() });
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
