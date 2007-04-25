if (typeof XMLHttpRequest == 'undefined') {
	try {
		var dummy = new ActiveXObject('Microsoft.XMLHTTP');
		XMLHttpRequest = function () {
			return new ActiveXObject('Microsoft.XMLHTTP');
	 	}
 	}
 	catch(e) { 		
 	}
}

function TreeWalker(func) {
	this.callback = func;
	this.walk = function(node) {
		this.callback(node);
		var currentNode = node.firstChild;
		while (currentNode != null) {
			this.walk(currentNode);			
			currentNode = currentNode.nextSibling;
		}
	}
}

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
	if (typeof XMLHttpRequest != 'undefined') {
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
		
		var body = '_ajax=true&event.type=' + e.type + '&event.source=' + source.id;
		
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
		
		var request = new XMLHttpRequest();
		request.onreadystatechange = function() {
			if (request.readyState == 4 && request.status == 200 && request.responseXML) {			
				processAjaxResponse(request.responseXML.documentElement);
			}
		}
		request.open("POST", url, true);
		request.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
		request.send(body);
		return false;
	}
	
}

function processAjaxResponse(doc) {
	if (!doc) return;
	var e = doc.firstChild;
	while (e) {
		try {
			if (e.nodeName == 'remove') {
				var ref = getRef(e);
				ref.parentNode.removeChild(ref);
			}
			else if (e.nodeName == 'insert') {
				var ref = getRef(e);
				ref.appendChild(importNode(getFirstChildElement(e), true));
			}
			else if (e.nodeName == 'replace') {
				var ref = getRef(e);
				var newNode = importNode(getFirstChildElement(e), true);
				var p = ref.parentNode;
				var marker = document.createTextNode(' ');
				
				p.insertBefore(marker, ref);
				p.removeChild(ref);
				p.insertBefore(newNode, marker);
				p.removeChild(marker);
			}
			else if (e.nodeName == 'error') {						
				setValid(getRef(e), parseInt(e.getAttribute('valid')));
				var newEl = importNode(getFirstChildElement(e), true);				
				var oldEl = document.getElementById(newEl.id);
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
	else {
		var c = e.childNodes;
		for (var i = 0; i < c.length; i++) {
			if (focusElement(c[i])) return true;
		}
	}
	return false;
}

function getRef(e) {
	var refId = e.getAttribute('ref');
	return document.getElementById(refId);
}

function setValid(e, valid) {
	var labels = document.getElementsByTagName('label');
	for (i = 0; i < labels.length; i++) {				
		if (labels[i].htmlFor == e.id) {	
			setErrorClass(labels[i], valid);
		}
	}
}

function setErrorClass(e, valid) {
	if (valid) {
		e.className = e.origClassName || '';
	}
	else {
		if (!e.origClassName) {
			e.origClassName = e.className || '';
		}
		e.className += ' error'; 
	}
}

function setEnabled(e, enabled) {
	var treeWalker = new TreeWalker(function(node) {
		if (typeof node.disabled != 'undefined') {									
			node.disabled = !enabled;
		}
	});			
	treeWalker.walk(e);
}

function propagate(id, type) {
	if (typeof XMLHttpRequest != 'undefined') {
		var e = document.getElementById(id);
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
}
