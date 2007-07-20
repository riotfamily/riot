

if (document.importNode && !(/Konqueror|Safari|KHTML/.test(navigator.userAgent))) {
	function importNode(node, deep) {
		return document.importNode(node, deep);
	}
}
else {
	importNode = function (node, deep) {
		var target = null;		
		var root = node;
		while (node != null) {
			
			// Skip script and noscript tags ...
			var skipNode = node.nodeName.toLowerCase().indexOf('script') != -1;
			
			if (!skipNode) {	
				var importedNode = importSingleNode(node);
				if (target == null) {
					target = importedNode;
				}			
				else {
					target.appendChild(importedNode);
				}
			}
						
			if (deep && !skipNode && node.hasChildNodes()) {						
				target = importedNode;				
				node = node.firstChild;
			}									
			else if (node != root) {
				// go up until we find a node with a nextSibling ...
				while (node.nextSibling == null && node.parentNode != root) {
					target = target.parentNode;
					node = node.parentNode;
				}
				node = node.nextSibling;
			}
			else {
				// We've reached the root element - exit the loop ...
				node = null;
			}				
		}
		return target;
	}
	
	function importSingleNode(node) {
		var clone;	
		if (node.nodeType == 1) {
			clone = document.createElement(node.nodeName);
			for (var i = 0; i < node.attributes.length; i++) {
	            var attr = node.attributes[i];
	            if (attr.nodeValue != null && attr.nodeValue != '') {                    
	                if (attr.name == 'class') {
	                    clone.className = attr.nodeValue;
	                }
	                else if (attr.name == 'style') {
	                	if (typeof clone.style != 'undefined') {
		                    clone.style.cssText = attr.nodeValue;
		                }
	                }
	                else if (attr.name.indexOf('on') == 0) {
	                	eval('clone.' + attr.name + ' = function() {'
	                		+	attr.nodeValue + '}');
	                }
	                else  {               
	                	clone.setAttribute(attr.name, attr.nodeValue);                	
	                }
	            }
	        }
		} 
		else if (node.nodeType == 3) {
			clone = document.createTextNode(node.nodeValue);
		}
		return clone;
	}
	
}

function ChangeEvent(source) {
	this.type = 'change';
	this.srcElement = source;
}

function getEventSource(e) {
	var s;
	if (e.target) s = e.target;
	else if (e.srcElement) s = e.srcElement;
	if (s.nodeType == 3) s = s.parentNode;
	return s;
}

function getFirstChildElement(e) {
	var c = e.firstChild;
	while (c && c.nodeType != 1) c = c.nextSibling;
	return c;
}

function submitEvent(e) {
	if (!e) var e = window.event;
	var source = getEventSource(e);
	if (e.type == 'keyup') {
		if (source.keyTimeout) {
			clearTimeout(source.keyTimeout);
		}
		var changeEvent = new ChangeEvent(source);
		source.keyTimeout = setTimeout(function() { submitEvent(changeEvent) }, 1000);
		return;
	}

	var url;
	if (source.form && source.form.ajaxUrl) {
		url = source.form.ajaxUrl;
	}
	else {
		url = window.location.href;	
	}
	
	var body = 'event.type=' + e.type + '&event.source=' + source.id;
	
	if (source.options) {
		for (var i = 0; i < source.options.length; i++) {
			if (source.options[i].selected) {
				body += '&source.value=' + encodeURIComponent(source.options[i].value);
			}
		}
	}
	else if (source.value) {
		body += '&source.value=' + encodeURIComponent(source.value);
	}
	
	new Ajax.Request(url, {
		onSuccess: processAjaxResponse,
		postBody: body	
	});
	return false;
}

function submitElement(id, clickedButton) {
	var e = $(id);
	var form = e.up('form');
	var url = (form && form.ajaxUrl) ? form.ajaxUrl : window.loction.href;
	var elements = e.descendants(); elements.push(e);
	var data = elements.inject({_exclusive: id}, function(result, element) {
		if (!element.disabled && element.form && element.name
				&& element.type != 'file' 
				&& (element.type != 'submit' || element == clickedButton)) {
				
			var key = element.name, value = element.getValue();
			if (value != null) {
				if (key in result) {
				if (result[key].constructor != Array) result[key] = [result[key]];
            		result[key].push(value);
				}
				else result[key] = value;
        	}
      	}
      	return result;
    });
	new Ajax.Request(url, {
		onSuccess: processAjaxResponse,
		parameters: data
	});
}

function processAjaxResponse(transport) {
	var doc = transport.responseXML.documentElement;
	var e = doc.firstChild;
	while (e) {
		try {
			if (e.nodeName == 'remove') {
				getRef(e).remove();
			}
			else if (e.nodeName == 'insert') {
				getRef(e).appendChild(importNode(getFirstChildElement(e), true));
			}
			else if (e.nodeName == 'replace') {
				var ref = getRef(e);
				var newNode = importNode(getFirstChildElement(e), true);
				ref.parentNode.replaceChild(newNode, ref);
			}
			else if (e.nodeName == 'error') {						
				setValid(getRef(e), parseInt(e.getAttribute('valid')));
				var newEl = importNode(getFirstChildElement(e), true);				
				var oldEl = $(newEl.id);
				if (oldEl) oldEl.parentNode.replaceChild(newEl, oldEl);								
			}
			else if (e.nodeName == 'focus') {		
				focusElement(getRef(e));
			}
			else if (e.nodeName == 'enable') {				
				setEnabled(getRef(e), parseInt(e.getAttribute('state')));
			}
			else if (e.nodeName == 'propagate') {
				propagate(e.getAttribute('ref'), e.getAttribute('type'));
			}
			else if (e.nodeName == 'refresh') {
				var ev = new ChangeEvent(getRef(e));
				setTimeout(function() { submitEvent(ev) }, '1000');
			}
			else if (e.nodeName == 'eval') {
				if (e.firstChild) eval(e.firstChild.nodeValue);
			}		
		}
		catch (err) {
			alert(err);
		}
		e = e.nextSibling;
	}
}

function focusElement(e) {
	if (e.nodeName.match(/(input|textarea|button)/i)) {
		e.focus();
		return true;
	}
	return e.immediateDescendants().any(focusElement);
}

function getRef(e) {
	return $(e.getAttribute('ref'));
}

function setValid(e, valid) {
	$$('label[for="' + e.id + '"]').each(function(el) {	
		setErrorClass(el, valid);
	});
}

function setErrorClass(e, valid) {
	e = $(e);
	if (valid) e.removeClassName('error')
	else e.addClassName('error'); 
}

function setEnabled(e, enabled) {
	$(e).descendants().push(e).each(function(node) {
		if (typeof node.disabled != 'undefined') {									
			node.disabled = !enabled;
		}
	});
}

function propagate(e, type) {
	e = $(e);
	if (!e) return;
	var tag = e.nodeName.toLowerCase();
	if (tag == 'input' || tag == 'button' || tag == 'select' || tag == 'textarea') {
		if (type == 'click') {
			e.onclick = submitEvent;
		}
		else if (type == 'change') {
			if (tag == 'input' && e.type == 'text') {
				e.onkeyup = submitEvent;
			}
			else {
				e.onchange = submitEvent;
			}
		}
	}
	else {
		e["_on" + type] = submitEvent;
	}
}
