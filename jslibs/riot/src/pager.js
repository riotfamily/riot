var Pager = Class.create();
Pager.prototype = {

	initialize: function(el, onclick) {
		this.el = $(el);
		this.onclick = onclick;
		this.clip = document.createElement('div');
		this.clip.className = 'clip';
		this.clip.style.position = 'relative';
		Element.makeClipping(this.clip);
		
		this.reel = document.createElement('div');
		this.reel.style.whiteSpace = 'nowrap';
		Element.makePositioned(this.reel);
		
		this.clip.appendChild(this.reel);
		this.el.appendChild(this.clip);
	},

	update: function(currentPage, pages) {
		if (pages != this.pages) {
			Element.update(this.reel);
			this.buttons = [];
			if (pages > 1) {
				this.el.addClassName('pager');
				for (var i = 1; i <= pages; i++) {
					this.buttons[i] = this.appendButton(i);
				}
				var end = document.createElement('span');
				this.reel.appendChild(end);
				this.maxOffset = end.offsetLeft - this.clip.clientWidth;
			}
		}
		this.gotoPage(currentPage);
	},
	
	gotoPage: function(page) {
		if (page >= this.buttons.length) return;
		var prevPage = this.currentPage;
		var p = this.buttons[page];
		p.addClassName('current-page');
		this.currentPage = p;
		if (this.maxOffset > 0) {
			if (this.effect && this.effect.state == 'running') {
				this.effect.cancel();
			}

			var offset = p.offsetLeft - Math.round(this.clip.clientWidth / 2 - p.clientWidth / 2);
			if (offset < 0) offset = 0;
			if (offset > this.maxOffset) {
				offset = this.maxOffset;
			}

			if (prevPage) {
				prevPage.removeClassName('current-page');
				if (offset != -parseInt(Element.getStyle(this.reel, 'left'))) {
					this.effect = new Effect.Move(this.reel, {x: -offset, mode: 'absolute'});
				}
			}
			else {
				this.reel.style.left = -offset + 'px';
			}
		}
	},
	
	handleClick: function(page) {
		this.onclick(page);
	},

	appendButton: function(page, className, label) {
		var a = document.createElement('span');
		Event.observe(a, 'click', this.handleClick.bind(this, page));
		a.className = className || 'page';
		a.innerHTML = label || page;
		this.reel.appendChild(a);
		return Element.extend(a);
	}
}
