var RiotList = Class.create({

	initialize: function(key) {
		this.key = key;
		this.selection = [];
	},

	render: function(target, commandTarget, itemCommandTarget, expandedId, filterForm) {
		target = $(target);
		this.table = new Element('table')
			.insert(new Element('thead').insert(this.headRow = new Element('tr')))
			.insert(this.tbody = new Element('tbody'));
		
		target.insert(this.table);
		this.pager = new Pager(target, this.gotoPage.bind(this));
		if (filterForm) {
			this.filterForm = $(filterForm);
		}
		
		Event.observe(window, 'resize', this.resizeColumns.bind(this));
		Event.observe(document, 'click', this.clearSelection.bind(this));

		if (commandTarget && $(commandTarget)) {
			this.commandTarget = new Element('div');
			$(commandTarget).appendChild(this.commandTarget);
		}
		
		if (itemCommandTarget && $(itemCommandTarget)) {
			this.itemCommandTarget = new Element('div');
			$(itemCommandTarget).appendChild(this.itemCommandTarget);
		}
		ListService.getModel(this.key, expandedId, this.renderTable.bind(this));
	},

	renderFormCommands: function(item, target) {
		this.selection = [item];
		var handler = this.execItemCommand.bind(this);
		ListService.getFormCommands(this.key, item.objectId, this.renderCommands.bind(this, $(target), handler));
	},

	renderTable: function(model) {
		this.columns = [];
		this.headings = {};
		this.model = model;
		this.table.addClassName(model.cssClass);
		model.columns.each(this.addColumnHeading.bind(this));
		this.columns.last().addClassName('col-last');
		if (!model.instantAction) {
			this.renderListCommands(model.commandButtons);
		}
		if (model.commandButtons.length > 0) {
			this.defaultCommand = model.commandButtons[0].id;
		}
		this.updateFilter(model);
	},

	addColumnHeading: function(col) {
		var label;
		var className = 'col-' + (this.columns.length + 1) + ' ' + col.cssClass;
		var label = new Element('span').insert(col.heading || '');
		var th = new Element('th', {className: className}).insert(label);
		
		th.property = col.property;
		this.columns.push(th);
		this.headings[col.property] = label;
		if (col.sortable) {
			th.addClassName('sortable');
			Event.observe(th, 'click', this.sort.bindAsEventListener(this));
			this.updateSortIndicator(col);
		}
		this.headRow.appendChild(th);
	},
	
	renderListCommands: function(commands) {
		var handler = this.execCommand.bind(this);
		this.commandButtons = this.renderCommands(this.commandTarget, handler, commands);
	},
	
	renderCommands: function(el, handler, commands) {
		var buttons;
		if (el && commands) {
			el.update();
			var list = this;
			buttons = commands.collect(function(command) {
				var button = new CommandButton(list, command, handler);
				el.appendChild(button.element);
				return button;
			});
			if (buttons.length > 0) {
				el.up('.box').style.display = 'block';
			}
			var p = el.ancestors().find(function(el) { return el.getStyle('position') == 'fixed'});
			if (p && p.offsetTop + p.offsetHeight > document.viewport.getHeight()) {
				p.style.position = 'absolute';
			}
		}
		return buttons;
	},
	
	updateColsAndRows: function(model) {
		model.columns.each(this.updateSortIndicator.bind(this));
		this.updateRows(model);
	},

	updateRowsAndPager: function(model) {
		this.updateRows(model);
		this.pager.update(model.currentPage, model.pages);
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
			ListRow.create(this, null, model.items[i]); 
		}
		this.updateCommandStates();
	},
	
	execCommand: function(commandId) {
		if (this.setBusy()) {
			ListService.execCommand(this.key, commandId, this.selection,
					this.processCommandResult.bind(this));
		}
	},
		
	refreshListCommands: function() {
		ListService.getListCommands(this.key, this.renderListCommands.bind(this));
	},
	
	refreshSiblings: function(objectId) {
		if (objectId) {
			var tr = $('item-' + objectId);
			if (tr && tr.parentRow) {
				tr.parentRow.refreshChildren();
				return;
			}
		}
		ListService.getModel(this.key, null, this.updateRowsAndPager.bind(this));
	},
		
	getSelectionIndex: function(item) {
		for (var i = 0; i < this.selection.length; i++) {
			if (this.selection[i].objectId == item.objectId) {
				return i;
			}
		}
		return -1;
	},
	
	isSelected: function(item) {
		return getSelectionIndex(item) != -1;
	},
	
	toggleSelection: function(tr) {
		if (tr.hasClassName('selected')) {
			this.unselectRow(tr);
		}
		else {
			this.selectRow(tr);
		}
	},
	
	selectRow: function(tr) {
		if (!tr.hasClassName('selected')) {
			this.selection.push(tr.item);
			tr.addClassName('selected');
			if (tr.hasClassName('highlight')) {
				tr.addClassName('highlight-selected');	
			}
			this.updateCommandStates();
		}
	},
	
	unselectRow: function(tr) {
		if (tr.hasClassName('selected')) {
			this.selection = this.selection.reject(function(i) {
				return i.objectId == tr.item.objectId;
			});
			tr.removeClassName('selected');
			tr.removeClassName('highlight-selected');
			this.updateCommandStates();
		}
	},
	
	clearSelection: function() {
		this.selection = [];
		this.tbody.select('tr.selected').invoke('removeClassName', 'selected');
		this.updateCommandStates();
	},
	
	updateCommandStates: function() {
		if (this.commandButtons) {
			ListService.getEnabledCommands(this.key, this.selection, 
					this.enableButtons.bind(this));
		}
	},
	
	enableButtons: function(enabled) {
		for (var i = 0; i < this.commandButtons.length; i++) {
			var button = this.commandButtons[i];
			button.setEnabled(enabled.include(button.command.id));
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
			else if (result.action == 'refreshListCommands') {
				this.refreshListCommands();
			}
			else if (result.action == 'confirm') {
				if (confirm(result.message)) {
					this.execItemCommand(result.commandId, true);
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
				var win;
				if (result.arguments) {
					 win = window.open(result.url, result.windowName || 'commandPopup', result.arguments);
				}
				else {
					win = window.open(result.url, result.windowName || 'commandPopup');
				}
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
			else if (result.action == 'dialog') {
				new riot.window.Dialog(result);
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
	
	handleInput: function(formKey) {
		ListService.handleInput(this.key, formKey, this.selection,
				this.processCommandResult.bind(this));	
	},
	
	setBusy: function() {
		if (this.busy) {
			return false;
		}
		this.busy = true;
		Element.addClassName(document.body, 'busy');
		return true;
	},
	
	setIdle: function() {
		if (!this.busy) {
			return false;
		}
		this.busy = false;
		Element.removeClassName(document.body, 'busy');
		return true;
	}

});

var ListRow = {
	create: function(list, parentRow, row) {
		var tr = Object.extend(new Element('tr'), {
			list: list, 
			item: row, 
			id: 'item-' + row.objectId,
			parentRow: parentRow,
			level: parentRow ? parentRow.level + 1 : 0
		});
		Object.extend(tr, ListRow.Methods);
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
			var cell = new Element('td', {className: list.columns[i].className}).insert(row.columns[i]);
			tr.appendChild(cell);
			if (list.model.tree && i == 0) {
				cell.insert({top: 
					new Element('span', {className: 'expand'})
					.setStyle({marginLeft: (tr.level * 22) + 'px'})
					.observe('click', tr.toggleChildren.bindAsEventListener(tr))
				});
			}
		}

		var i = list.getSelectionIndex(row); 
		if (i != -1) {
			tr.addClassName('selected');
			list.selection[i] = row;
		}
		
		if (Prototype.Browser.IE) {
			//Prevent text selection in unselected items
			tr.onmousedown = function() {
				this.selectedOnMouseDown = this.hasClassName('selected') && !event.ctrlKey; 
			};
			tr.onselectstart = function() {
				return this.selectedOnMouseDown; 
			};
		}
		
		if (list.model.instantAction) {
			tr.observe('click', tr.execDefaultCommand.bindAsEventListener(tr));
		}
		else {
			tr.observe('click', tr.toggle.bindAsEventListener(tr));
			tr.observe('dblclick', tr.execDefaultCommand.bindAsEventListener(tr));
		}
		
		if (parentRow) {
			parentRow.insert({after: tr});
			parentRow.childRows.push(tr);
		}
		else {
			list.tbody.appendChild(tr);
		}
		
		if (row.children) {
			tr.expanded = true;
			tr.addClassName('expanded');
			tr.addChildren(row.children);
			row.children = null;
		}
	},
		
	Methods: {
	
		toggle: function(ev) {
			if (!ev.ctrlKey && !ev.metaKey) {
				this.list.clearSelection();
			}
			this.list.toggleSelection(this);
			ev.stop();
		},
		
		execDefaultCommand: function() {
			if (this.list.commandButtons) {
				if (this.list.selection.length == 0) {
					this.list.selection = [this.item];
				}
				this.list.updateCommandStates();
				this.list.commandButtons[0].onclick();
			}
		},
		
		remove: function() {
			this.removeChildren();
			this.parentNode.removeChild(this);
		},
		
		removeChildren: function() {
			if (this.childRows) {
				this.childRows.invoke('remove');
				this.childRows = null;
			}
		},
				
		addChildren: function(items) {
			this.childRows = [];
			for (var i = items.length - 1; i >= 0; i--) {
				ListRow.create(this.list, this, items[i]);
			}
			this.removeClassName('expanding');
		},
		
		toggleChildren: function(event) {
			event.stop();
			if (this.item.expandable) {
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
				this.addClassName('expanding');
				ListService.getChildren(this.list.key, this.item.objectId, this.addChildren.bind(this));
			}
		},
		
		collapse: function() {
			if (this.expanded) {
				this.expanded = false;
				this.removeClassName('expanded');
				this.childRows.each(this.list.unselectRow.bind(this.list));
				this.removeChildren();
			}
		},
				
		replaceChildren: function(items) {
			this.removeChildren();
			this.addChildren(items);
			this.list.updateCommandStates();
		},
		
		refreshChildren: function() {
			ListService.getChildren(this.list.key, this.item.objectId, this.replaceChildren.bind(this));
		},
		
		onmouseover: function() {
			this.addClassName('highlight');
			if (this.hasClassName('selected')) {
				this.addClassName('highlight-selected');
			}
		},
		
		onmouseout: function() {
			this.removeClassName('highlight').removeClassName('highlight-selected');
		}
		
	}
}

var CommandButton = Class.create({
	initialize: function(list, command, handler) {
		this.list = list;
		this.command = command;
		this.handler = handler;
		this.element = new Element('a', {href: '#'}).addClassName('action')
			.insert(new Element('span').addClassName('icon action-' + command.styleClass))
			.insert(new Element('span').addClassName('label')
				.insert(new Element('div').insert(command.label))
			).observe('click', this.onclick.bindAsEventListener(this));
		
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
			this.handler(this.command.id);
		}
		if (event) {
			event.stop();
		}
	}
});

dwr.engine.setTextHtmlHandler(function() {
	location.reload();
});

dwr.engine.setErrorHandler(function(err, ex) {
	if (ex.javaClassName == 'org.riotfamily.core.security.PermissionDeniedException'
			&& ex.permissionRequestUrl) {
		
		location.href = top.contextPath + ex.permissionRequestUrl;
	}
	else {
		list.setIdle();
		alert(ex.message);
	}
});

dwr.engine.setPreHook(function() {
	if (top.setLoading) top.setLoading(true);
});

dwr.engine.setPostHook(function() {
   if (top.setLoading) top.setLoading(false);
}); 
