var RiotImageReplacement = Class.create();
RiotImageReplacement.prototype = {
	initialize: function(imageUrl, selectors) {
		this.selectors = selectors;
		this.imageUrl = imageUrl;
		Event.onDOMReady(this.insertImages.bind(this));
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
		var img = new Image();
		img.onload = this.insertImage.bind(this, el, img);
		img.alt = text;
		img.src = this.imageUrl + '?text=' + escape(text) 
				+ '&width=' + el.offsetWidth + '&selector=' + escape(sel)
				+ '&color=' + escape(el.getStyle('color'));
	},
	
	insertImage: function(el, image) {
		var img;
		var useFilter = false;
		/*@cc_on
		/*@if (@_jscript_version < 5.7) 
			useFilter = true;
		/*@end
		@*/
		if (useFilter) {
			img = document.createElement('div');
			img.style.border = '1px solid lime';
			img.style.width = image.width + 'px';
            img.style.height = image.height + 'px';
			img.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" 
				+ image.src + "', sizingMethod='scale')";
		}
		else {
			img = document.createElement('img');
			img.src = image.src;
		}
		img.style.className = 'replacement';
		
		var printText = document.createElement("span");
		printText.style.display = 'none';
		printText.className = "print-text";
		printText.innerHTML = el.innerHTML;
		
		el.update();
		el.appendChild(img);
		el.appendChild(printText);
	}
	
}

Object.extend(Event, {
  _domReady : function() {
    if (arguments.callee.done) return;
    arguments.callee.done = true;

    if (this._timer)  clearInterval(this._timer);
    
    this._readyCallbacks.each(function(f) { f() });
    this._readyCallbacks = null;
},
  onDOMReady : function(f) {
    if (!this._readyCallbacks) {
      var domReady = this._domReady.bind(this);
      
      if (document.addEventListener)
        document.addEventListener("DOMContentLoaded", domReady, false);
        
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

