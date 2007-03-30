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
	
	replaceBy: function(el, replacement) {
		el = $(el);
		el.parentNode.replaceChild($(replacement), el);
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
		if (!isDefined(add)) add = !el.hasClassName(className);
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
	}
}

Element.addMethods(RElement);

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