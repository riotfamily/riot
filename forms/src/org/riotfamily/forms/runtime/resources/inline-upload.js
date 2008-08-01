function uploadInline(e, uploadUrl) {
	if (!window.submitEvent) {
		f.submit();
		return;
	}
	var f = e.form;
    var originalAction = f.action;
    f.action = uploadUrl;
    
	if (!window.frames[e.id]) {
		var divIframe = document.createElement('div');
		divIframe.style.width = '1px';
		divIframe.style.height = '1px';
		divIframe.style.visibility = 'hidden';
		divIframe.style.position = 'absolute';
		divIframe.innerHTML = '<iframe name="' + e.id + '" width="1" height="1"></iframe>';		
		document.body.appendChild(divIframe);				
	}
	
	var originalTarget = f.target;
	if (window.frames[e.id]) {		
		f.target = e.id;
		f.action += '&_exclusive=' + e.id;		
	}
	f.submit();
	
	f.target = originalTarget;
	f.action = originalAction;
	setTimeout(function() { submitEvent(new ChangeEvent(e)); }, 1000);
}
