if (!window.riot) var riot = {};

riot.ListEditor = {
	init: function(id) {
		var ul = $(id).down('ul');
		Sortable.create(ul, {
			handle: 'handle',
			format: /item-(.*)/,
			onUpdate: function(element) {
				riot.form.submitEvent(element, 'sort', Sortable.sequence(element));
			}
		});
	}
}
