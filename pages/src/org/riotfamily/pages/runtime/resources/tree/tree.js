/**
 * Lightweight tree widget based on the SilverStripe Tree Control.
 * See http://www.silverstripe.com/downloads/tree/
 * The code has been refactored and now uses prototype.js.
 */
var Tree = {
	
	create: function(el, linkHandler) {
		el = $(el);
		var items = $A(el.childNodes).findAll(function(e) { return e.tagName == 'LI' });
		items.each(function(item, index) {
			Tree._initLi(item, index == items.length - 1);
		});
		if (linkHandler) {
			$A(el.getElementsByTagName('A')).each(function(a) { a.onclick = linkHandler	});
		}
	},

	_initLi: function(li, last) {
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

		Element.addClassName(li, 'closed');
		
		if (last) {
			Element.addClassName(li, 'last');
			Element.addClassName(divA, 'last');
		}
		
		divB.onclick = function(ev) {
			var el = this.li;
			if (el.childUL) {
				if(!Element.hasClassName(el, 'closed')) {
					Element.addClassName(li, 'closed');
				} 
				else {
					Element.removeClassName(li, 'closed');
				}
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
				li.childUL = li.childNodes[j];
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
		if (li.childUL) {
			this.create(li.childUL);
			Element.addClassName(li, 'children');
			Element.addClassName(divA, 'children');
		}
	}
}
