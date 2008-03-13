var RiotList = Class.create({

	initialize: function(key) {
		this.key = key;
		this.selection = [];
		this.itemCommandClickHandler = this.onItemCommandClick.bindAsEventListener(this);
	},

	render: function(target, commandTarget, expandedId, filterForm) {
		target = $(target);
		this.table = RBuilder.node('table', {className: 'view-mode', parent: target},
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
		ListService.getModel(this.key, expandedId, this.renderTable.bind(this, commandTarget));
	},

	renderFormCommands: function(objectId, target) {
		var item = {objectId: objectId};
		ListService.getFormCommands(this.key, objectId, this.appendCommands.bind(this, target, true, item, this.itemCommandClickHandler));
	},

	renderTable: function(commandTarget, model) {
		this.columns = [];
		this.headings = {};
		this.tree = model.tree;
		this.texts = model.texts;
		this.table.addClassName(model.cssClass);
		this.hasBatchCommands = model.batchCommands && model.batchCommands.length > 0; 
		model.columns.each(this.addColumnHeading.bind(this));
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
			
			RBuilder.node('div', {className: 'selectionStatus'},
				this.selectionStatus = RBuilder.node('span', {className: 'itemCount'}),
				RBuilder.node('span', {className: 'clear'}, '[', this.texts['label.selection.clear'], ']').observe('click', this.clearSelection.bind(this))
			).appendTo(this.batchContainer);	
			
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

	addColumnHeading: function(col) {
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
		for (var i = 0; i < model.items.length; i++) {
			ListRow.create(this, model, null, model.items[i]); 
		}
		if (this.tree) {
			this.tbody.appendChild(ListRow.createCommandRow(this, 0, null));
		}
	},

	appendCommands: function(el, renderLabel, item, handler, commands) {
		el = $(el);
		var alwaysOn = item == null && this.tree;
		return commands.collect(function(command) {
			var button = new CommandButton(item, command, handler, renderLabel, alwaysOn);
			el.appendChild(button.element);
			return button;
		});
	},

	onItemCommandClick: function(event) {
		event.stop();
		var a = event.findElement('a');
		if (!this.selectParentMode) {
			if (this.tree && !a.item) {
				this.selectParent(a.command);
				var cancel = RBuilder.node('button', {}, this.texts['label.tree.cancelCommand']);
				this.dialog = RBuilder.node('div', {className: 'select-parent'}, 
					this.texts['label.tree.selectTarget'], cancel
				).hide().insertSelfAfter(a);
				cancel.observe('click', this.cancelParentSelection.bind(this));
				new Effect.BlindDown(this.dialog, {duration: 0.3});
			}
			else {
				this.execCommand(a.item, null, a.command, false);
			}
		}
	},
	
	selectParent: function(command) {
		this.selectParentMode = true;
		this.parentCommand = command;
		this.table.removeClassName('view-mode');
		this.table.addClassName('select-parent-mode');
		DWREngine.beginBatch();
		this.table.select('.parent-command').invoke('setup', command.id);
		DWREngine.endBatch();
	},
	
	cancelParentSelection: function() {
		this.table.addClassName('view-mode');
		this.table.removeClassName('select-parent-mode');
		this.tbody.select('tr.leaf').invoke('collapse');
		this.dialog.remove();
		this.dialog = null;
		this.selectParentMode = false;
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

	execParentCommand: function(parentId) {
		this.cancelParentSelection();
		this.execCommand(null, parentId, this.parentCommand, true);
	},
	
	execCommand: function(item, parentId, command, confirmed) {
		if (this.setBusy()) {
			if (item) {
				ListService.execItemCommand(this.key, item, command, confirmed,
						this.processCommandResult.bind(this));
			}
			else {
				ListService.execListCommand(this.key, parentId, command, confirmed,
						this.processCommandResult.bind(this));
			}
		}
	},
		
	refreshSiblings: function(objectId) {
		if (objectId) {
			var tr = $('item-' + objectId);
			if (tr.parentRow) {
				tr.parentRow.refreshChildren();
				return;
			}
		}
		ListService.getModel(this.key, null, this.updateRowsAndPager.bind(this));
	},
	
	isSelected: function(item) {
		return this.selection.find(function(i) {
			return i.objectId == item.objectId;
		});
	},
	
	toggleSelection: function(tr) {
		var cb = tr.cb;
		var item = tr.item; 
		if (cb.checked) {
			if (!this.isSelected(item)) {
				this.selection.push(item);
			}
		}
		else {
			this.selection = this.selection.reject(function(i) {
				return i.objectId == item.objectId;
			});
		}
		this.updateBatchStates();
	},
	
	clearSelection: function() {
		this.selection = [];
		this.tbody.select('input.check').each(function(cb) {
			cb.checked = false;
		});
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
		
		if (this.selection.length > 1) {
			this.selectionStatus.update(this.texts['label.selection.count'].interpolate(this.selection));	
		}
		else {
			this.selectionStatus.update();
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
			if (result.action == 'batch') {
				result.batch.each(this.processCommandResult.bind(this));
			}
			else if (result.action == 'refreshSiblings') {
				this.refreshSiblings(result.objectId);
			}
			else if (result.action == 'confirm') {
				if (confirm(result.message)) {
					this.execCommand(result.item, result.item.parentId, result.command, true);
				}
			}
			else if (result.action == 'confirmBatch') {
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
			else if (result.action == 'message') {
				alert(result.message);
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

var ListRow = {
	create: function(list, model, parentRow, row) {
		var tr = RBuilder.node('tr', {
			list: list, 
			item: row, 
			id: 'item-' + row.objectId,
			parentRow: parentRow,
			level: parentRow ? parentRow.level + 1 : 0
		});
		Object.extend(tr, ListRow.Methods);
		tr.setHoverClass('highlight');
		if (row.cssClass) {
			tr.addClassName(row.cssClass);
		}
		if (row.expandable) {
			tr.addClassName('expandable');
		}
		else {
			tr.addClassName('leaf');
		}
		
		for (var i = 0; i < row.columns.length; i++) {
			var cell = RBuilder.node('td', {innerHTML: row.columns[i], className: list.columns[i].className}).appendTo(tr);
			if (list.hasBatchCommands && i == 0) {
				tr.cb = RBuilder.node('input', {type: 'checkbox', className: 'check', checked: list.isSelected(row)})
					.observe('click', list.toggleSelection.bind(list, tr));
				cell.prependChild(tr.cb);
			}
			if (list.tree && i == 0) {
				var arrow = RBuilder.node('div', {
					parent: tr, className: 'expand', style: 'margin-left:' + (tr.level * 22) + 'px'
				});
				cell.prependChild(arrow);
				arrow.observe('click', tr.toggleChildren.bindAsEventListener(tr));
			}
		}

		var td = RBuilder.node('td', {className: 'commands'});

		list.appendCommands(td, false, row, list.itemCommandClickHandler, row.commands);

		row.commands.each(function(command) {
			if (command.itemStyleClass) {
				tr.addClassName(command.itemStyleClass);
			}
		});

		if (row.defaultCommand) {
			tr.observe('click', tr.execDefaultCommand.bindAsEventListener(tr));
		}
			
		tr.appendChild(td);
		if (parentRow) {
			tr.insertSelfAfter(parentRow);
			parentRow.childRows.push(tr);
		}
		else {
			tr.appendTo(list.tbody);
		}
		
		if (row.children) {
			tr.expanded = true;
			tr.addClassName('expanded');
			tr.addChildren(row.children);
			row.children = null;
		}
	},
	
	createCommandRow: function(list, level, item) {
		var parentId = item ? item.objectId : null;
		
		var arrow = RBuilder.node('div', {
			className: 'expand', 
			style: 'margin-left:' + ((level) * 22) + 'px;'
		});
		
		var icon = RBuilder.node('a', { href: '#', style: 'float:right'});
		var label = RBuilder.node('span', {className: 'label'});
		
		var tr = RBuilder.node('tr', {
				className: 'parent-command', 
				level: level,
				label: label,
				icon: icon,
				list: list,
				item: item,
				setup: function(commandId) {
					if (commandId) {
						ListService.getParentCommandState(this.list.key, commandId, this.item, this.foo.bind(this));
					}
				},
				foo: function(cmd) {
					this.label.innerHTML = cmd.label + ' ...';
					this.icon.className = 'action action-' + cmd.styleClass;
					this.label.parentNode.className = this.icon.parentNode.className = cmd.enabled ? 'enabled' : 'disabled';
				}
			}, 
			RBuilder.node('td', {colSpan: list.columns.length}, arrow, label),
			RBuilder.node('td', {}, icon)
		).setHoverClass('highlight-parent').observe('click', list.execParentCommand.bind(list, parentId));
		
		if (list.parentCommand) {
			tr.setup(list.parentCommand.id);
		}
		return tr;
	},
	
	Methods: {
	
		remove: function() {
			this.removeChildren();
			if (this.commandRow) {
				this.commandRow.remove();
			}
			this.parentNode.removeChild(this);
		},
		
		removeChildren: function() {
			if (this.childRows) {
				this.childRows.invoke('remove');
				this.childRows = null;
			}
		},
				
		addChildren: function(model) {
			this.childRows = [];
			for (var i = model.items.length - 1; i >= 0; i--) {
				ListRow.create(this.list, model, this, model.items[i]);
			}
		},
		
		toggleChildren: function(event) {
			event.stop();
			if (this.item.expandable || this.list.selectParentMode) {
				if (this.expanded) {
					this.collapse();
				}
				else {
					this.expand();
				}
			}
		},
		
		expand: function() {
			if (!this.expanded) {
				this.expanded = true;
				this.addClassName('expanded');
				if (this.commandRow) {
					this.commandRow.show();	
				}
				else {
					this.commandRow = ListRow.createCommandRow(this.list, this.level+1, this.item).insertSelfAfter(this);
				}
				ListService.getChildren(this.list.key, this.item.objectId, this.addChildren.bind(this));
			}
		},
		
		collapse: function() {
			if (this.expanded) {
				this.expanded = false;
				this.removeClassName('expanded');
				this.removeChildren();
				if (this.commandRow) {
					this.commandRow.hide();
				}
			}
		},
				
		replaceChildren: function(model) {
			this.removeChildren();
			this.addChildren(model);
		},
		
		refreshChildren: function() {
			ListService.getChildren(this.list.key, this.item.objectId, this.replaceChildren.bind(this));
		},
		
		execDefaultCommand: function(event) {
			if (event.element() != this.cb) {
				this.list.execCommand(this.item, null, this.item.defaultCommand, false);
			}
		}
	}
}

var CommandButton = Class.create({
	initialize: function(item, command, handler, renderLabel, enabled) {
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
		this.setEnabled(enabled || command.enabled);
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
		throw ex;
	}
});

dwr.engine.setPreHook(function() {
	if (top.setLoading) top.setLoading(true);
});

dwr.engine.setPostHook(function() {
   if (top.setLoading) top.setLoading(false);
}); 
