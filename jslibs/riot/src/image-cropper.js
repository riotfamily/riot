var Cropper = {
	elementPos: function(el) {
		var p = Position.positionedOffset(el);
		return new Cropper.Pos(p[0], p[1]);
	},
	toggle: function(el, visible) {
		Element[visible ? 'show' : 'hide'](el);
	},
	appendDiv: function(parent, className) {
		var e = document.createElement('div');
		if (className) e.className = className;
		if (parent) parent.appendChild(e);
		return Element.extend(e);
	}
};
Cropper.Pos = Class.create();
Cropper.Pos.prototype = {
	initialize: function(x, y) {
		this.x = Math.round(x || 0); 
		this.y = Math.round(y || 0);
	},

	setFromMouse: function(event) {
		this.x = Event.pointerX(event); this.y = Event.pointerY(event);
	},

	mouseDelta: function(event) {
		return new Cropper.Pos(Event.pointerX(event) - this.x, Event.pointerY(event) - this.y);
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
}

Cropper.UI = Class.create();
Cropper.UI.prototype = {
	initialize: function(el, src, cropUrl, options) {
		this.element = $(el).addClassName('cropper');
		this.cropUrl = cropUrl;
		var o = this.options = options || {};

		o.minWidth = o.minWidth || o.width || 0;
		o.maxWidth = o.maxWidth || o.width;

		o.minHeight = o.minHeight || o.height || 0;
		o.maxHeight = o.maxHeight || o.height;
		
		this.canvas = Cropper.appendDiv(this.element, 'canvas');		
		this.preview = Cropper.appendDiv(this.canvas, 'preview').setStyle({
			MozUserSelect: 'none',	overflow: 'hidden',	position: 'relative'
		});
		
		this.controls = Cropper.appendDiv(this.element, 'controls no-crop').setStyle({width: this.canvas.offsetWidth + 'px'});
		this.resizeHandle = Cropper.appendDiv(this.preview, 'resizeHandle').setStyle({
			position: 'absolute', bottom: 0, right: 0, zIndex: 100,	cursor: 'se-resize'
		});
		Event.observe(this.resizeHandle, 'mousedown', this.onMouseDownResize.bindAsEventListener(this));

		var zoom = Cropper.appendDiv(this.controls, 'zoom');
		this.zoomTrack = Cropper.appendDiv(zoom, 'zoomTrack');

		if (this.cropUrl) {
			this.cropEnabled = true;
			var e = this.cropButton = Cropper.appendDiv(this.controls, 'cropButton');
			e.appendChild(document.createTextNode(o.cropLabel || 'Crop'));
			e.onclick = this.crop.bind(this);

			e = this.undoButton = Cropper.appendDiv(this.controls).hide();
			e.className = 'undoButton';
			e.appendChild(document.createTextNode(o.undoLabel || 'Undo'));
			e.onclick = this.undo.bind(this);
		}

		this.elementPos = Cropper.elementPos(this.element);
		this.mousePos = new Cropper.Pos();
		this.click = new Cropper.Pos();
		this.offset = new Cropper.Pos();

		this.img = document.createElement('img');
		this.img.style.position = 'absolute';
		this.img.onload = this.onLoadImage.bind(this);
		this.preview.appendChild(this.img);

		var zoomHandler = this.zoom.bind(this);
		var handle = Cropper.appendDiv(this.zoomTrack, 'zoomHandle');

		var zoomSlider = this.zoomSlider = new Control.Slider(handle, this.zoomTrack, {
			range: $R(0, 1),
			onSlide: zoomHandler,
			onChange: zoomHandler
		});
		zoomSlider.rescale = function(min, handleSize) {
			this.range.start = min;
			this.handleLength = handleSize;
			this.handles[0].style.width = handleSize + 'px';
			this.setValueBy(0);
		}
		zoomSlider.translateToPx = function(value) {
			var range = this.range.end-this.range.start;
			if (range == 0) return 0;
			return Math.round(((this.trackLength-this.handleLength)/range) * (value - this.range.start)) + 'px';
		}

		if (this.img.addEventListener) {
			this.img.addEventListener("DOMMouseScroll", this.onMouseWheel.bindAsEventListener(this), true);
		}
		else if (this.img.attachEvent) {
			this.img.attachEvent("onmousewheel", this.onMouseWheel.bindAsEventListener(this));
		}

		Event.observe(this.img, 'mousedown', this.onMouseDown.bindAsEventListener(this));
		Event.observe(document, 'mousemove', this.onMouseMove.bindAsEventListener(this));
		Event.observe(document, 'mouseup', this.onMouseUp.bindAsEventListener(this));
		Event.observe(window, 'mouseout', this.onMouseOut.bindAsEventListener(this));

		this.setImage(src);
	},
	
	setImage: function(src) {
		var present = src && src != '';
		if (present) {		
			this.img.style.width = 'auto';
			this.img.src = src;
		}
		else if (!this.img.src) {
			this.setCanvasSize(new Cropper.Pos(this.options.maxWidth || 150, this.options.maxHeight || 100));
		}
		this.setCrop(false);
		this.setCrop(present);
		Cropper.toggle(this.img, present);
		Cropper.toggle(this.zoomTrack, present);
		Cropper.toggle(this.resizeHandle, present);
	},

	setCanvasSize: function(size) {
		size.applySize(this.canvas);
		this.element.style.width = this.canvas.offsetWidth + 'px';
		this.element.style.height = 'auto';
		this.zoomSlider.trackLength = this.canvas.offsetWidth;
	},

	onLoadImage: function() {
		this.imageSize = new Cropper.Pos(this.img.width, this.img.height);
		this.min = new Cropper.Pos(this.options.minWidth, this.options.minHeight);
		this.max = new Cropper.Pos(this.options.maxWidth || this.imageSize.x, this.options.maxHeight || this.imageSize.y);
		this.setCanvasSize(this.max);
		this.max.keepWithin(0, 0, this.imageSize.x, this.imageSize.y);
		this.zoomSlider.range.end = this.imageSize.x;
		this.setSize(this.max);		
		this.zoomSlider.setValue(this.imageSize.x);
	},

	onMouseWheel: function(event) {
		var delta;
		if (event.wheelDelta) {
       		delta = -event.wheelDelta / 40;
	    }
		else {
       		delta = event.detail || 0;
			if (delta < -3) delta = -3;
			if (delta > 3) delta = 3;
    	}
		this.zoomToPointer = true;
		this.zoomSlider.setValueBy(this.imageSize.x / 100 * delta);
		this.zoomToPointer = false;
		Event.stop(event);
	},

	setSize: function(size) {
		size.keepWithin(this.min.x, this.min.y, this.max.x, this.max.y);
		size.applySize(this.preview);
		
		this.offset.keepWithin(0, 0, this.img.width - size.x, this.img.height - size.y);
		this.offset.applyOffset(this.img);
		
		this.setCrop(size.x < this.imageSize.x || size.y < this.imageSize.y);

		var minWidth = Math.max(size.x, Math.ceil(this.imageSize.x * (size.y / this.imageSize.y)));
		var handleSize = Math.round(this.zoomSlider.trackLength * (minWidth / this.imageSize.x));
		this.zoomSlider.rescale(minWidth, handleSize);
	},

	zoom: function(newWidth) {
		if (isNaN(newWidth)) return;
		newWidth = Math.round(newWidth);
		var scale = newWidth / this.img.width;
		var newHeight = this.img.height * scale;

		if (this.mode != 'resize') {
			var center = this.zoomToPointer
					? new Cropper.Pos(this.mousePos.x - this.elementPos.x, this.mousePos.y - this.elementPos.y)
					: new Cropper.Pos(this.preview.offsetWidth / 2, this.preview.offsetHeight / 2);

			var g = new Cropper.Pos(this.offset.x + center.x, this.offset.y + center.y);
			this.offset.moveBy(g.x * scale - g.x, g.y * scale - g.y);
		}
		this.offset.keepWithin(0, 0, newWidth - this.preview.offsetWidth, newHeight - this.preview.offsetHeight);
		this.offset.applyOffset(this.img);
		this.img.style.width = newWidth + 'px';
	},

	onMouseDown: function(event) {
		this.mode = 'pan';
		Event.stop(event);
	},

	onMouseDownResize: function(event) {
		this.mode = 'resize';
		this.img.style.cursor = document.body.style.cursor = this.resizeHandle.style.cursor;
		Event.stop(event);
	},

	onMouseUp: function(event) {
		this.mode = null;
		this.img.style.cursor = this.img.width > this.preview.offsetWidth || this.img.height > this.preview.offsetHeight ? 'move' : 'default';
		document.body.style.cursor = 'default';
	},

	onMouseOut: function(event) {
		if (!event.relatedTarget) {
            this.onMouseUp(event);
        }
	},

	onMouseMove: function(event) {
		if (this.mode == 'resize') {
			this.setSize(this.elementPos.mouseDelta(event));
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

	setCrop: function(enabled) {
		if (enabled && !this.cropEnabled) {
			this.cropEnabled = true;
			this.controls.removeClassName('no-crop');
		}
		else if (!enabled && this.cropEnabled) {
			this.cropEnabled = false;
			this.controls.addClassName('no-crop');
		}
	},

	crop: function() {
		if (this.cropEnabled) {
			this.setCrop(false);
			this.resizeHandle.hide();
			this.zoomTrack.hide();
			new Ajax.Request(this.cropUrl, {
				method: 'get', 
				parameters: {
					width: this.preview.offsetWidth,
					height: this.preview.offsetHeight,
					x: this.offset.x,
					y: this.offset.y,
					scaledWidth: this.img.width
				},
				onComplete: this.handleResponse.bind(this)
			});
		}
	},

	handleResponse: function(response) {
		this.showCroppedImage(response.responseText);
	},
	
	showCroppedImage: function(src) {
		this.preview.removeChild(this.img);
		this.croppedImg = document.createElement('img');
		this.croppedImg.src = src;
		this.preview.appendChild(this.croppedImg);
		this.cropButton.hide();
		this.undoButton.show();
	},

	undo: function() {
		this.setCrop(true);
		this.undoButton.hide();
		this.cropButton.show();
		this.zoomTrack.show();
		this.resizeHandle.show();
		this.preview.removeChild(this.croppedImg);
		this.preview.appendChild(this.img);
	}

}
