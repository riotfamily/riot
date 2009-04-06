if (!window.riot) var riot = {}; // riot namespace

riot.notification = (function() {

	// ------------------------------------------------------------------------
	// Private fields
	// ------------------------------------------------------------------------

	var template = new Template('<table cellspacing="0" cellpadding="0">\
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
							<td class="icon"></td>\
							<td class="text">\
								<div class="title">#{title}</div>\
								<div class="message">#{message}</div>\
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
		</table>');

	// ------------------------------------------------------------------------
	// Public API
	// ------------------------------------------------------------------------

	return {
		show: function(o) {
			var data = {
				title: o.title, 
				message: o.message
			};
			var el = new Element('div', {className: 'notification'}).setStyle({display: 'none'})
				.insert(template.evaluate(data))
				.observe('mouseover', function() {
					this.down().addClassName('hi');
				})
				.observe('mouseout', function() {
					this.down().removeClassName('hi');
				})
				.observe('click', function() {
					this.remove();
				});

			Element.insert(document.body, el);
			Effect.Appear(el);
		}
	};

})();
