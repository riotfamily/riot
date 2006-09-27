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

/**
 * Creates a new subclass. The superclass may either be a constructor function 
 * or an object (abstract superclass). 
 *
 * see: http://phrogz.net/JS/Classes/OOPinJS2.html
 */
Class.extend = function(superclass, members) {
	
	var clazz = function() {
		this.superclass = {};
		Object.extend(this.superclass, this.supertype);
		for (n in this.superclass) {
			var member = this.superclass[n];
			if (member.bind) this.superclass[n] = member.bind(this);
		}
		this.initialize.apply(this, arguments);
    }
    
	if (superclass.constructor == Function) { 
		Object.extend(clazz.prototype, superclass.prototype);
		clazz.prototype.constructor = clazz;
		clazz.prototype.supertype = superclass.prototype;
	} 
	else { 
		clazz.prototype = superclass;
		clazz.prototype.constructor = clazz;
		clazz.prototype.supertype = superclass;
	} 
	
	if (members) {
		Object.extend(clazz.prototype, members);
	}
	if (!clazz.prototype.initialize) {
		clazz.prototype.initialize = Prototype.emptyFunction;
	}
	return clazz;
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

Element.insertBefore = function(node, marker) {
	marker.parentNode.insertBefore(node, marker);
}

Element.insertAfter = function(node, marker) {
	var p = marker.parentNode;
	if (marker.nextSibling) p.insertBefore(node, marker.nextSibling);
	else p.appendChild(node);
}

Element.prependChild = function(parent, node) {
	parent = $(parent);
	if (parent.firstChild) parent.insertBefore(node, parent.firstChild);
	else parent.appendChild(node);
}

Element.defineTag = function(tag) {
	Element[tag.toUpperCase()] = function() {
		var args = [tag].concat($A(arguments));
		return Element.createNode.apply(Element, args);
	}
};

(function() {
	var tags = ['a', 'button', 'div', 'h1', 'h2', 'h3', 'img', 'input', 
		'label', 'li', 'option', 'p', 'select', 'span', 'table', 'tbody', 'td',
		'textarea', 'tr', 'ul'];
		
	for (var i = tags.length - 1; i >= 0; i--) {
		Element.defineTag(tags[i]);
	}
})();

Element.createNode = function(tag, options) {
	var e;
	e = document.createElement(tag);
	for (var attr in options) {
		if (attr == 'style') {
			Element.setStyle(e, options.style);
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
};
	
/**
 * Returns the computed runtime styles for the given element.
 */
Element.getStyles = function(element) {
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
}
	
/**
 * 
 */
Element.cloneStyles = function(source, target, properties) {
	if (properties) {
		for (var i = 0; i < properties.length; i++) {
			target.style[properties[i].camelize()] = Element.getStyle(
					source, properties[i]);
		}
	}
	else {
		this.setStyle(target, this.getStyles(source));
	}
}

Element.getBackgroundColor = function(el) {
	el = $(el);
	var bg;
	while (el && (!isSet(bg) || bg == 'transparent')) {
		bg = Element.getStyle(el, 'background-color');
		el = el.parentNode;
	}
	return bg || '#ffffff';
}
				
Element.findAncestor = function(el, iterator) {
	el = el.parentNode;
	while (el) {
		if (iterator(el)) return el;
		el = el.parentNode;		
	}
	return null;
}

Element.getAncestorWithClassName = function(el, className) {
	return Element.findAncestor(el, function(a) { 
		return Element.hasClassName(a, className);
	});
}

Element.getDescendants = function(el) {
	return Array.from(el.all ? el.all : el.getElementsByTagName('*'));
}

Element.getNextSiblingElement = function(el) {
	var e = el.nextSibling;
	while (e) {
		if (e.nodeType == 1) return e;
		e = e.nextSibling;
	}
	return null;
}

Element.invisible = function(el) {
	$(el).style.visibility = 'hidden';
}

Element.visible = function(el) {
	$(el).style.visibility = 'visible';
}

Element.toggleClassName = function(el, className, add) {
	if (!isDefined(add)) add = !Element.hasClassName(el, className);
	if (add) Element.addClassName(el, className); else Element.removeClassName(el, className);
}
  
Element.repaint = function(el) {
	var e = el || document.body;
	var display = Element.getStyle(e, 'display');
	e.style.display = 'none';
	e.style.display = display;
}

Element.hoverHighlight = function(el, className) {
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
}

Element.stopHighlighting = function(el) {
	if(isSet(el.removeHighlight)) {
		el.removeHighlight();
		Event.stopObserving(el, 'mouseover', el.addHighlight, false);
		Event.stopObserving(el, 'mouseout', el.removeHighlight, false);
	}
}
