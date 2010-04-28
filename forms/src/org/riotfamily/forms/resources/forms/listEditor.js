(function($) {
	$.fn.moveUp = function() {
		var prev = this.prev();
	    if (prev) this.remove().insertBefore(prev);
	    return this;
	};
	
	$.fn.moveDown = function() {
		var next = this.next();
	    if (next) this.remove().insertAfter(next);
	    return this;
	}
	
	$.fn.listEditor = function() {
		return this.each(function() {
			$(this).sortable({
				axis: 'y',
				handle: '.handle',
				update: function(event, ui) {
					var el = $(this);
					var order = $.map(el.sortable('toArray'), function(id) { 
						return id.replace(/.+_(.+)/, '$1');
					}).join(',');
					el.submitEvent('sort', order);
				}
			});
		});
	}
})(jQuery);

