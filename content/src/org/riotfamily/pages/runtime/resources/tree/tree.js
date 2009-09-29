/**
 * Lightweight tree widget based on the SilverStripe Tree Control.
 * See http://www.silverstripe.com/downloads/tree/
 * The code has been refactored and now uses prototype.js.
 */
var Tree = Class.create({

	initialize: function(el, linkHandler) {
		el = $(el);
		this.linkHandler = linkHandler;
		this.initUl(el);
		var a = el.select('a');
		for (var i = 0; i < a.length; i++) {
			a[i].observe('click', this.handleClick.bindAsEventListener(this, a[i]));
		}
	},

	handleClick: function(ev, a) {
		ev.stop();
		this.linkHandler(a.getAttribute('href'));
	},

	initUl: function(ul) {
		var items = ul.childElements().findAll(function(e) {return e.tagName == 'LI'});
		for (var i = 0, len = items.length; i < len; i++) {
			this.initLi(items[i], i == len -1);
		}
	},

	initLi: function(li, last) {
		// Create the extra divs
		var divA = document.createElement('div');
		var divB = document.createElement('div');
		var divC = document.createElement('div');
		divA.appendChild(divB);
		divB.appendChild(divC);
		divB.li = li;
		divA.className = 'a';
		divB.className = 'b';
		divC.className = 'c';

		if (!li.hasClassName('expanded')) {
			li.addClassName('closed');
		}
		else {
			li.removeClassName('expnaded');
		}

		if (last) {
			Element.addClassName(li, 'last');
			Element.addClassName(divA, 'last');
		}

		divB.onclick = function(ev) {
			if (this.li.childUl) {
				Element.toggleClassName(this.li, 'closed');
			}
		};

		// Find nested UL within the LI
		var stoppingPoint = li.childNodes.length;
		var startingPoint = 0;

		for (var j = 0; j < li.childNodes.length; j++) {
			if (li.childNodes[j].tagName == 'DIV') {
				startingPoint = j + 1;
				continue;
			}
			if (li.childNodes[j].tagName == 'UL') {
				li.childUl = li.childNodes[j];
				stoppingPoint = j;
				break;
			}
		}

		// Move all the nodes up until that point into divC
		for (var j = startingPoint; j < stoppingPoint; j++) {
			divC.appendChild(li.childNodes[startingPoint]);
		}

		// Insert the outermost extra div into the tree
		if (li.childNodes.length > startingPoint) {
			li.insertBefore(divA, li.childNodes[startingPoint]);
		}
		else {
			li.appendChild(divA);
		}

		// Process the children
		if (li.childUl) {
			this.initUl(li.childUl);
			Element.addClassName(li, 'children');
			Element.addClassName(divA, 'children');
		}
	}
});
