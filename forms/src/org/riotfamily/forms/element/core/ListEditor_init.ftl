Sortable.create('${element.items.id}', {
	handle: 'handle',
	starteffect: false,
	format: /(.*)/,
	onUpdate: function(element) {
		var itemOrder = $('${element.id}-order');
		itemOrder.value = Sortable.sequence(element).join(',') + ',';
	}
});
