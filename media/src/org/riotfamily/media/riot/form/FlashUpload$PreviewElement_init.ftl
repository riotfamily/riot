$('${element.id}-view').onclick = function() {
	var el = $('${element.id}-overlay');
	this.expand = !this.expand;
	if (this.expand) {
		this.className = 'hide';
		new Effect.BlindDown(el, {duration: 0.3, afterFinish: function() {
			var swf = new SWFObject('${element.downloadUrl}','${element.id}-swf','100%','100%','${element.swf.version}','#ffffff');
			swf.write(el);
		}});
	}
	else {
		el.hide().update();
		this.className = 'view';
	}
	return false;
};