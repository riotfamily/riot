var Viewport = {};

Viewport.getInnerHeight = function() {
	if (!w) var w = window;
	if (w.innerHeight) {
		// all except Explorer
		return w.innerHeight;
	} 
	var d = w.document;
	if (d.documentElement && d.documentElement.clientHeight) { 
		// Explorer 6 Strict Mode
		return d.documentElement.clientHeight;
	} 
	// other Explorers
	return d.body.clientHeight;
}

Viewport.getInnerWidth = function(w) {
	if (!w) var w = window;
	if (w.innerWidth) { 
		// all except Explorer
		return w.innerWidth;
	}
	var d = w.document;
	if (d.documentElement && d.documentElement.clientWidth) {
		// Explorer 6 Strict Mode
		return d.documentElement.clientWidth;
	}
	// other Explorers
	return d.body.clientWidth; 
}

Viewport.getPageHeight = function(w) {
	var bodyHeight = Viewport.getBodyHeight(w);
	var windowHeight = Viewport.getInnerHeight(w);
	return bodyHeight < windowHeight ? windowHeight : bodyHeight;
}

Viewport.getBodyHeight = function(w) {
	if (!w) var w = window;
	var d = w.document;
	if (w.innerHeight && w.scrollMaxY) {	
		return w.innerHeight + w.scrollMaxY;
	} 
	if (d.body.scrollHeight > d.body.offsetHeight) { 
		// all but Explorer Mac
		return d.body.scrollHeight;
	} 
	// Explorer Mac... would also work in Explorer 6 Strict, Mozilla and Safari
	return d.body.offsetHeight;
}

Viewport.getScrollTop = function() {
	if (self.pageYOffset) {
		  return self.pageYOffset;
	}
	else if (document.documentElement && document.documentElement.scrollTop) {
		return document.documentElement.scrollTop;
	}
	return document.body.scrollTop;
}

Viewport.getScrollLeft = function() {
	if (self.pageXOffset) {
		  return self.pageXOffset;
	}
	else if (document.documentElement && document.documentElement.scrollLeft) {
		return document.documentElement.scrollLeft;
	}
	return document.body.scrollLeft;
}
	
Viewport.center = function(el) {
	var top = Math.round(Viewport.getInnerHeight() / 2 - el.clientHeight / 2);
	var left = Math.round(Viewport.getInnerWidth() / 2 - el.clientWidth / 2);
	if (Element.getStyle(el, 'position') != 'fixed') {
		top += Viewport.getScrollTop();
		left += Viewport.getScrollLeft();
	}
	el.style.top = top + 'px';
	el.style.left = left + 'px';
}