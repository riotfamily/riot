(function() {
	var button = $('${element.renderModel.expandButton.id}');
	button.observe('click', function() {
		this.collapsed = !this.collapsed;
		if (this.collapsed) {
			this.addClassName('button-expand');
			this.removeClassName('button-collapse');
			$('${element.id}-elements').hide();
			this.blur();			
		}
		else {
			this.addClassName('button-collapse');
			this.removeClassName('button-expand');
			var e = $('${element.id}-elements')
			e.show();
			e = e.down('input');
			if (e) e.focus(); else this.blur();
		}
	}.bind(button));
})();
