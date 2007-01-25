var TweakStyle = {

	createCorner: function(pos) {
		var c = document.createElement('div');
		Element.setStyle(c, {
			width: '1px',
			height: '1px',
			overflow: 'hidden',
			position: 'absolute'
		});
		c.className = 'corner-' + pos;
		if (pos.indexOf('b') != -1) {  c.style.bottom = 0; }
		else { c.style.top = 0; }
		if (pos.indexOf('r') != -1) { c.style.right = 0; }
		else { c.style.left = 0; }
		c.style.backgroundColor = '#fff';
		return c;
	},
	
	roundElement: function(el, corners) {
		el = $(el);
		if (el) {
			if (corners == 'all') corners = 'tl,tr,bl,br';
			corners = corners || 'tl,tr';
			el.style.position = 'relative';
			var c = corners.split(',');
			for (var i = 0; i < c.length; i++) {
				el.appendChild(this.createCorner(c[i]));
			}
		}
	},
	
	roundElements: function(tagName, className, corners) {
		var tags = document.getElementsByTagName(tagName);
		for (var i = 0; i <  tags.length; i++) {
			if (Element.hasClassName(tags[i], className)) {
				this.roundElement(tags[i], corners);
			}
		}
	},
	
	styleButton: function(button, beforeClick) {
		button = $(button);
		button.style.display = 'none';
		var a = document.createElement('a');
		a.className = button.className;
		a.href = '#';
		a.onclick = function() {
			if (beforeClick) beforeClick();
			button.click();
			return false;
		}
		
		var face = document.createElement('div');
		face.className = 'face';
		face.innerHTML = button.value;
		a.appendChild(face);
		
		button.parentNode.insertBefore(a, button);
		this.roundElement(a, 'all');
	},

	login: function() {
		this.styleButton('submit');
		this.roundElement('tip-of-the-day', 'all');
	},
		
	path: function() {
		parent.frameset.resizeFrame(window);
		this.roundElement('path');
	},
	
	group: function() {
		this.roundElements('div', 'title');
	},
	
	status: function() {
		var div = $('panel');
		div.style.width = '100%';
		this.roundElement(div);
		parent.frameset.resizeFrame(window);
	},
	
	form: function() {
		this.roundElements('div', 'title');
		var beforeSave;
		if (typeof showSavingMessage != 'undefined') {
			beforeSave = showSavingMessage;
		}
		tags = document.getElementsByTagName('input');
		for (var i = 0; i < tags.length; i++) {
			if (Element.hasClassName(tags[i], 'button-save')) {
				this.styleButton(tags[i], beforeSave);
			}
		} 
		this.roundElement('form-options', 'all');
	},
	
	list: function() {
		this.roundElements('div', 'title');
		this.roundElement('list');
	}
	
}