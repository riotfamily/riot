var RiotCheckboxGroup = Class.create();
RiotCheckboxGroup.prototype = {
	initialize: function(el) {
		this.el = el = $(el);
		var handler = this.onclick;
		el.options = this.el.getElementsBySelector('input');
		el.options.each(function(cb) {
			el.form = cb.form;
			cb.observe('click', handler.bind(el, cb));
		});
	},
	onclick: function(cb) {
		cb.selected = cb.checked;
		if (this._onchange) {
			this._onchange(new ChangeEvent(this));
		}
	}
}

var RiotRadioButtonGroup = Class.create(RiotCheckboxGroup, {
	
	onclick: function(cb) {
		this.options.each(function(el) {
			el.selected = false;
		});
		cb.selected = cb.checked;
		if (this._onchange) {
			this._onchange(new ChangeEvent(this));
		}
	}

});

var RiotImageCheckbox = Class.create({
	initialize: function(el, src) {
		el = this.el = $(el).hide();
		var id = el.id;
		var handler = this.handleClick.bindAsEventListener(this);
		var className = '';
		$$('label[for="' + id + '"]').each(function(label) {
			label.observe('click', handler);
			className += label.className + ' ';
		});
		el.id += '-checkbox';
		this.wrapper = new Element('span', {id: id, className: 'image' + el.type.capitalize()});
		el.wrap(this.wrapper)
		
		el.image = new Element(src ? 'img' : 'div', {className: className});
		el.image.onclick = handler;
		if (src) el.image.src = src;
		this.wrapper.appendChild(el.image);

		if (el.checked) {
			this.wrapper.addClassName('checked');
		}
	},
	
	handleClick: function(e) {
		this.el.checked = !this.el.checked;
		if (this.el.checked) {
			this.wrapper.addClassName('checked');
		}
		else {
			this.wrapper.removeClassName('checked');
		}
	}
});


var RiotImageRadioButton = Class.create(RiotImageCheckbox, {
	
	initialize: function($super, el, src) {
		$super(el, src);
		this.others = $$('input[name="' + this.el.name + '"]:not(#' + this.el.id +')');
	},
	
	handleClick: function(e) {
		if (!this.el.checked) {
			this.el.checked = true;
			this.wrapper.addClassName('checked');
			this.others.pluck('parentNode').invoke('removeClassName', 'checked');
		}
	}
	
});
