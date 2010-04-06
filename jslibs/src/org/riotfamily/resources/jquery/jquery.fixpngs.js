(function($) {
	
	var ie6 = $.browser.msie && parseInt($.browser.version) < 7;
	
	$.fn.fixpngs = function() {
		return this.each(function() {
			if (ie6) {
				var $el = $(this);
				var bg = $el.css('background-image');
				if (bg && bg != 'none') {
					bg = bg.replace(/url\(['"]?(.*?)['"]?\)/, '$1');
					this.style.backgroundImage = 'none';
					var repeat = $el.css('background-repeat') != 'no-repeat';
					var method = repeat ? 'scale' : 'image';
					this.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + bg + "', sizingMethod='" + method +"')";
				}
			}
		});
	}
	
})(jQuery);
