if (!window.riot) var riot = {};

riot.list = (function($) {
	
	var options;
	var model, columns, headings;
	var $table, $focusedRow, lastClickedIndex;
	
	function renderTable(m) {
		columns = [];
		headings = {};
		model = m;
		$table.addClass(m.cssClass);
		$.each(m.columns, addColumnHeading);
		$.each(model.columns, updateSortIndicator);
		
		if (!model.instantAction) {
			$.each(model.commandButtons, function() {
				createButton(this).appendTo(options.commandTarget);
			})
			$('a.action span.icon').fixpngs();
		}
		/*
		if (model.commandButtons.length > 0) {
			defaultCommand = model.commandButtons[0].id;
		}
		*/
		renderItems(m.items);
		focusRow($table.find('tbody tr:first'));
	}
	
	function addColumnHeading(i, col) {
		var className = 'col-' + (i + 1) + ' ' + col.cssClass;
		var $label = $('<span>').html(col.heading || '');
		var $th = $('<th>').addClass(className).append($label);
		if (col.sortable) {
			$th.addClass('sortable').click(function() {
				ListService.sort(options.key, col.property, function(model) {
					renderItems(model.items);
					$.each(model.columns, updateSortIndicator);
				});
			});
		}
		$table.find('thead > tr').append($th).find('th:last').addClass('col-last');
	}
	
	function updateSortIndicator(i, col) {
		$table.find('th span').removeClass('sorted-asc sorted-desc');
		if (col.sorted) {
			$table.find('th span').eq(i)
				.toggleClass('sorted-asc', col.ascending)
				.toggleClass('sorted-desc', !col.ascending);
		}
	}

	
	function renderItems(items) {
		var rows = createRows(items, 0);
		rows.appendTo($table.find('tbody').empty());
	}
	
	function createRows(items, level) {
		return $().add($.map(items, function(item) {
			var lightweightItem = {
					objectId: item.objectId,
					parentNodeId: item.parentNodeId 
			};
			var $tr = $('<tr>').addClass('level-' + level)
				.addClass(item.expandable ? 'expandable' : 'leaf')
				.data({item: lightweightItem, level: level})
				.click(toggleSelection);
			
			$.each(item.columns, function(i, data) {
				var className = $table.find('thead th').get(i).className;
				var $td = $('<td>').addClass(className).html(data).appendTo($tr);
				if (i == 0) {
					if (model.tree) {
						$td.prepend($('<span>').addClass('expand')
							.css({marginLeft: ((level || 0) * 22) + 'px'})
							.click(toggleChildren));
					}
					$td.prepend($('<input type="checkbox">')
							.click(toggleSelection)
							.focus(function() { focusRow($tr) }));
				}
			});
			return $tr;
		}));
	}
	
	function toggleSelection(event) {
		event.stopPropagation();
		var $tr = $(this).closest('tr');
		var $rows = $tr.parent().children();
		var i = $tr.index();
		var checked = !$tr.is('.selected');
		
		function setState($row, selected) {
			$row.toggleClass('selected', selected).find(':checkbox:first').attr('checked', selected);
		}

		if ((this.tagName == 'TR' && !event.metaKey && !event.altKey && !event.ctrlKey) || event.shiftKey) {
			$rows.each(function() { setState($(this), false) });
		}
		
		if (event.shiftKey && lastClickedIndex !== undefined) {
			var start = Math.min(i, lastClickedIndex);
			var end = Math.max(i, lastClickedIndex) + 1;
			$rows.slice(start, end).each(function() {
				setState($(this), true);
			});
		}
		else {
			setState($tr, checked);
			lastClickedIndex = i;
		}
		focusRow($tr);
		updateCommandStates();
	}
	
	function toggleChildren(event) {
		var $tr = $(this).closest('tr');
		if ($tr.is('.expanded')) {
			collapse($tr);
		}
		else if ($tr.is('.expandable')) {
			expand($tr);
		}
	}
	
	function focusRow($tr) {
		if ($tr.length > 0) {
			if ($focusedRow) {
				$focusedRow.removeClass('highlight highlight-selected');
			}
			$tr.addClass('highlight');
			if ($tr.is('.selected')) {
				$tr.addClass('highlight-selected');
			}
			$tr.find(':checkbox:first').get(0).focus();
			$focusedRow = $tr;
			return true;
		}
		return false;
	}
	
	function expand($tr) {
		if ($tr.is('.expandable') && !$tr.is('.expanding') && !$tr.is('.expanded')) {
			$tr.addClass('expanding')
			ListService.getChildren(options.key, $tr.data().item.objectId, function(items) { 
				$tr.removeClass('expanding');
				if (items.length == 0) {
					$tr.removeClass('expandable');
					$tr.addClass('leaf');
				}
				else {
					var level = $tr.data().level || 0;
					createRows(items, level + 1).insertAfter($tr);
					$tr.addClass('expanded');
				}
			});
			return true;
		}
		return false;
	}
	
	function collapse($tr) {
		if ($tr.is('.expanded')) {
			$tr.removeClass('expanded').nextUntil('.level-' + $tr.data().level).remove();
			return true;
		}
		return false;
	}
	
	function handleKeyEvent(event) {
		switch (event.keyCode) {
		case 37: 
			if (!collapse($focusedRow)) {
				var prev = $focusedRow.prevAll('.level-' + ($focusedRow.data().level - 1) + ':first');
				focusRow(prev);
			}
			break;
				
		case 38:
			if (focusRow($focusedRow.prev())) {
				event.preventDefault();
			}
			break;
				
		case 39:
			expand($focusedRow);
			break;
			
		case 40:
			if (focusRow($focusedRow.next())) {
				event.preventDefault();
			}
			break;
		}
	}
	
	//
	
	function updateCommandStates() {
		if (model.commandButtons) {
			var selection = $('tr.selected').map(function() { return $(this).data().item }).get();
			ListService.getEnabledCommands(options.key, selection, function(enabled) {
				$('a.action').each(function() {
					var on = $.inArray(this.id, enabled) != -1;
					$(this).toggleClass('enabled', on).toggleClass('disabled', !on);
				});
			});
		}
	}
	
	var handlers = {
		
		batch: function(result) {
			dwr.engine.beginBatch();
			$.each(result.batch, processCommandResult);
			dwr.engine.endBatch();
		},
		
		refreshList: function(result) {
			refreshList(result.objectId, result.refreshAll);
		},
		
		updateCommands: function(result) {
			updateCommandStates();
		},
		
		gotoUrl: function(result) {
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
		
		dialog: function(result) {
			var $dlg;
			if (result.url) {
				$dlg = $('<iframe>', {src: result.url}).load(function() { $dlg.dialog('open') });
			}
			else {
				$dlg = $('<div>').append(result.content);
			}
			$dlg.dialog({title: result.title, autoOpen: !result.url, close: function(event, ui) { $dlg.remove() }}).appendTo('body');
		},
		
		notification: function(result) {
			riot.showNotification(result);
		},
		
		reload: function(result) {
			window.location.reload();
		},
		
		eval: function(result) {
			eval(result.script);
		},
		
		download: function(result) {
			dwr.engine.openInDownload(result.file);
		}
	}
	
	function processResult(result) {
		if (result) {
			var handler = handlers[result.action];
			if (handler) {
				handler(result);
			}
		}
	}
	
	function execCommand(commandId) {
		var selection = $('tr.selected').map(function() { return $(this).data().item }).get();
		ListService.execCommand(options.key, commandId, selection, processResult);
	}
	
	function createButton(command) {
		return $('<a>', {href: '#'}).addClass('action').attr('id', command.id)
			.toggleClass('enabled', command.enabled)
			.append($('<span>').addClass('icon-and-label')
					.append($('<span>').addClass('icon').css('background-image', 'url(' + command.icon + ')'))
					.append($('<span>').addClass('label').html(command.label))
			).click(function() {
				if (!$(this).is('disabled')) {
					execCommand(command.id);
				}
			});
	}
	
	return {
		
		render: function (o) {
			options = o;
			$table = $('<table><thead><tr></tr></thead><tbody></tbody></table>').appendTo(options.target);
			$table.keydown(handleKeyEvent);
			ListService.getModel(o.key, o.expandedId, renderTable);
		}
	}

	
})(jQuery);

dwr.engine.setErrorHandler(function(err, ex) {
	throw ex;
});