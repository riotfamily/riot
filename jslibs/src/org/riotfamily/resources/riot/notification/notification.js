if (!window.riot) riot = {};

(function($) {
	
	var ie6 = $.browser.msie && parseInt($.browser.version) < 7;
		
	var template = '<table cellspacing="0" cellpadding="0">\
		  <tbody>\
			<tr class="t">\
			  <td class="l"></td>\
			  <td class="c"></td>\
			  <td class="r"></td>\
			</tr>\
			<tr class="m">\
			  <td class="l"></td>\
			  <td class="c">\
				<table>\
					<tbody>\
						<tr>\
							<td$icon></td>\
							<td class="text">\
								<div class="title">$title</div>\
								<div class="message">$message</div>\
							</td>\
						</tr>\
					</tbody>\
				</table>\
			  </td>\
			  <td class="r"></td>\
			</tr>\
			<tr class="b">\
			  <td class="l"></td>\
			  <td class="c"></td>\
			  <td class="r"></td>\
			</tr>\
		  </tbody>\
		</table>';

	var visible = 0; 
	var container;
	
	function initContainer() {
		if (!container) {
			container = $('<div id="riot-notifications"><div></div></div>').appendTo(document.body);
		}
	}
	
	function evalTemplate(data) {
		return template.replace(/\$(\w+)/g, function(str, p1) { return data[p1] || '' });
	}

	riot.showNotification = function(options) {
		var o = $.extend({
			autoHide: true,
			duration: 4
		}, options);
		
		var $el, hover, closeOnOut, closed;

		initContainer();
		
		function close() {
			if (!closed) {
				$el.css('visibility', 'hidden');
				closed = true;
				if (--visible == 0) {
					container.empty();
				}
			}
		}
		
		function scheduledClose() {
			if (!hover) {
				close();
			}
			else {
				closeOnOut = true;
			}
		}
		
		function mouseover() {
			hover = true;
			$el.find(':first-child').addClass('hi');
			$el.find('td').fixpngs();
		}
		
		function mouseout() {
			hover = false;
			if (closeOnOut) {
				setTimeout(scheduledClose, 1000);
			}
			else {
				$el.find(':first-child').removeClass('hi');
				$el.find('td').fixpngs();
			}
		}
		
		if (o.icon) {
			o.icon = ' class="icon" style="background-image:url('+o.icon+')"'; 
		}
		$el = $('<div>').addClass('notification').css('visibility', 'hidden')
			.html(evalTemplate(o))
			.mouseover(mouseover)
			.mouseout(mouseout)
			.prependTo(container);
		
		$el.find('.t .l').click(close);
		
		if (o.autoHide) {
			setTimeout(scheduledClose, o.duration * 1000);
		}
		
		$el.find('td').fixpngs();
		$el.hide().css('visibility', 'visible')[ie6 ? 'show' : 'fadeIn']();
		visible++;
	};
	
})(jQuery);
