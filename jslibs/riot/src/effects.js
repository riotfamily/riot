Effect.Remove = function(element, onRemove) {
	element = $(element);
	onRemove = onRemove || Element.remove;
	if (element.clientHeight > 100) {
		Element.makeClipping(element);
		return new Effect.Parallel([ 
			new Effect.Scale(element, 1, { 
				sync: true, 
		    	duration: 0.5, 
		    	delay: 0.5,
		        scaleX: false, 
		        scaleContent: false
	        }),
			new Effect.Opacity(element, { 
				sync: true, 
				to: 0.0, 
				from: 1.0,
				duration: 0.8
			})], 
	    	{
	    		duration: 1.0, 
				afterFinish: function(effect) {
	          		onRemove(effect.effects[0].element); 
	          	} 
			}
		);
	}
	else {
		return new Effect.Opacity(element, { 
			to: 0.0, 
			from: 1.0,
    		duration: 0.8, 
			afterFinish: function(effect) {
          		onRemove(effect.element); 
          	} 
		});
	}
}