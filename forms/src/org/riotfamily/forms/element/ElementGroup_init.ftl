(function() {
	$('${group.renderModel.expandButton.eventTriggerId}').observe('click', function() {
		this.collapsed = !this.collapsed;
		if (this.collapsed) {
			this.addClassName('button-expand');
			this.removeClassName('button-collapse');
			$('${group.id}-elements').hide();
			this.value = '${group.renderModel.expandButton.expandLabel}';
			this.blur();			
		}
		else {
			this.addClassName('button-collapse');
			this.removeClassName('button-expand');
			this.value = '${group.renderModel.expandButton.collapseLabel}';
			var e = $('${group.id}-elements')
			e.show();
			e = e.down('input');
			if (e) e.focus(); else this.blur();
		}
	});
})();
