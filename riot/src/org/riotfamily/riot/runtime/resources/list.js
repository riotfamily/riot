var RiotList = Class.create();
RiotList.prototype = {

	initialize: function(key) {
		this.key = key;
	},
	
	render: function(target, commandTarget) {
		var table = RBuilder.node('table', {parent: $(target)}, 
			RBuilder.node('thead', null, 
				this.headRow = RBuilder.node('tr')
			),
			this.tbody = RBuilder.node('tbody')
		);
		ListService.getTable(this.key, this.renderTable.bind(this, commandTarget));
	},
	
	renderFormCommands: function(objectId, target) {
		var item = {objectId: objectId};
		ListService.getFormCommands(this.key, objectId, this.appendCommands.bind(this, target, true, item));
	},
		
	renderTable: function(commandTarget, data) {
		this.headings = {};
		data.columns.each(this.addColumn.bind(this));
		var th = RBuilder.node('th', {className: 'commands', parent: this.headRow,
			style: { width: data.itemCommandCount * 34 + 'px' }});
		
		this.appendCommands(commandTarget, true, null, data.listCommands);	
		this.updateRows(data.rows);
	},
	
	updateTable: function(data) {
		data.columns.each(this.updateSortIndicator.bind(this));
		this.updateRows(data.rows);
	},
	
	addColumn: function(col) {
		var label;
		var th = RBuilder.node('th', {property: col.property}, 
			label = RBuilder.node('span', null, col.heading)
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
		var property = Event.findElement(event, 'th').property;
		ListService.sort(this.key, property, this.updateTable.bind(this));
	},
	
	filter: function(filter) {
		ListService.filter(this.key, filter, this.updateRows.bind(this));
	},
	
	updateRows: function(rows) {
		this.tbody.innerHTML = '';
		rows.each(this.addRow.bind(this));
	},
	
	addRow: function(row) {
		var tr = RBuilder.node('tr');
		Event.observe(tr, 'mouseover', tr.addClassName.bind(tr, 'highlight'));
		Event.observe(tr, 'mouseout', tr.removeClassName.bind(tr, 'highlight'));
		
		if (row.defaultCommandId) {
			Event.observe(tr, 'click', this.execCommand.bind(this, row, row.defaultCommandId, false));
		}
		
		row.columns.each(function(data) {
			RBuilder.node('td', {innerHTML: data, parent: tr});
		});

		var td = RBuilder.node('td', {className: 'commands highlight-default'});
		Event.observe(td, 'mouseover', td.removeClassName.bind(td, 'highlight-default'));
		Event.observe(td, 'mouseout', td.addClassName.bind(td, 'highlight-default'));
		
		this.appendCommands(td, false, row, row.commands);
		tr.appendChild(td);
		this.tbody.appendChild(tr);
	},
	
	appendCommands: function(el, renderLabel, item, commands) {
		el = $(el);
		var handler = this.execItemCommand.bindAsEventListener(this);
		commands.each(function(command) {
			var a = RBuilder.node('a', {href: '#', item: item, 
					className: 'action action-' + command.action});
					
			if (command.enabled) {
				a.command = command;
				a.addClassName('enabled');
				if (item && item.defaultCommandId == command.id) {
					a.addClassName('default');
				}
				Event.observe(a, 'click', handler);
			}
			else {
				a.addClassName('disabled');
				a.onclick = Event.stop;
			}
			if (renderLabel) {
				RBuilder.node('span', {parent: a}, command.label);
			}
			el.appendChild(a);
		});
	},
	
	execItemCommand: function(event) {
		var a = Event.findElement(event, 'a');
		this.execCommand(a.item, a.command.id, false);
		Event.stop(event);
	},
	
	execCommand: function(item, commandId, confirmed) {
		ListService.execCommand(this.key, item, commandId, confirmed, 
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