Element.addMethods('ul', {
	makeSortable: function(el) {
		Sortable.create(el, {
			handle: 'handle',
			format: /item-(.*)/,
			onUpdate: function(element) {
				riot.form.submitEvent(element, 'sort', Sortable.sequence(element));
			}
		});
	}
});