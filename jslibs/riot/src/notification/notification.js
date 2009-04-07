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

	var Notification = Class.create({
		initialize: function(o) {
			o = Object.extend({
				autoHide: true,
				duration: 4
			}, o);
			
			var data = {
				title: o.title, 
				message: o.message
			};
			this.el = new Element('div', {className: 'notification'}).setStyle({display: 'none'})
				.insert(template.evaluate(data))
				.observe('mouseover', this.mouseover.bind(this))
				.observe('mouseout', this.mouseout.bind(this));
			
			this.el.down('.t .l').observe('click', this.close.bind(this));
		
			if (o.autoHide) {
				setTimeout(this.scheduledClose.bind(this), o.duration * 1000);
			}
			Element.insert(document.body, this.el);
			Effect.Appear(this.el, {duration: 0.8});
		},
		
		close: function() {
			if (!this.closed) {
				this.el.remove();
				this.closed = true;
			}
		},
		
		scheduledClose: function() {
			if (!this.hover) {
				this.close();
			}
			else {
				this.closeOnOut = true;
			}
		},
		
		mouseover: function() {
			this.hover = true;
			this.el.down().addClassName('hi');
		},
		
		mouseout: function() {
			this.hover = false;
			if (this.closeOnOut) {
				setTimeout(this.scheduledClose.bind(this), 1000);
			}
			else {
				this.el.down().removeClassName('hi');
			}
		}
	});
	
	// ------------------------------------------------------------------------
	// Public API
	// ------------------------------------------------------------------------

	return {
		
		show: function(settings) {
			new Notification(settings);
		}
	};

})();
