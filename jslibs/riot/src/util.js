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

var Styles = {
	clone: function(source, target, properties) {
		if (properties) {
			for (var i = 0; i < properties.length; i++) {
				target.style[properties[i].camelize()] = Element.getStyle(
						source, properties[i]);
			}
		}
		else {
			this.setStyle(target, this.getStyles(source));
		}
	},

	getAll: function(element) {
		element = $(element);
		var styles = {};
		for (name in element.style) {
			if (typeof name != 'function' && name != 'length' && name != 'parentRule') {
				styles[name] = element.style[name];
			}
		}
		if(document.defaultView && document.defaultView.getComputedStyle) {
			var css = document.defaultView.getComputedStyle(element, null);
			for (var i = 0; i < css.length; i++) {
				var name = css.item(i);
				var value = css.getPropertyValue(name);
				if (value != 'auto') styles[name] = value;
			}
		}
		else if(element.currentStyle) {
			for (name in element.currentStyle) {
				if (typeof name == 'function') continue;
				value = element.currentStyle[name];
				if (value != 'auto') styles[name] = value;
			}
		}
		return styles;
	},

	getBackgroundColor: function(element) {
		element = $(element);
		var bg;
		while (element && element.style && (!bg || bg == 'transparent')) {
			bg = Element.getStyle(element, 'background-color');
			element = element.parentNode;
		}
		return bg || '#ffffff';
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
		el.parentNode.replaceChild($(replacement), el);
		return el;
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
		if(options.setWidth)  el.style.width = source.offsetWidth + options.offsetWidth + 'px';
	    if(options.setHeight) el.style.height = source.offsetHeight + options.offsetHeight + 'px';
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
