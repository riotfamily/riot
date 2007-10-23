/*
 * RiotFamily utility functions (depends on prototype.js)
 * Author: Felix Gnass
 */

var RBuilder = {};
RBuilder.node = function(tag, options) {
	var e = Element.extend(document.createElement(tag));
	for (var attr in options) {
		if (attr == 'style') {
			Element.setStyle(e, options.style);
		}
		else if (attr == 'parent') {
			$(options[attr]).appendChild(e);
		}
		else {
			if (attr == 'class') attr = 'className';
			e[attr] = options[attr];
		}
	}
	for (var i = 2; i < arguments.length; i++) {
		var arg = arguments[i];
		if (!arg) continue;
		if(arg.constructor == Array) arg.each(append); else append(arg);
	}

	function append(arg) {
        if(arg == null) return;
        switch(typeof arg) {
            case 'number': arg = '' + arg;  // fall through
            case 'string': arg = document.createTextNode(arg);
        }
        e.appendChild(arg);
    }

	return e;
}

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
	insertSelfBefore: function(el, marker) {
		el = $(el);
		marker.parentNode.insertBefore(el, marker);
		return el;
	},

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

	appendTo: function(el, parent) {
		el = $(el);
		$(parent).appendChild(el);
		return el;
	},

	replaceBy: function(el, replacement) {
		el = $(el);
		replacement = $(replacement);
		el.parentNode.replaceChild(replacement, el);
		return replacement;
	},

	surroundWith: function(el, wrapper) {
		el = $(el);
		el.replaceBy(wrapper).appendChild(el);
		return wrapper;
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
		while (el && el.style && (!bg || bg == 'transparent')) {
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

	disableClicks: function(el) {
		if (!(el = $(el))) return;
		var a = el.ancestors().concat([el]).concat(el.descendants());
		for (var i = 0; i < a.length; i++) {
			var e = a[i];
			if (e.onlick && !e.riot_onclick) {
				e.riot_onclick = e.onclick;
				e.onclick = null;
			}
			if (e.tagName == 'A' && e.href && !e.riot_href) {
				e.riot_href = e.href;
				e.href = 'javascript://';
			}
		}
		return el;
	},

	restoreClicks: function(el) {
		if (!(el = $(el))) return;
		var a = el.ancestors().concat([el]).concat(el.descendants());
		for (var i = 0; i < a.length; i++) {
			var e = a[i];
			if (e.riot_onlick) {
				e.onclick = e.riot_onclick;
				e.riot_onclick = null;
			}
			if (e.tagName == 'A' && e.riot_href) {
				e.href = e.riot_href;
				e.riot_href = null;
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
	    	if(options.setLeft) el.style.left = (source.leftPos() + options.offsetLeft) + 'px';
    		if(options.setTop) el.style.top = (source.topPos() + options.offsetTop) + 'px';
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
	
	//Prototype's Position.cumulativeOffset() method to too naive, 
	//see http://qooxdoo.org/documentation/general/compute_element_position
	//and http://www.koders.com/javascript/fidEA3E9D9152F06207EBED4D57045EFC0F2629593B.aspx?s=array
	
    leftPos: function(el) {
    	var left;
    	if (Prototype.Browser.IE) {
        	left = el.getBoundingClientRect().left + Viewport.getScrollLeft();
      	}
      	else {
    		left = el.offsetLeft;
			while (el.tagName.toLowerCase() != 'body') {
				el = el.offsetParent;
				left += el.offsetLeft - el.scrollLeft;
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
        return left;
	},
	
	topPos: function(el) {
		var top;
    	if (Prototype.Browser.IE) {
        	top = el.getBoundingClientRect().top + Viewport.getScrollTop();
      	}
      	else {
    		top = el.offsetTop;
			while (el.tagName.toLowerCase() != 'body') {
				el = el.offsetParent;
				top += el.offsetTop - el.scrollTop;
			}
		}
		var body = $(document.body);
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
        return top;
	}
}

Element.addMethods(RElement);
if (Prototype.Browser.IE) {
	// Reset the _extended flag, inn case $() was already invoked before this point
	for (var i = 0, len = document.all.length; i < len; i++) {
		var el = document.all[i];
		if (el._extended) el._extended = false;
	}
}

var RForm = {
	getValues: function(form) {
		return $(form).getElements().inject({}, function(map, e) {
			if (e.name) {
				var value = e.getValue();
				if (value) {
					if (!map[e.name]) map[e.name] = [];
					map[e.name].push(value);
				}
			}
			return map;
		});
	}
}
