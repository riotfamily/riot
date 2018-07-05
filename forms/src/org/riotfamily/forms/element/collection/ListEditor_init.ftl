Sortable.create('${items.id}', {
	handle: 'handle',
	format: /(.*)/,
	onUpdate: function(element) {
		var itemOrder = $('${items.id}-order');
		itemOrder.value = Sortable.sequence(element).join(',') + ',';
	}
});
Draggables.addObserver({
	onStart: function(eventName, draggable, event) {
		if (window.beforeListMove) beforeListMove();
	},
	onDrag: function(eventName, draggable, event) {
	},
	onEnd: function(eventName, draggable, event) {
		if (window.afterListMove) afterListMove();
	}
});