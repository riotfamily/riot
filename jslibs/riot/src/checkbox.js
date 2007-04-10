var RiotCheckboxGroup = Class.create();
RiotCheckboxGroup.prototype = {
	initialize: function(el) {
		this.el = el = $(el);
		var handler = this.onclick;
		el.options = this.el.getElementsBySelector('input');
		el.options.each(function(cb) {
			el.form = cb.form;
			cb.observe('click', handler.bind(this, cb));
		});
	},
	onclick: function(cb) {
		cb.selected = cb.checked;
		if (this.el._onchange) {
			this.el._onchange(new ChangeEvent(this.el));
		}
	}
}

var RiotImageCheckbox = Class.create();
RiotImageCheckbox.prototype = {
	initialize: function(el, className, src) {
		el = $(el).hide(el);
		this.checkbox = el;
		var id = el.id;
		var handler = this.handleClick.bindAsEventListener(this);
		$$('label[for="' + id + '"]').each(function(label) {
			label.observe('click', handler);
		});
		
		this.wrapper = document.createElement('span');
		el.id = id + '-checkbox';
		this.wrapper.id = id;
		this.wrapper.className = 'imageCheckbox';
		
		el.parentNode.insertBefore(this.wrapper, el);
		el.remove();
		this.wrapper.appendChild(el);
		
		var image = document.createElement(src ? 'img' : 'div');
		image.className = className || 'image';
		image.src = src;
		this.wrapper.appendChild(image);
		
		if (this.checkbox.checked) {
			Element.addClassName(this.wrapper, 'checked');
		}
		image.onclick = handler;
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
 