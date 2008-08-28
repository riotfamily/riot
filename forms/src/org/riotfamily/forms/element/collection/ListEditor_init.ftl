Sortable.create('${items.id}', {
	handle: 'handle',
	format: /(.*)/,
	onUpdate: function(element) {
		var itemOrder = $('${items.id}-order');
		itemOrder.value = Sortable.sequence(element).join(',') + ',';
	}
});