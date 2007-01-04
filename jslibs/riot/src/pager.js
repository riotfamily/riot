var Pager = Class.create();
Pager.prototype = {

	initialize: function(el, options) {
		this.el = $(el);
		this.options = Object.extend({
			padding: 3, 
			prevLabel: '<', 
			nextLabel: '>',
			gapLabel: '...'
		}, options);
	},

	render: function() {
		this.el.innerHTML = '';
		if (this.pages < 2) {
			this.el.removeClassName('pager');
			return;
		}
		this.el.addClassName('pager');
		var start = this.currentPage - this.options.padding;
		var end = this.currentPage + this.options.padding;
		if (start < 0) end += -1 * start + 1;
		if (end > this.pages) start -= (end - this.pages);
		if (start < 1) start = 1;
		if (end > this.pages) end = this.pages;

		var prevCount = Math.max(this.currentPage - start, 0);
		if (prevCount > 0) this.appendButton(this.currentPage - 1, 'prev', this.options.prevLabel);
		if (start > 1) this.appendButton(1);
		if (start > 2) this.appendSpan('gap', this.options.gapLabel);

		for (var i = 0; i < prevCount; i++) {
			this.appendButton(start + i);
		}

		this.appendSpan('page current-page', this.currentPage);

		var nextCount = Math.max(end - this.currentPage, 0);
		for (var i = 0; i < nextCount; i++) {
			this.appendButton(this.currentPage + i + 1);
		}

		if (end < this.pages - 1) this.appendSpan('gap', this.options.gapLabel);
		if (end < this.pages) this.appendButton(this.pages);
		if (nextCount > 0) 	this.appendButton(this.currentPage + 1, 'next', this.options.nextLabel);
	},

	onclick: function(ev, page) {
		if (this.options.onclick) {
			this.options.onclick(page);
		}
		Event.stop(ev);
	},

	update: function(currentPage, pages) {
		this.currentPage = currentPage;
		if (pages) this.pages = pages;
		this.render();
	},

	appendSpan: function(className, label) {
		var span = document.createElement('span');
		span.className = className;
		span.appendChild(document.createTextNode(label));
		this.el.appendChild(span);
		this.appendSpace();
	},

	appendSpace: function() {
		this.el.appendChild(document.createTextNode(' '));
	},

	appendButton: function(page, className, label) {
		var a = document.createElement('a');
		a.href = '#' + page;
		Event.observe(a, 'click', this.onclick.bindAsEventListener(this, page));
		a.className = className || 'page';
		a.innerHTML = label || page;
		this.el.appendChild(a);
		this.appendSpace();
	}
}
