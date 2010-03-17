if (!window.riot) var riot = {}; // riot namespace

riot.notification = (function() {

	// ------------------------------------------------------------------------
	// Private fields and functions
	// ------------------------------------------------------------------------

	var ie6 = Prototype.Browser.IE && typeof document.documentElement.style.maxHeight == 'undefined';
		
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
							<td#{icon}></td>\
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

	var visible = 0; 
	var container;
	
	function initContainer() {
		if (!container) {
			container = new Element('div');
			Element.insert(document.body, container.wrap(
					new Element('div', {id: 'riot-notifications'})));
		}
	}
	
	function fixPNGs(el) {
		if (ie6) {
			el.select('td').each(function(td) {
				if (td.style.filter) {
					td.style.backgroundImage = '';
				}
				var bg = td.getStyle('background-image');
				if (bg && bg != 'none') {
					bg = bg.replace(/url\(['"]?(.*?)['"]?\)/, '$1');
					td.style.backgroundImage = 'none';
					var repeat = td.getStyle('background-repeat') != 'no-repeat';
					var method = (repeat ? "scale" : "image")
					td.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + bg + "', sizingMethod='" + method +"')";
				}
			});
		}
	}
	
	var Notification = Class.create({
		initialize: function(o) {
			o = Object.extend({
				autoHide: true,
				duration: 4
			}, o);
			
			var data = {
				title: o.title,
				icon: o.icon ? ' class="icon" style="background-image:url('+o.icon+')"' : '',
				message: o.message
			};
			this.el = new Element('div').addClassName('notification').setStyle({visibility: 'hidden'})
				.insert(template.evaluate(data))
				.observe('mouseover', this.mouseover.bind(this))
				.observe('mouseout', this.mouseout.bind(this));
			
			this.el.down('.t .l').observe('click', this.close.bind(this));
		
			if (o.autoHide) {
				setTimeout(this.scheduledClose.bind(this), o.duration * 1000);
			}
			
			container.insert({top: this.el});
			if (container.getHeight() > document.viewport.getHeight()) {
				container.childElements().without(this.el).invoke('remove');
			}
			fixPNGs(this.el);
			this.el.hide().setStyle({visibility: 'visible'});
			if (Prototype.Browser.IE) {
				this.el.show();
			}
			else {
				Effect.Appear(this.el, {duration: 0.8});
			}
			visible++
		},
		
		close: function() {
			if (!this.closed) {
				this.el.setStyle({visibility: 'hidden'});
				this.closed = true;
				if (--visible == 0) {
					container.update();
				}
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
			fixPNGs(this.el);
		},
		
		mouseout: function() {
			this.hover = false;
			if (this.closeOnOut) {
				setTimeout(this.scheduledClose.bind(this), 1000);
			}
			else {
				this.el.down().removeClassName('hi');
				fixPNGs(this.el);
			}
		}
	});
	
	// ------------------------------------------------------------------------
	// Public API
	// ------------------------------------------------------------------------

	return {
		
		show: function(settings) {
			initContainer();
			new Notification(settings);
		}
	};

})();
