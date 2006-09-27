var WindowCallback = {

	callbacks: new Array(),
	
	register: function(win, handler) {
		this.callbacks.push({win: win, handler: handler});
	},
	
	invoke: function(win) {
		var callback = this.callbacks.detect(function(c) {return c.win == win});
		if (callback) {
			var handler = callback.handler;
			if (arguments.length > 1) {
				var args = Array.from(arguments).pop();
			    handler = function() {
			    	callback.handler(args);
			    }
			}
			this.callbacks = this.callbacks.without(callback);
			setTimeout(handler, 1);
		}
	}
}
