var RiotList = Class.create({

	initialize: function(key) {
		this.key = key;
		this.selection = [];
		this.itemCommandClickHandler = this.onItemCommandClick.bindAsEventListener(this);
	},

	render: function(target, commandTarget, filterForm) {
		target = $(target);
		this.table = RBuilder.node('table', {parent: target},
			RBuilder.node('thead', null,
				this.headRow = RBuilder.node('tr')
			),
			this.tbody = RBuilder.node('tbody')
		);
		this.batchContainer = RBuilder.node('div', {parent: target, style: {display: 'none'}});
		this.pager = new Pager(target, this.gotoPage.bind(this));
		if (filterForm) {
			this.filterForm = $(filterForm);
		}
		Event.observe(window, 'resize', this.resizeColumns.bind(this));
		ListService.getModel(this.key, this.renderTable.bind(this, commandTarget));
	},

	renderFormCommands: function(objectId, target) {
		var item = {objectId: objectId};
		ListService.getFormCommands(this.key, objectId, this.appendCommands.bind(this, target, true, item, this.itemCommandClickHandler));
	},

	renderTable: function(commandTarget, model) {
		this.columns = [];
		this.headings = {};
		this.table.className = model.cssClass;
		this.hasBatchCommands = model.batchCommands && model.batchCommands.length > 0; 
		if (this.hasBatchCommands) {
			RBuilder.node('th', {className: 'check', parent: this.headRow, style: { width: '12px' }});
		}
		model.columns.each(this.addColumn.bind(this));
		var th = RBuilder.node('th', {className: 'commands', parent: this.headRow,
			style: { width: model.itemCommandCount * 32 + 'px' }});
		// Expand the column for IE browsers
		th.innerHTML = '<div style="width: ' + model.itemCommandCount * 32 + 'px;"></div>';			
		this.appendCommands(commandTarget, true, null, this.itemCommandClickHandler, model.listCommands);
		this.updateFilter(model);
		if (this.hasBatchCommands) {
			this.batchContainer.addClassName('commands batchCommands');
			this.table.addClassName('selectable');
			var handler = this.onBatchCommandClick.bindAsEventListener(this);
			this.batchButtons = this.appendCommands(this.batchContainer, false, null, handler, model.batchCommands);
			this.updateBatchStates();
		}
	},

	updateColsAndRows: function(model) {
		model.columns.each(this.updateSortIndicator.bind(this));
		this.updateRows(model);
	},

	updateRowsAndPager: function(model) {
		this.updateRows(model);
		this.pager.update(model.currentPage, model.pages);
	},

	addColumn: function(col) {
		var label;
		var className = 'col-' + (this.columns.length + 1) + ' ' + col.cssClass;
		var th = RBuilder.node('th', {property: col.property, className: className},
			label = RBuilder.node('span', {innerHTML: col.heading})
		);
		this.columns.push(th);
		this.headings[col.property] = label;
		if (col.sortable) {
			th.addClassName('sortable');
			Event.observe(th, 'click', this.sort.bindAsEventListener(this));
			this.updateSortIndicator(col);
		}
		this.headRow.appendChild(th);
	},

	updateSortIndicator: function(col) {
		var e = this.headings[col.property];
		if (col.sorted) {
			e.toggleClassName('sorted-asc', col.ascending);
			e.toggleClassName('sorted-desc', !col.ascending);
		}
		else {
			e.removeClassName('sorted-asc');
			e.removeClassName('sorted-desc');
		}
	},

	sort: function(event) {
		var property = Event.findElement(event, 'th').property;
		ListService.sort(this.key, property, this.updateColsAndRows.bind(this));
	},

	filter: function(filter) {
		ListService.filter(this.key, filter, this.updateRowsAndPager.bind(this));
	},

	reset: function() {
		ListService.filter(this.key, null, this.updateFilter.bind(this));
	},

	updateFilter: function(model) {
		this.updateRowsAndPager(model);
		if (this.filterForm) {
			this.filterForm.update(model.filterFormHtml);
		}
	},

	resizeColumns: function() {
		if (this.columnsSized) {
			this.columns.each(function(th) {
				th.style.width = 'auto';
			});
			this.columnsSized = false;
		}
	},

	gotoPage: function(page) {
		if (!this.columnsSized) {
			this.columns.each(function(th) {
				var colWidth = th.offsetWidth - parseInt(th.getStyle('padding-left'))
					- parseInt(th.getStyle('padding-right'));

				th.style.width = colWidth + 'px';
			});
			this.columnsSized = true;
		}
		ListService.gotoPage(this.key, page, this.updateRowsAndPager.bind(this));
	},

	updateRows: function(model) {
		this.tbody.update();
		model.items.each(this.addRow.bind(this, model));
	},

	addRow: function(model, row) {
		var tr = RBuilder.node('tr', {item: row});
		Event.observe(tr, 'mouseover', tr.addClassName.bind(tr, 'highlight'));
		Event.observe(tr, 'mouseout', tr.removeClassName.bind(tr, 'highlight'));
		if (row.cssClass) {
			tr.addClassName(row.cssClass);
		}
		if (row.lastOnPage) {
			tr.addClassName('last');
		}
		
		if (this.hasBatchCommands) {
			tr.cb = RBuilder.node('input', {type: 'checkbox'});
			tr.cb.observe('click', this.toggleItem.bindAsEventListener(this, tr));
			RBuilder.node('td', {parent: tr, className: 'check'}, tr.cb).observe('click', this.toggleItem.bindAsEventListener(this, tr));
		}
		
		for (var i = 0; i < row.columns.length; i++) {
			var cell = RBuilder.node('td', {innerHTML: row.columns[i], parent: tr, className: this.columns[i].className});
			if (row.defaultCommand) {
				cell.observe('click', this.execCommand.bind(this, row, row.defaultCommand, false));
			}
		}

		var td = RBuilder.node('td', {className: 'commands highlight-default'});
		Event.observe(td, 'mouseover', td.removeClassName.bind(td, 'highlight-default'));
		Event.observe(td, 'mouseout', td.addClassName.bind(td, 'highlight-default'));

		this.appendCommands(td, false, row, this.itemCommandClickHandler, row.commands);

		row.commands.each(function(command) {
			if (command.itemStyleClass) {
				tr.addClassName(command.itemStyleClass);
			}
		});

		tr.appendChild(td);
		this.tbody.appendChild(tr);
	},

	appendCommands: function(el, renderLabel, item, handler, commands) {
		el = $(el);
		return commands.collect(function(command) {
			var button = new CommandButton(item, command, handler, renderLabel);
			el.appendChild(button.element);
			return button;
		});
	},

	onItemCommandClick: function(event) {
		var a = event.findElement('a');
		this.execCommand(a.item, a.command, false);
		event.stop();
	},
	
	onBatchCommandClick: function(event) {
		var a = event.findElement('a');
		this.execBatchCommand(a.command, false);
		event.stop();
	},
	
	execBatchCommand: function(command, confirmed) {
		if (this.setBusy()) {
			ListService.execBatchCommand(this.key, this.selection, command, confirmed,
					this.processCommandResult.bind(this));
		}
	},

	execCommand: function(item, command, confirmed) {
		if (this.setBusy()) {
			ListService.execCommand(this.key, item, command, confirmed,
					this.processCommandResult.bind(this));
		}
	},
	
	toggleItem: function(event, tr) {
		var cb = tr.cb;
		var item = tr.item; 
		var it = function(i) {
			return i.objectId == item.objectId;
		};
		if (!event || event.element() != cb) {
			cb.checked = !cb.checked;
		}
		if (cb.checked) {
			if (!this.selection.find(it)) {
				this.selection.push(item);
			}
		}
		else {
			this.selection = this.selection.reject(it);
		}
		this.updateBatchStates();
	},
	
	isActionEnabled: function(action, item) {
		for (var i = 0; i < item.commands.length; i++) {
			var cmd = item.commands[i];
			if (cmd.action == action && cmd.enabled) {
				return true;
			}
		}
		return false;
	},
	
	isActionEnabledForSelection: function(action) {
		if (this.selection.length == 0) {
			return false;
		}
		for (var i = 0; i < this.selection.length; i++) {
			var item = this.selection[i];
			if (!this.isActionEnabled(action, item)) {
				return false;
			}
		}
		return true;
	},
	
	updateBatchStates: function() {
		for (var i = 0; i < this.batchButtons.length; i++) {
			var button = this.batchButtons[i];
			var enabled = this.isActionEnabledForSelection(button.command.action);
			button.setEnabled(enabled);
		}
		if (this.selection.length > 0 && !this.batchContainer.visible()) {
			new Effect.Appear(this.batchContainer, {duration: 0.3});
		}	
		else if (this.selection.length == 0 && this.batchContainer.visible()) {
			new Effect.Fade(this.batchContainer, {duration: 0.3});
		}	
	},
	
	processCommandResult: function(result) {
		this.setIdle();
		if (result) {
			if (result.action == 'confirm') {
				if (confirm(result.message)) {
					this.execCommand(result.item, result.command, true);
				}
			}
			if (result.action == 'confirmBatch') {
				if (confirm(result.message)) {
					this.execBatchCommand(result.command, true);
				}
			}
			else if (result.action == 'gotoUrl') {
				var win = eval(result.target);
				if (result.replace) {
					win.location.replace(result.url);
				}
				else {
					win.location.href = result.url;
				}
			}
			else if (result.action == 'popup') {
				var win = window.open(result.url, result.windowName || 'commandPopup');
				if (!win) {
					alert(result.popupBlockerMessage || 'The Popup has been blocked by the browser.');
				}
				else {
					if (win.focusLost) {
						win.close();
						win = window.open(result.url, result.windowName || 'commandPopup');
					}
					win.focus();
					win.onblur = function() {
						this.focusLost = true;
					}
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
	},
	
	setBusy: function() {
		if (this.busy) {
			return false;
		}
		this.busy = true;
		if (this.table) {
			this.table.addClassName('busy');
		}
		return true;
	},
	setIdle: function() {
		if (!this.busy) {
			return false;
		}
		this.busy = false;
		if (this.table) {
			this.table.removeClassName('busy');
		}
		return true;
	}

});

var CommandButton = Class.create({
	initialize: function(item, command, handler, renderLabel) {
		this.item = item;
		this.command = command;
		this.handler = handler;
		this.element = RBuilder.node('a', {href: '#', item: item, command: command,
				className: 'action action-' + command.styleClass});

		if (item && item.defaultCommand == command) {
			this.element.addClassName('default');
		}
		if (renderLabel) {
			RBuilder.node('span', {className: 'label', parent: this.element, innerHTML: command.label});
		}
		else {
			this.element.title = command.label.stripTags();
		}
		this.element.observe('click', this.onclick.bindAsEventListener(this));
		this.setEnabled(command.enabled);
	},
	
	setEnabled: function(enabled) {
		this.enabled = enabled;
		if (enabled) {
			this.element.addClassName('enabled');
			this.element.removeClassName('disabled');
		}
		else {
			this.element.removeClassName('enabled');
			this.element.addClassName('disabled');	
		}
	},
	
	onclick: function(event) {
		if (this.enabled) {
			this.handler(event);
		}
		else {
			event.stop();
		}
	}
});

dwr.engine.setErrorHandler(function(err, ex) {
	if (ex.javaClassName == 'org.riotfamily.riot.list.ui.ListSessionExpiredException') {
		location.reload();
	}
	else {
		list.setIdle();
		alert(err);
	}
});

dwr.engine.setPreHook(function() {
	if (top.setLoading) top.setLoading(true);
});

dwr.engine.setPostHook(function() {
   if (top.setLoading) top.setLoading(false);
}); 
