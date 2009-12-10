/*
 * RiotFamily utility functions (depends on prototype.js)
 * Author: Felix Gnass
 */

var StyleUtils = {
	sides: ['top', 'right', 'bottom', 'left'],
	borderProps: ['style', 'width', 'color'],
	borderRegEx: /border(?:-(top|right|bottom|left))?(?:-(style|width|color))?/,
	backgroundProps: ['color', 'image', 'position', 'repeat'],
	combine: function(s, a) {
		var result = [];
		a.each(function(i) {
			result.push(s + '-' + i);
		});
		return result;
	},
	resolveProperty: function(prop) {
		if (prop == 'margin' || prop == 'padding') {
			return StyleUtils.combine(prop, StyleUtils.sides);
		}
		if (prop == 'background') {
			return StyleUtils.combine(prop, StyleUtils.backgroundProps);
		}
		var m = prop.match(StyleUtils.borderRegEx);
		if (m) {
			if (!m[1]) {
				var a = StyleUtils.combine('border', StyleUtils.sides);
				var b = m[2] ? [m[2]] : StyleUtils.borderProps;
				return a.collect(function(i) {
					return StyleUtils.combine(i, b);
				}).flatten();
			}
			else if (!m[2]) {
				return StyleUtils.combine(prop, StyleUtils.borderProps);
			}
		}
		return [prop];
	},
	resolveProperties: function(props) {
		return props.collect(StyleUtils.resolveProperty).flatten();
	}
}

