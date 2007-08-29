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

	disableHandlers: function(el, name) {
		if (!(el = $(el))) return;
		el.descendants().each(function(e) {
			if (e[name]) {
				e['riot_' + name] = e[name];
				e[name] = null;
			}
		});
		return el;
	},

	restoreHandlers: function(el, name) {
		if (!(el = $(el))) return;
		$(el).descendants().each(function(e) {
			if (e['riot_' + name]) {
				e[name] = e['riot_' + name];
				e['riot_' + name] = null;
			}
		});
		return el;
	},

	copyPosFrom: function(el, source) {
		el = $(el);
		source = $(source);
		var options = Object.extend({
			setWidth: true,
		    setHeight: true,
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
		Position.clone(source, el, Object.extend(options, {
			setWidth: false, setHeight: false
		}));
		return el;
	},

	copyPosTo: function(el, target) {
		$(target).copyPosFrom(el);
		return el;
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
