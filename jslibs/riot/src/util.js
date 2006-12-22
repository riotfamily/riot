/*
 * RiotFamily utility functions (depend on prototype.js) 
 * Author: Felix Gnass
 */ 

function isDefined(obj) {
	return typeof(obj) != 'undefined';
}

function isSet(obj) {
	return this.isDefined(obj) && obj != null;
}

function isFunction(func) {
	return typeof(func) == 'function';
}

if (!String.prototype.trim) {
	String.prototype.trim = function() {
		return this.replace(/^\s+|\s+$/g, '');
	}
}

Object.bindMethods = function(obj, methodNames) {
	if (methodNames) {
		for (var i = 0; i < methodNames.length; i++) {
			var method = obj[methodNames[i]];
			obj[methodNames[i]] = method.bind(obj);
		}
	}
}

/**
 * Returns true if the mouse event was fired within the range of the given element.
 */
Event.within = function(event, element) {
	var pX = Event.pointerX(event);
	var pY = Event.pointerY(event);
	return Position.within(element, pX, pY);
}

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
		while (element && element.style && (!isSet(bg) || bg == 'transparent')) {
			bg = Element.getStyle(element, 'background-color');
			element = element.parentNode;
		}
		return bg || '#ffffff';
	}
}
	
var RElement = {
	insertBefore: function(element, marker) {
		marker.parentNode.insertBefore($(element), marker);
	},

	insertAfter: function(element, marker) {
		element = $(element)
		var p = marker.parentNode;
		if (marker.nextSibling) p.insertBefore(element, marker.nextSibling);
		else p.appendChild(element);
	},

	prependChild: function(element, child) {
		element = $(element);
		if (element.firstChild) element.insertBefore(child, element.firstChild);
		else element.appendChild(child);
	},
	
	findAncestor: function(el, iterator) {
		el = el.parentNode;
		while (el) {
			if (iterator(el)) return el;
			el = el.parentNode;		
		}
		return null;
	},
		
	getAncestorWithClassName: function(el, className) {
		return RElement.findAncestor($(el), function(a) { 
			return Element.hasClassName(a, className);
		});
	},

	getDescendants: function(el) {
		el = $(el);
		return Array.from(el.all ? el.all : el.getElementsByTagName('*'));
	},

	getNextSiblingElement: function(el) {
		var e = $(el).nextSibling;
		while (e) {
			if (e.nodeType == 1) return e;
			e = e.nextSibling;
		}
		return null;
	},

	makeInvisible: function(el) {
		$(el).style.visibility = 'hidden';
	},

	makeVisible: function(el) {
		$(el).style.visibility = 'visible';
	},

	toggleClassName: function(el, className, add) {
		el = $(el);
		if (!isDefined(add)) add = !Element.hasClassName(el, className);
		if (add) Element.addClassName(el, className); else Element.removeClassName(el, className);
	},
  
	repaint: function(el) {
		var e = $(el) || document.body;
		var display = Element.getStyle(e, 'display');
		e.style.display = 'none';
		e.style.display = display;
	},

	hoverHighlight: function(el, className) {
		el = $(el);
		if(!isSet(el.addHighlight)) {
			el.addHighlight = function(event) {
				Element.addClassName(this, className);
				if (event) Event.stop(event);
			}.bindAsEventListener(el);
			
			el.removeHighlight = function(event) {
				Element.removeClassName(this, className);
				if (event) Event.stop(event);
			}.bindAsEventListener(el);
		}	
		Event.observe(el, 'mouseover', el.addHighlight, false);
		Event.observe(el, 'mouseout', el.removeHighlight, false);
	},

	stopHighlighting: function(el) {
		el = $(el);
		if(isSet(el.removeHighlight)) {
			el.removeHighlight();
			Event.stopObserving(el, 'mouseover', el.addHighlight, false);
			Event.stopObserving(el, 'mouseout', el.removeHighlight, false);
		}
	}
}

// See: http://www.bloglines.com/blog/reinyannyan?id=1

var Class = {
    create: function (proto) {
        var clazz = function () {
            if (this.initialize && arguments.callee.caller != Class.extend) {
                this.__class__ = arguments.callee.prototype;
                Object.extend(this, Class.Methods);
                this.initialize.apply(this, arguments);
            }
        };
        clazz.prototype = proto || {};
        clazz.extend = Class.extend;
        return clazz;
    },

    singleton: function (proto) {
        var clazz = {
            instance: function () {
                if (!this.__instance__) {
                    var clazz = Class.create(proto);
                    this.__instance__ = eval('new clazz');
                    proto = null;
                }
                return this.__instance__;
            }
        };
        return clazz;
    },

    append_features: function (object, module) {
        for (var prop in module) {
            if (Class.kind_of(module[prop], Function)) {
                (function (method) {
                    object[method] = function () {
                        return module[method].apply(object, arguments);
                    };
                })(prop);
            }
            else {
                object[prop] = module[prop];
			}
		}		         
    },
  
    extend: function (subobj) {
        var subproto = new this;
        Object.extend(subproto, subobj);
        subproto.__super__ = this.prototype;
        return Class.create(subproto);
    },
    get_method: function (clazz, args) {
        var c = args.callee.caller;
        for (var method in clazz) {
            if (clazz[method] == c) return method;
        }
        return null;
    },
    call_super: function (superclass, self, method, args) {
        if (superclass && superclass[method]) {
            var __class__  = self.__class__;
            self.__class__ = superclass;
            self.__super__ = superclass.__super__;
            try {
                superclass[method].apply(self, args);
            }
            finally {
                self.__class__ = __class__;
                self.__super__ = superclass;
            }
        }
    },
    kind_of: function (object, clazz) {
        return eval('clazz.prototype.isPrototypeOf(object)');
    }
};

Class.Methods = {
    extend: function () {
        var i = arguments.length;
        while (--i >= 0) {
            Object.extend(this, arguments[i]);
        }
        return this;
    },
    
    include: function () {
        var i = arguments.length;
        while (--i >= 0) {
            Class.append_features(this, arguments[i]);
        }
        return this;
    },
    
    kind_of: function (clazz) {
        return Class.kind_of(this, clazz);
    },
    
    SUPER: function () {
        var method = Class.get_method(this.__class__, arguments);
        Class.call_super(this.__super__, this, method, arguments);
    }
};