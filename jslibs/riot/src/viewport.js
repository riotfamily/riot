var Viewport = {};

Viewport.getInnerHeight = function() {
	if (self.innerHeight) { return self.innerHeight; }
	if (document.documentElement && document.documentElement.clientHeight) {
		return document.documentElement.clientHeight;
	}
	if (document.body) { return document.body.clientHeight; }
}

Viewport.getInnerWidth = function() {
	if (self.innerWidth) { return self.innerWidth; }
	if (document.documentElement && document.documentElement.clientWidth) {
		return document.documentElement.clientWidth;
	}
	if (document.body) { return document.body.clientWidth; }
}

Viewport.getPageHeight = function() {
	var b = document.body;
	var bodyHeight = Math.max(b.scrollHeight, b.offsetHeight);
	return Math.max(bodyHeight, this.getInnerHeight());
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