var NotificationList = Class.create();
NotificationList.prototype = {

	notifications: [],
	
	initialize: function(el, url) {
		this.element = $(el);
		this.url = url;
		this.emptyMessage = this.element.innerHTML;
		this.update();
		new PeriodicalExecuter(this.update.bind(this), 10);
	},
	
	update: function() {
		new Ajax.Request(this.url, {
			method: 'get', 
			onComplete: this.processResponse.bind(this)
		});
	},
	
	remove: function(n) {
		new Ajax.Request(this.url, {
			method: 'get', 
			parameters: 'id=' + n.id,
			onComplete: Prototype.emptyFunction
		});
		this.notifications = this.notifications.without(n);
		if (this.notifications.length == 0) {
			this.element.innerHTML = this.emptyMessage;
		}
	},
	
	processResponse: function(res, model) {
		var _this = this;
		var knownIds = this.notifications.pluck('id');
		model.notifications.each(function(data) {
			if (!knownIds.include(data.id)) {
				if (_this.notifications.length == 0) {
					_this.element.innerHTML = '';
				}
				var n = new Notification(_this, data);
				_this.notifications.push(n);
				Element.prependChild(_this.element, n.element);
			}
		});
	}
	
}

var Notification = Class.create();
Notification.prototype = {
	initialize: function(list, data) {
		this.list = list;
		this.data = data;
		this.id = data.id;
		this.element = this.createElement();
		this.collapsed = true;
	},
	
	createElement: function (n) {	
		var removeHandler = this.remove.bind(this);
		return Element.create('div', {className: 'notification'},
			Element.create('div', {className: 'message', title: this.data.issueDate, innerHTML = this.data.message}),
			Element.create('div', {className: 'delete', onclick: removeHandler})
		);
	},
		
	remove: function() {
		this.list.remove(this);
		new Effect.Fade(this.element);
	}

}
