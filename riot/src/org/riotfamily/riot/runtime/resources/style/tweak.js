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

	roundElements: function(selector, corners) {
		$$(selector).each(function(e) {
			TweakStyle.roundElement(e, corners);
		});
	},

	styleButton: function(button) {
		button = $(button);
		button.style.display = 'none';
		var a = document.createElement('a');
		a.className = button.className;
		a.href = '#';
		a.onclick = function() {
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
		this.roundElements('div.title');
		$$('#editors .text').each(function(el) {
			var h = el.offsetHeight - parseInt(el.getStyle('padding-top'));
			el.style.paddingTop = Math.round((el.parentNode.offsetHeight - h) / 2) + 'px';
		});
	},

	status: function() {
		var div = $('panel');
		div.style.width = '100%';
		this.roundElement(div);
		parent.frameset.resizeFrame(window);
	},

	form: function() {
		this.roundElements('div.title');
		$$('.buttons input.button').each(function(b) {
			TweakStyle.styleButton(b);
		});
		this.roundElement('form-options', 'all');
		this.roundElement('message', 'all');
	},

	list: function() {
		this.roundElements('div.title');
		this.roundElement('list');
	},

	dialogForm: function() {
		this.form();
	}

}