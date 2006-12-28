var RiotList = Class.create();
RiotList.prototype = {

	initialize: function(editorId, parentId) {
		this.editorId = editorId;
		this.parentId = parentId;

		this.table = $('list');
		RBuilder.node('thead', {parent: this.table}, 
			this.headRow = RBuilder.node('tr')
		);
		this.tbody = RBuilder.node('tbody', {parent: this.table});
		
		ListService.getTable(editorId, parentId, this.renderTable.bind(this));
	},
	
	renderTable: function(data) {
		this.headings = {};
		data.columns.each(this.addColumn.bind(this));
		var th = RBuilder.node('th', {className: 'commands', parent: this.headRow,
			style: { width: data.itemCommandCount * 34 + 'px' }});
			
		this.updateRows(data.rows);
	},
	
	updateTable: function(data) {
		data.columns.each(this.updateSortIndicator.bind(this));
		this.updateRows(data.rows);
	},
	
	addColumn: function(col) {
		var label;
		var th = RBuilder.node('th', { property: col.property }, 
			label = RBuilder.node('span', { property: col.property }, col.heading)
		);
		this.headings[col.property] = label;
		if (col.sortable) {
			th.className = 'sortable';
			Event.observe(th, 'click', this.sort.bindAsEventListener(this));
			this.updateSortIndicator(col);
		}
		this.headRow.appendChild(th);
	},
	
	updateSortIndicator: function(col) {
		var e = this.headings[col.property];
		if (col.sorted) {
			e.className = col.ascending ? 'sorted-asc' : 'sorted-desc';
		}
		else {
			e.className = '';
		}	
	},
	
	sort: function(event) {
		var property = Event.element(event).property;
		ListService.sort(this.editorId, this.parentId, property, this.updateTable.bind(this));
	},
	
	filter: function(filter) {
		ListService.filter(this.editorId, this.parentId, filter, this.updateRows.bind(this));
	},
	
	updateRows: function(rows) {
		this.tbody.innerHTML = '';
		rows.each(this.addRow.bind(this));
	},
	
	addRow: function(row) {
		var tr = RBuilder.node('tr');
		RElement.hoverHighlight(tr, 'highlight');
		row.columns.each(function(data) {
			RBuilder.node('td', { innerHTML: data, parent: tr });
		});
		var td = RBuilder.node('td', { className: 'commands' });
		this.appendItemCommands(td, row);
		tr.appendChild(td);
		this.tbody.appendChild(tr);
	},
	
	appendItemCommands: function(el, item) {
		var handler = this.execItemCommand.bindAsEventListener(this);
		item.commands.each(function(command) {
			var a = RBuilder.node('a', { href: '#', item: item, 
					className: 'action action-' + command.action });
					
			if (command.enabled) {
				a.command = command;
				a.addClassName('command-enabled');
				Event.observe(a, 'click', handler);
			}
			else {
				a.onclick = Event.stop;
			}
			el.appendChild(a);
		});
	},
	
	execItemCommand: function(event) {
		var a = Event.element(event);
		this.execCommand(a.item, a.command.id, false);
		Event.stop(event);
	},
	
	execCommand: function(item, commandId, confirmed) {
		ListService.execCommand(this.editorId, this.parentId, 
				item, commandId, confirmed, 
				this.processCommandResult.bind(this));
	},
	
	processCommandResult: function(result) {
		if (result.action == 'confirm') {
			if (confirm(result.message)) {
				this.execCommand(result.item, result.commandId, true);
			}
		}
		else if (result.action == 'gotoUrl') {
			if (result.replace) {
				window[result.target].location.replace(result.url);
			}
			else {
				window[result.target].location.href = result.url;
			}
		}
		else if (result.action == 'popup') {
			var win = window.open(result.url, result.windowName || 'commandPopup');
			if (!win) {
				alert(result.popupBlockerMessage || 'The Popup has been blocked by the browser.');
			}
		}
		else if (result.action == 'reload') {
			window.location.reload();
		}
		else if (result.action == 'eval') {
			eval(result.script);
		}
		else if (result.action == 'setRowStyle') {
			alert(result.objectId + ': ' + result.rowStyle);
		}
	}

}