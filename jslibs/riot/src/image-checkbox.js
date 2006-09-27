var ImageCheckbox = Class.create();
ImageCheckbox.prototype = {
	
	initialize: function(el, className) {
		el = $(el);
		Element.hide(el);
		this.checkbox = el;
		var id = el.id;
		this.wrapper = document.createElement('div');
		el.id = id + '-checkbox';
		this.wrapper.id = id;
		this.wrapper.className = 'imageCheckbox';
		
		el.parentNode.insertBefore(this.wrapper, el);
		Element.remove(el);
		this.wrapper.appendChild(el);
		
		var image = document.createElement('div');
		image.className = className || 'image';
		this.wrapper.appendChild(image);
		
		if (this.checkbox.checked) {
			Element.addClassName(this.wrapper, 'checked');
		}
		image.onclick = this.handleClick.bindAsEventListener(this);
	},
	
	handleClick: function(e) {
		this.checkbox.checked = !this.checkbox.checked;
		if (this.checkbox.checked) {
			Element.addClassName(this.wrapper, 'checked');
		}
		else {
			Element.removeClassName(this.wrapper, 'checked');
		}
	}

}