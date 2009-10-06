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
		e.unselectable = 'on';
		if (className) e.className = className;
		if (parent) parent.appendChild(e);
		return Element.extend(e);
	},
	appendButton: function(parent, className, label, handler) {
		var e = new Element('input', {type: 'button', value: label}).addClassName(className);
		e.onclick = handler;
		if (parent) parent.insert(e);
		return e;
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
		this.cropUrl = this.resizeable = cropUrl;
		var o = this.options = Object.extend({
			previewWidth: 263,
			previewHeight: 100
		}, options || {});

		this.canvas = Cropper.appendDiv(this.element, 'canvas');
		this.canvasSize = new Cropper.Pos(o.canvasWidth || o.maxWidth || o.previewWidth,
				o.canvasHeight || o.maxHeight || o.previewHeight);

		this.preview = Cropper.appendDiv(this.canvas, 'preview').setStyle({
			MozUserSelect: 'none',	overflow: 'hidden',	position: 'relative'
		});
		Cropper.appendDiv(this.preview, 'mask');		

		this.controls = Cropper.appendDiv(this.element, 'controls no-crop');
		this.resizeHandle = Cropper.appendDiv(this.preview, 'resizeHandle').setStyle({
			position: 'absolute', bottom: 0, right: 0, zIndex: 100,	cursor: 'se-resize', overflow: 'hidden'
		});
		Event.observe(this.resizeHandle, 'mousedown', this.onMouseDownResize.bindAsEventListener(this));

		this.zoomElement = Cropper.appendDiv(this.controls, 'zoom');
		this.zoomTrack = Cropper.appendDiv(this.zoomElement, 'zoomTrack');

		if (this.cropUrl) {
			this.cropEnabled = true;
			var e;

			// Size selectors:
			if (o.minWidth || o.maxWidth || o.minHeight || o.maxHeight) {
				e = Cropper.appendDiv(this.controls, 'sizeSelectors');

				// Width
				if (o.widths) {
					this.widthSelector = this.createSizeSelector(o.widths, 'setWidth');
					e.appendChild(this.widthSelector);
					if (o.heights) {
						this.resizeable = false;
					}
				}
				else {
					e.appendChild(this.createSizeLabel(o.minWidth, o.maxWidth));
				}

				// Times
				var x = document.createElement('span');
				x.className = 'times';
				x.innerHTML = '&times;';
				e.appendChild(x);

				// Height
				if (o.heights) {
					this.heightSelector = this.createSizeSelector(o.heights, 'setHeight');
					e.appendChild(this.heightSelector);
				}
				else {
					e.appendChild(this.createSizeLabel(o.minHeight, o.maxHeight));
				}

			}

			// Buttons:
			e = this.cropButton = Cropper.appendButton(this.controls, 
					'crop', o.cropLabel || 'Crop', this.crop.bind(this));

			e = this.undoButton = Cropper.appendButton(this.controls, 
					'undo', o.undoLabel || 'Undo', this.undo.bind(this)).hide();
		}

		this.elementPos = Cropper.elementPos(this.element);
		this.mousePos = new Cropper.Pos();
		this.click = new Cropper.Pos();
		this.offset = new Cropper.Pos();

		this.img = document.createElement('img');
		this.img.style.position = 'absolute';
		this.img.unselectable = 'on';
		this.img.onload = this.onLoadImage.bind(this);
		this.preview.appendChild(this.img);

		var zoomHandler = this.zoom.bind(this);
		var handle = Cropper.appendDiv(this.zoomTrack, 'zoomHandle');

		var zoomSlider = this.zoomSlider = new Control.Slider(handle, this.zoomTrack, {
			range: $R(0, 1),
			onSlide: zoomHandler,
			onChange: zoomHandler
		});

		// Patch to support rescaling:
		zoomSlider.translateToPx = function(value) {
			var range = this.range.end-this.range.start;
			if (range == 0) return 0;
			return Math.round(((this.trackLength-this.handleLength)/range) * (value - this.range.start)) + 'px';
		}
		zoomSlider.rescale = function(min, handleSize) {
			this.range.start = min;
			this.handleLength = handleSize;
			this.handles[0].style.width = handleSize + 'px';
			this.setValueBy(0);
		}

		// Enable zooming using the mouse wheel:
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

		this.setCanvasSize(this.canvasSize);
		this.setImage(src);
	},

	createSizeSelector: function(values, setter) {
		var sel;
		if (values.length > 1) {
			sel = document.createElement('select');
			values.each(function(s) {
				var option = document.createElement('option');
				option.value = s;
				option.innerHTML = s + ' px';
				sel.appendChild(option);
			});
			sel.onchange = this.setSizeFromSelect.bind(this, sel, setter);
		}
		else {
			sel = document.createElement('span');
			sel.innerHTML = values[0] + ' px';
		}
		return sel;
	},

	createSizeLabel: function(minValue, maxValue) {
		var label = document.createElement('span');
		label.innerHTML = (minValue || '1') + '-' + (maxValue ||  '&infin;') + ' px';
		return label;
	},

	getMaxFromSelector: function(sel, min, max) {
		var initialValue;
		if (sel) {
			var opts = sel.options;
			if (opts) {
				for (var i = 0; i < opts.length; i++) {
					var v = parseInt(opts[i].value);
					opts[i].disabled = v < min || v > max;
					if (v > (initialValue || 0) && !opts[i].disabled) {
						initialValue = v;
						opts[i].selected = true;
					}
				}
			}
			else {
				initialValue = parseInt(sel.innerHTML);
			}
		}
		return initialValue;
	},

	setImage: function(src) {
		var present = (typeof src == 'string') && src != '';
		if (present) {
			this.img.style.width = 'auto';
			this.img.src = src;
		}

		this.setCrop(false);
		this.setCrop(present && this.cropUrl);
		Cropper.toggle(this.img, present);
		Cropper.toggle(this.zoomTrack, present);
		Cropper.toggle(this.resizeHandle, present && this.resizeable);
	},

	setCanvasSize: function(size) {
		size.applySize(this.canvas);
		this.zoomElement.style.width = this.canvas.offsetWidth + 'px';
		this.element.style.width = 'auto';
		this.element.style.height = 'auto';
		this.zoomSlider.trackLength = this.canvas.offsetWidth;
	},

	onLoadImage: function() {
		this.imageSize = new Cropper.Pos(this.img.width, this.img.height);

		// Make sure min and max are not greater than the image dimrensions:
		this.min = new Cropper.Pos(this.options.minWidth, this.options.minHeight);
		this.min.keepWithin(0, 0, this.imageSize.x, this.imageSize.y);
		this.max = new Cropper.Pos(this.options.maxWidth || this.canvasSize.x, this.options.maxHeight || this.canvasSize.y);
		this.max.keepWithin(0, 0, this.imageSize.x, this.imageSize.y);

		this.zoomSlider.range.end = this.imageSize.x;
		this.setSize(this.max);

		var initialWidth = this.getMaxFromSelector(this.widthSelector, this.min.x, this.max.x);
		if (initialWidth) this.setWidth(initialWidth);

		var initialHeight = this.getMaxFromSelector(this.heightSelector, this.min.y, this.max.y);
		if (initialHeight) this.setHeight(initialHeight);

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

		var minWidth = this.cropUrl ? Math.max(size.x, Math.ceil(this.imageSize.x * (size.y / this.imageSize.y))) : size.x;
		if (this.imageSize.x >= minWidth) {
			var handleSize = Math.round(this.zoomSlider.trackLength * (minWidth / this.imageSize.x));
			this.zoomSlider.rescale(minWidth, handleSize);
		}
	},

	setWidth: function(width) {
		this.min.x = this.max.x = width;
		this.setSize(new Cropper.Pos(width, this.preview.offsetHeight));
	},

	setHeight: function(height) {
		this.min.y = this.max.y = height;
		this.setSize(new Cropper.Pos(this.preview.offsetWidth, height));
	},

	setSizeFromSelect: function(sel, setter) {
		if (this.imageSize) {
			this[setter](sel.options[sel.selectedIndex].value);
		}
	},

	zoom: function(newWidth) {
		if (isNaN(newWidth)) return;
		var scale = newWidth / this.img.width;
		var originalScale = newWidth / this.imageSize.x;
		var newHeight = Math.round(this.imageSize.y * originalScale);
		newWidth = Math.round(newWidth);
		
		if (this.mode != 'resize') {
			this.elementPos = Cropper.elementPos(this.element);
			var center = this.zoomToPointer
					? new Cropper.Pos(this.mousePos.x - this.elementPos.x, this.mousePos.y - this.elementPos.y)
					: new Cropper.Pos(this.preview.offsetWidth / 2, this.preview.offsetHeight / 2);

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
		this.elementPos = Cropper.elementPos(this.element);
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
		this.mode = null;
		if (document.all) {
			this.resizeHandle.style.cursor = 'se-resize';
		}
		this.img.style.cursor = this.img.width > this.preview.offsetWidth || this.img.height > this.preview.offsetHeight ? 'move' : 'auto';
		document.body.style.cursor = 'auto';
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
			if (this.cropButton) this.cropButton.disabled = false;
		}
		else if (!enabled && this.cropEnabled) {
			this.cropEnabled = false;
			this.controls.addClassName('no-crop');
			if (this.cropButton) this.cropButton.disabled = true;
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
		if (this.options.onCrop) this.options.onCrop(this);
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
		if (this.options.undoUrl) {
			new Ajax.Request(this.options.undoUrl, { method: 'get'});
		}
		this.setCrop(true);
		this.undoButton.hide();
		this.cropButton.show();
		this.zoomTrack.show();
		if (this.resizeable) this.resizeHandle.show();
		this.preview.removeChild(this.croppedImg);
		this.preview.appendChild(this.img);
		if (this.options.onUndo) this.options.onUndo(this);
	}

}
