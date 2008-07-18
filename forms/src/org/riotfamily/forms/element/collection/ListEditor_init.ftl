Sortable.create('${items.id}', {
	handle: 'handle',
	format: /(.*)/,
	onUpdate: function(element) {
		var itemOrder = $('${element.id}-order');
		itemOrder.value = Sortable.sequence(element).join(',') + ',';
	}
});