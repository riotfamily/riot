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

function submitEvent(e, onComplete) {
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

	var url = window.location.href;
	if (source.form && source.form.action) {
		url = source.form.action;
	}
	
	var sourceId = source.sourceId || source.id;
	var body = 'event.type=' + e.type + '&event.source=' + sourceId; 
	
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
		onComplete: onComplete,
		postBody: body
	});
	return false;
}

function submitElement(id, clickedButton) {
	
	var form = $(clickedButton).up('form');
	var url = form.action || window.location.href;
	
	var elements;
	if (id != form.id) {
		var e = $(id);
		elements = e.descendants(); elements.push(e);
	}
	else {
		elements = form.descendants();
	}
	
	var data = elements.inject({}, function(result, element) {
		if (!element.disabled && element.form && element.name
				&& element.type != 'file' 
				&& (element.type != 'submit' || element == clickedButton)) {
				
			var key = element.name, value = $F(element);
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
	
	if (id != form.id) {
		data._exclusive = id;
	}
	
	new Ajax.Request(url, {
		onSuccess: processAjaxResponse,
		parameters: data
	});
}
				
function processAjaxResponse(transport) {
	transport.responseJSON.each(performAction);
}

function performAction(action) {
	if (action.command == 'remove') {
		new Effect.Remove(action.element);
	}
	else if (action.command == 'insert') {
		$(action.element).insert(action.value);
		if (window.onInsertElement) {
			onInsertElement($(action.element));
		}
	}
	else if (action.command == 'replace') {
		$(action.element).replace(action.value);
		if (window.onInsertElement) {
			onInsertElement($(action.element));
		}
	}
	else if (action.command == 'error') {						
		var errors = $(action.element + '-error');
		if (errors) errors.replace(action.value);								
	}
	else if (action.command == 'valid') {						
		setValid($(action.element), action.value == 'true');
	}
	else if (action.command == 'focus') {		
		focusElement($(action.element));
	}
	else if (action.command == 'enable') {				
		setEnabled($(action.element), action.value == 'true');
	}
	else if (action.command == 'setVisible') {				
		setVisible(action.element, action.value == 'true');
	}
	else if (action.command == 'propagate') {
		propagate(action.trigger, action.value, action.element);
	}
	else if (action.command == 'refresh') {
		var ev = new ChangeEvent($(action.element));
		setTimeout(function() { submitEvent(ev) }, '1000');
	}
	else if (action.command == 'eval') {
		eval(action.value);
	}		
}

function focusElement(e) {
	try {
		if (e) {
			if (e.nodeName.match(/(input|textarea|button)/i)) {
				e.focus();
				return true;
			}
			return e.immediateDescendants().any(focusElement);
		}
	} catch (ex) {
	}
	return false;
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
	e = $(e);
	if (e) {
		var nodes = e.descendants();
		nodes.push(e);
		nodes.each(function(node) {
			if (typeof node.disabled != 'undefined') {									
				node.disabled = !enabled;
			}
		});
	}
}

function setVisible(id, visible) {
	var el = $('container-' + id) || $(id);
	if (visible) {
		el.show();
	}
	else {
		el.hide();
	}
}

function propagate(e, type, sourceId) {
	e = $(e);
	if (!e) return;
	e.sourceId = sourceId;
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
