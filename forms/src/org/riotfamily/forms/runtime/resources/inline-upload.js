function uploadInline(e, uploadUrl) {
	if (!window.submitEvent) {
		f.submit();
		return;
	}
	var f = e.form;
    var originalAction = f.action;
    f.action = uploadUrl;
    var id = 'f' + e.id;
	if (!window.frames[id]) {
		var div = document.createElement('div');
		div.style.width = '1px';
		div.style.height = '1px';
		div.style.visibility = 'hidden';
		div.style.position = 'absolute';
		div.innerHTML = '<iframe id="' + id + '" name="' + id + '" width="1" height="1"></iframe>';		
		document.body.appendChild(div);				
	}
	
	var originalTarget = f.target;
	if (window.frames[id]) {		
		f.target = id;
		f.action += '&_exclusive=' + e.id;
		f.submit();
		f.target = originalTarget;
		f.action = originalAction;	
		setTimeout(function() { submitEvent(new ChangeEvent(e)); }, 1000);
	}
}
