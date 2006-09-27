var OnePxCorner = {

	create: function(pos, color) {
		var c = document.createElement('div');
		c.style.width = '1px';
		c.style.height = '1px';
		c.style.overflow = 'hidden';
		c.style.position = 'absolute';
		c.className = 'corner-' + pos;
		if (pos.indexOf('b') != -1) {  c.style.bottom = 0; }
		else { c.style.top = 0; }
		if (pos.indexOf('r') != -1) { c.style.right = 0; }
		else { c.style.left = 0; }
		if (color) c.style.backgroundColor = color;
		return c;
	},
	
	addTo: function(el, corners, color) {
		if (!el) return;
		if (typeof el == 'string') el = document.getElementById(el);
		corners = corners || 'tl,tr';
		el.style.position = 'relative';
		var c = corners.split(',');
		for (var i = 0; i < c.length; i++) {
			el.appendChild(this.create(c[i], color));
		}
	}
	
}
