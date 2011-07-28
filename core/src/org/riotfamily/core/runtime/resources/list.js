var RiotList = Class.create({

	initialize: function(key) {
		this.key = key;
		this.selection = [];
	},

	render: function(target, commandTarget, expandedId, filterForm) {
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
		document.observe('click', function() {
			this.clearSelection();
			this.updateCommandStates();
		}.bind(this));

		if (commandTarget && $(commandTarget)) {
			this.commandTarget = new Element('div');
			$(commandTarget).appendChild(this.commandTarget);
		}
		ListService.getModel(this.key, expandedId, this.renderTable.bind(this));
	},

	renderFormCommands: function(item, target) {
		this.selection = [item];
		ListService.getFormCommands(this.key, item, 
				this.renderCommands.bind(this, $(target), this.execCommand.bind(this)));
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
		
	refreshList: function(objectId, refreshAll) {
		if (refreshAll) {
			ListService.getModel(this.key, objectId, this.updateRowsAndPager.bind(this));
		}
		else {
			var tr = $('item-' + objectId);
			if (tr) {
				ListService.getChildren(this.key, tr.item.objectId, function(items) {
					tr.removeChildren();
					tr.addChildren(items);
					this.updateCommandStates();				
				}.bind(this));
				return;
			}
		}
		var screenlets = $('screenlets');
		if (screenlets) {
			ListService.renderScreenlets(this.key, function(html) {
				screenlets.update(html);
			});
		}
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
		}
	},
	
	unselectRow: function(tr) {
		if (tr.hasClassName('selected')) {
			this.selection = this.selection.reject(function(i) {
				return i.objectId == tr.item.objectId;
			});
			tr.removeClassName('selected');
			tr.removeClassName('highlight-selected');
		}
	},
	
	clearSelection: function() {
		this.selection = [];
		this.tbody.select('tr.selected').invoke('removeClassName', 'selected');
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
	
	handlers: {
		
		batch: function(list, result) {
			dwr.engine.beginBatch();
			result.batch.each(list.processCommandResult.bind(list));
			dwr.engine.endBatch();
		},
		
		refreshList: function(list, result) {
			list.refreshList(result.objectId, result.refreshAll);
		},
		
		updateCommands: function(list, result) {
			list.updateCommandStates();
		},
		
		gotoUrl: function(list, result) {
			var win = eval(result.target);
			if (result.replace) {
				win.location.replace(result.url);
			}
			else {
				win.location.href = result.url;
			}
		},
		
		popup: function(list, result) {
			var win;
			if (result.arguments) {
				 win = window.open(result.url, result.windowName || '_blank', result.arguments);
			}
			else {
				win = window.open(result.url, result.windowName || '_blank');
			}
			if (!win) {
				alert(result.popupBlockerMessage || 'The Popup has been blocked by the browser.');
			}
			else {
				try {
					if (win.focusLost) {
						win.close();
						win = window.open(result.url, result.windowName || 'commandPopup');
					}
					win.focus();
					win.onblur = function() {
						this.focusLost = true;
					}
				}
				catch (e) {
				}
			}
		},
		
		dialog: function(list, result) {
			new riot.window.Dialog(result);
		},
		
		notification: function(list, result) {
			riot.notification.show(result);
		},
		
		reload: function(list, result) {
			window.location.reload();
		},
		
		eval: function(list, result) {
			eval(result.script);
		},
		
		download: function(list, result) {
			dwr.engine.openInDownload(result.file);
		}
	},
	
	processCommandResult: function(result) {
		this.setIdle();
		if (result) {
			var handler = this.handlers[result.action];
			if (handler) {
				handler(this, result);
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
	create: function(list, parentRow, item) {
		
	// Create TR element
		var tr = Object.extend(new Element('tr'), {
			list: list, 
			item: item, 
			id: 'item-' + item.objectId,
			parentRow: parentRow,
			level: parentRow ? parentRow.level + 1 : 0
		});
		if (item.cssClass) {
			tr.addClassName(item.cssClass);
		}
		
		// Add methods
		Object.extend(tr, ListRow.Methods);
		
		tr.setExpandable(item.expandable);
		
		// Create the TD elements
		for (var i = 0; i < item.columns.length; i++) {
			var td = new Element('td', {className: list.columns[i].className}).insert(item.columns[i]);
			tr.appendChild(td);
			if (list.model.tree && i == 0) {
				td.insert({top: 
					new Element('span', {className: 'expand'})
					.setStyle({marginLeft: (tr.level * 22) + 'px'})
					.observe('click', tr.toggleChildren.bindAsEventListener(tr))
				});
			}
		}
		
		// Select row if part of current selection
		var i = list.getSelectionIndex(item); 
		if (i != -1) {
			tr.addClassName('selected');
			list.selection[i] = item;
		}

		//Prevent text selection in unselected items
		if (Prototype.Browser.IE) {
			tr.onmousedown = function() {
				this.selectedOnMouseDown = this.hasClassName('selected') && !event.ctrlKey; 
			};
			tr.onselectstart = function() {
				return this.selectedOnMouseDown; 
			};
		}
		
		// Register event listeners
		if (list.model.instantAction) {
			tr.observe('click', tr.execDefaultCommand.bindAsEventListener(tr));
		}
		else {
			tr.observe('click', tr.toggle.bindAsEventListener(tr));
			tr.observe('dblclick', tr.execDefaultCommand.bindAsEventListener(tr));
		}
		
		// Add to DOM
		if (parentRow) {
			parentRow.insert({after: tr});
			parentRow.childRows.push(tr);
		}
		else {
			list.tbody.appendChild(tr);
		}
		
		// Add children (if present in model) 
		if (item.children) {
			tr.expanded = true;
			tr.addClassName('expanded');
			tr.addChildren(item.children);
		}
		
		// Convert item to lightweight object
		delete item.columns;
		delete item.children;
		delete item.expandable;
	},
		
	Methods: {
	
		/**
		 * Click listener that toggles the selection state.
		 */
		toggle: function(ev) {
			if (!ev.ctrlKey && !ev.metaKey) {
				this.list.clearSelection();
			}
			this.list.toggleSelection(this);
			this.list.updateCommandStates();
			ev.stop();
		},
		
		/**
		 * Click listener to expand/collapse tree items.
		 */
		toggleChildren: function(ev) {
			ev.stop();
			if (this.expandable) {
				if (this.expanded) {
					this.collapse();
				}
				else {
					this.expand();
				}
			}
		},
		
		/**
		 * DoubleClick listener (or click listener in instantAction mode).
		 */
		execDefaultCommand: function() {
			if (this.list.commandButtons) {
				if (this.list.selection.length == 0) {
					this.list.selection = [this.item];
				}
				this.list.updateCommandStates();
				var b = this.list.commandButtons[0];
				b.handler(b.command.id); 
			}
		},
		
		/**
		 * Adds support :hover style in IE6.
		 */
		onmouseover: function() {
			this.addClassName('highlight');
			if (this.hasClassName('selected')) {
				this.addClassName('highlight-selected');
			}
		},
		
		/**
		 * Adds support :hover style in IE6.
		 */
		onmouseout: function() {
			this.removeClassName('highlight').removeClassName('highlight-selected');
		},
		
		/**
		 * Overwrites Prototype's Element.remove() method to also remove 
		 * the childRows. 
		 */
		remove: function() {
			this.removeChildren();
			this.parentNode.removeChild(this);
		},
		
		/**
		 * Invokes the the remove() method on all childRows. 
		 */
		removeChildren: function() {
			if (this.childRows) {
				this.childRows.invoke('remove');
				this.childRows = null;
			}
		},
			
		/**
		 * Callback method to create childRows.
		 */
		addChildren: function(items) {
			this.removeClassName('expanding');
			this.childRows = [];
			this.setExpandable(items.length > 0);
			this.setExpanded(items.length > 0);
			for (var i = items.length - 1; i >= 0; i--) {
				ListRow.create(this.list, this, items[i]);
			}
		},
		
		setExpandable: function(expandable) {
			this.expandable = expandable;
			if (expandable) {
				this.removeClassName('leaf');
				this.addClassName('expandable');
				
			}
			else {
				this.addClassName('leaf');
				this.removeClassName('expandable');
			}
		},
		
		setExpanded: function(expanded) {
			this.expanded = expanded;
			if (expanded) {
				this.addClassName('expanded');
			}
			else {
				this.removeClassName('expanded');
			}
		},
		
		expand: function() {
			if (!this.expanded) {
				this.setExpanded(true);
				this.addClassName('expanding');
				ListService.getChildren(this.list.key, this.item.objectId, this.addChildren.bind(this));
			}
		},
		
		collapse: function() {
			if (this.expanded) {
				this.setExpanded(false);
				this.childRows.each(this.list.unselectRow.bind(this.list));
				this.list.updateCommandStates();
				this.removeChildren();
			}
		}
	}
}

var CommandButton = Class.create({
	initialize: function(list, command, handler) {
		this.list = list;
		this.command = command;
		this.handler = handler;
		var style = 'background-image:url(' + command.icon 
			+ ');_background-image:none;_filter:progid:'
			+ 'DXImageTransform.Microsoft.AlphaImageLoader(src=\''
			+ command.icon + '\', sizingMethod=\'crop\')';
		
		this.element = new Element('a', {href: '#'}).addClassName('action')
			.insert(new Element('span').addClassName('icon-and-label')
				.insert(new Element('span', {style: style}).addClassName('icon'))
				.insert(new Element('span').addClassName('label').insert(command.label))
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
	if (ex.javaClassName) {
		if (ex.javaClassName == 'org.riotfamily.core.security.policy.PermissionDeniedException'
				&& ex.permissionRequestUrl) {
			
			new riot.window.Dialog({
				url: riot.contextPath + ex.permissionRequestUrl,
				minHeight : 255,
				closeButton: true,
				autoSize: true,
				onClose: list.setIdle.bind(list)
			});
			
		}
		else {
			list.setIdle();
			if (ex.message) {
				alert(ex.message);
			}
			else {
				alert(ex.javaClassName);
			}
		}
	}
	else {
		list.setIdle();
		throw ex;
	}
});

dwr.engine.setWarningHandler(function(err, ex) {
	if (window.console && console.log) {
		console.log(err);
	}
});
