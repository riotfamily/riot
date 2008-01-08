$('${element.id}-view').onclick = function() {
	var el = $('${element.id}-overlay');
	this.expand = !this.expand;
	if (this.expand) {
		this.className = 'hide';
		new Effect.BlindDown(el, {duration: 0.3, afterFinish: function() {
			var swf = new SWFObject('${element.playerUrl}','${element.id}-swf','${element.previewWidth}','${element.previewHeight + 11}','9','#ffffff');
			swf.addVariable("file", "${element.downloadUrl}");
			swf.addVariable("width", "${element.previewWidth}");
			swf.addVariable("height", "${element.previewHeight}");
			swf.addVariable("controls", "on");
			swf.write(el);
		}});
	}
	else {
		el.hide().update();
		this.className = 'view';
	}
	return false;
};