/*
 * Initializes the specified table by adding event listeners. 
 */
function initList(listId, defaultCommand) {
	var table = $(listId);
	initHeadings(table.tHead.rows[0].cells);
	var rows = table.tBodies[0].rows;
	var index = 0;
	for (var i = 0; i < rows.length; i++) {
		if (rows[i].className != 'separator') {
			initRow(rows[i], index++, defaultCommand);
		}
	}
	var cmds = findElements(document, '(command-.*|defaultCommand)');
	for (var i = 0; i < cmds.length; i++) {
		var e = cmds[i];
		if (!e.command)	e.command = getCommand(e);
		e.onclick = executeCommand;
	}
}

/*
 * Initializes the given list of cells by adding an onclick handler that
 * reloads the list with an 'orderBy' parameter set to the cell's index.
 * Cells that don't have the 'sortable' class are ignored.
 */
function initHeadings(cells) {
	for (var i = 0; i < cells.length; i++) {
		var th = cells[i];
		if (Element.hasClassName(th, 'sortable')) {
			th.onclick = function() {
				location.replace('?orderBy=' + this.cellIndex);
			};
		}
	}
}

/*
 * Initializes the given row. Adds an onmouseover and onmouseout handler
 * to highlight the row onmouseover. Additionally the objectId property 
 * is set on descendent elements that have a 'command-*' class.
 */
function initRow(row, index, defaultCommand) {
	Element.addClassName(row, (index % 2 == 0) ? 'even' : 'odd');
	row.onmouseover = function() {
		Element.addClassName(this, 'highlight');
	};
	row.onmouseout = function() {
		Element.removeClassName(this, 'highlight');
	};
	
	var objectId = getObjectId(row);
	var defaultEnabled = false;
	
	var cmds = findElements(row, 'command-.*');
	for (var i = 0; i < cmds.length; i++) {
		cmds[i].objectId = objectId;
		cmds[i].rowIndex = index;
		if (defaultCommand != '' && Element.hasClassName(cmds[i], defaultCommand)) {
			defaultEnabled = true;
		}
	}
	
	if (defaultEnabled) {	
		var data = findElements(row, 'data');
		for (var i = 0; i < data.length; i++) {
			if (data[i].getElementsByTagName('a').length == 0) {
				data[i].objectId = objectId;
				data[i].rowIndex = index;
				data[i].command = defaultCommand;
				Element.addClassName(data[i], 'defaultCommand');
			}
		}
	}
}

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
 * Returns the objectId for the given element by inspecting the id attribute. 
 * Example: id="object-23" would return 23.
 */
function getObjectId(e) {
	if (e.id && e.id.indexOf('object-') == 0) {
		return e.id.substring(7); 
	}
	return null;
}

/**
 * Like prototype's document.getElementsByClassName() but without XPath
 * which allows us to use regular expressions within the className.
 */
function findElements(parent, className) {
	var children = parent.getElementsByTagName('*');
	var elements = [];
	for (var i = 0, length = children.length; i < length; i++) {
		var child = children[i];
		if (Element.hasClassName(child, className)) {
			elements.push(Element.extend(child));
		}
	}
	return elements;
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
		if (this.rowIndex != null) url += '&rowIndex=' + this.rowIndex;
		sendCommandRequest(url);
		return false;
	}
}

function sendCommandRequest(url) {
	document.body.style.cursor = 'wait';
	new Ajax.Request(url, {
		method: 'get', 
		onComplete: processCommandResponse
	});
}

function processCommandResponse(req) {
	document.body.style.cursor = 'default';
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
