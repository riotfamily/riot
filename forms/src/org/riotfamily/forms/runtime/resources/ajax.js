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

	var url = window.location.href;
	if (source.form && source.form.action) {
		url = source.form.action;
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
	var url = form.action || window.loction.href;
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

function submitForm(form, onSuccess, onFailure) {
	form = $(form);
	var url = form.action || window.loction.href;
	var elements = form.select('textarea','input:not(input[type="submit"])', 'select');
	var params = Form.serializeElements(elements, true);
	params.ajaxSave = 'true';
	var request = new Ajax.Request(url, {
		onSuccess: function(transport) {
			if (onSuccess) onSuccess(transport);
		},
		onException: function(transport, exception) {
			if (onFailure) onFailure(transport);
			return true;
		},
		onFailure: function(transport) {
			if (onFailure) onFailure(transport);
			return true;
		},
		parameters: params
	});
}
				
function processAjaxResponse(transport) {
	transport.responseJSON.each(performAction);
}

function performAction(action) {
	if (action.command == 'remove') {
		$(action.element).remove();
	}
	else if (action.command == 'insert') {
		$(action.element).insert(action.value);
	}
	else if (action.command == 'replace') {
		$(action.element).replace(action.value);
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
	else if (action.command == 'propagate') {
		propagate(action.element, action.value);
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
	if (e) {
		if (e.nodeName.match(/(input|textarea|button)/i)) {
			e.focus();
			return true;
		}
		return e.immediateDescendants().any(focusElement);
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