var RElement = {

	insertSelfAfter: function(el, marker) {
		el = $(el);
		var p = marker.parentNode;
		if (marker.nextSibling) p.insertBefore(el, marker.nextSibling);
		else p.appendChild(el);
		return el;
	},

	prependChild: function(el, child) {
		el = $(el);
		if (el.firstChild) el.insertBefore(child, el.firstChild);
		else el.appendChild(child);
		return el;
	},

	replaceBy: function(el, replacement) {
		el = $(el);
		replacement = $(replacement);
		el.parentNode.replaceChild(replacement, el);
		return replacement;
	},

	makeBlock: function(el) {
		el = $(el);
		el.style.display = 'block';
		return el;
	},

	makeInvisible: function(el) {
		if (!(el = $(el))) return;
		el.style.visibility = 'hidden';
		return el;
	},

	makeVisible: function(el) {
		if (!(el = $(el))) return;
		el.style.visibility = 'visible';
		return el;
	},

	cloneStyle: function(el, source, properties) {
		el = $(el);
		source = $(source);
		properties = StyleUtils.resolveProperties(properties);
		for (var i = 0; i < properties.length; i++) {
			var prop = properties[i];
			el.style[properties[i].camelize()] = source.getStyle(properties[i]);
		}
		return el;
	},

	getBackgroundColor: function(el) {
		element = $(el);
		var bg;
		while (el && el.style && (!bg || bg == 'transparent' || bg.match(/^rgba.*0\)$/))) {
			bg = Element.getStyle(el, 'background-color');
			el = el.parentNode;
		}
		return bg || '#fff';
	},

	toggleClassName: function(el, className, add) {
		if (!(el = $(el))) return;
		if (typeof add == 'undefined') add = !el.hasClassName(className);
    	Element.classNames(el)[add ? 'add' : 'remove'](className);
	    return el;
	},

	setHoverClass: function(el, className) {
		if (!(el = $(el))) return;
		el.observe('mouseover', el.addClassName.bind(el, className));
		el.observe('mouseout', el.removeClassName.bind(el, className));
		return el;
	},
	
	disableLinks: function(el) {
		if (!(el = $(el))) return;
		var a = el.ancestors().concat([el]).concat(el.descendants());
		for (var i = 0; i < a.length; i++) {
			var e = a[i];
			if (typeof e.riot_onclick == 'undefined') {
				if (e.tagName == 'A' && e.href) {
					e.riot_onclick = e.onclick || null;
					e.onclick = function() { return false; }
				}
				else if (e.onclick) {
					e.riot_onclick = e.onclick;
					e.onclick = null;
				}
			}
		}
		return el;
	},

	enableLinks: function(el) {
		if (!(el = $(el))) return;
		var a = el.ancestors().concat([el]).concat(el.descendants());
		for (var i = 0; i < a.length; i++) {
			var e = a[i];
			if (typeof e.riot_onclick != 'undefined') {
				e.onclick = e.riot_onclick;
				e.riot_onclick = void(0);
			}
		}
		return el;
	},

	copyPosFrom: function(el, source) {
		el = $(el);
		source = $(source);
		var options = Object.extend({
			setWidth: true,
		    setHeight: true,
		    setLeft: true,
		    setTop: true,
		    offsetLeft: 0,
		    offsetTop: 0,
	    	offsetWidth: 0,
	    	offsetHeight: 0
	    }, arguments[2] || {});
		if (options.setWidth)  el.style.width = source.offsetWidth + options.offsetWidth + 'px';
	    if (options.setHeight) {
	    	if (source.offsetHeight == 0) {
	    		// Firefox wrongly reports the offsetHeight as 0, when an 
	    		// inline element contains images but no text nodes ...
	    		var img = source.down('img');
	    		if (img) source = img;
	    	}
	    	el.style.height = source.offsetHeight + options.offsetHeight + 'px';
	    }
	    if (Position.offsetParent(el) == document.body) {
	    	// Shortcut, if the target offsetParent is document.body
	    	// Works around a Prototype/Safari 3.0.3+ bug with with Position.page()
	    	var pos = source.cumulativeOffset();
	    	if(options.setLeft) el.style.left = (pos.left + options.offsetLeft) + 'px';
    		if(options.setTop) el.style.top = (pos.top + options.offsetTop) + 'px';
	    }
	    else {
			Position.clone(source, el, Object.extend(options, {
				setWidth: false, setHeight: false
			}));
		}
		return el;
	},

	copyPosTo: function(el, target) {
		$(target).copyPosFrom(el);
		return el;
	},
	
	// Overwrite Prototype's cumulativeOffset() method with more sophisticated one: 
	cumulativeOffset: function(el) {
    	var left, top;
    	if (Prototype.Browser.IE) {
    		var rect = el.getBoundingClientRect();
    		var scroll = document.viewport.getScrollOffsets();
        	left = rect.left - 2 + scroll.left;
        	top = rect.top - 2 + scroll.top;
      	}
      	else {
    		top = el.offsetTop;
    		left = el.offsetLeft;
			while (el && el.tagName.toLowerCase() != 'body') {
				el = el.offsetParent;
				if (el) {
					left += el.offsetLeft;
					top += el.offsetTop;
				}
			}
		}
		var body = $(document.body);
		if (body.getStyle('border-left-style') != 'none') {
			var border = parseInt(body.getStyle('border-left-width'));
			if (!isNaN(border)) {
				if (Prototype.Browser.IE) {
					left -= border;
				}
				else if (Prototype.Browser.Gecko) {
					left += border;
				}
			}
		}
		if (body.getStyle('border-left-style') != 'none') {
			var border = parseInt(body.getStyle('border-top-width'));
			if (!isNaN(border)) {
				if (Prototype.Browser.IE) {
					top -= border;
				}
				else if (Prototype.Browser.Gecko) {
					top += border;
				}
			}
		}
        return Element._returnOffset(left, top);
	}
};

(function() {
	Position.cumulativeOffset = RElement.cumulativeOffset;
	Element.addMethods(RElement);
	if (Prototype.Browser.IE) {
		// Reset the _extendedByPrototype flag, inn case $() was already invoked before this point
		for (var i = 0, len = document.all.length; i < len; i++) {
			var el = document.all[i];
			if (el._extendedByPrototype) {
				el._extendedByPrototype = void(0);
			}
		}
	}
})();

