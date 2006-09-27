/*
 * Parses a command id from the className of the given element.
 * Example: class="command command-foo enabled" would return 'foo'.
 */
function getCommand(e) {
	var c = e.className;
	var i = c.indexOf('command-');
	if (i != -1) {
		var i = i + 8;
		var n = c.indexOf(' ', i);
		if (n == -1) n = c.length;
		return c.substring(i, n);
	}
	return null;
}

/* 
 * Onclick handler that executes a command using a XMLHttpRequest.
 */
function executeCommand(e) {
	if (this.command) {
		if (!e) var e = window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		
		var url = location.href;
		var i = url.indexOf('?');
		if (i != -1) {
			url = url.substring(0, i);
		}
		url += '?command=' + this.command;
		
		if (this.objectId != null) url += '&objectId=' + this.objectId;
		sendCommandRequest(url);
		return false;
	}
}

function sendCommandRequest(url) {
	new Ajax.Request(url, {
		method: 'get', 
		onComplete: processCommandResponse
	});
}

function processCommandResponse(req) {	
	var e = req.responseXML.documentElement.firstChild;
	while (e) {
		if (e.nodeName == 'confirm') {
			if (confirm(e.firstChild.nodeValue)) {
				sendCommandRequest(e.getAttribute('url'));
			}
		}
		else if (e.nodeName == 'eval') {
			eval(e.firstChild.nodeValue);
		}
		e = e.nextSibling;		
	}
}