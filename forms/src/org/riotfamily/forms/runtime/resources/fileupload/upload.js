Resources.loadScript('riot-js/browserinfo.js', 'browserInfo');

function uploadInline(e, uploadUrl) {
	if (!submitEvent) {
		f.submit();
		return;
	}
	var f = e.form;
	
	if (typeof XMLHttpRequest == 'undefined') {
		f.submit();
	}
	else {
	
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
}

function initPreview(id, url) {
	var frame = document.getElementById(id + '-preview');
	if (frame != null) {
		frame.style.overflow = 'hidden';
		var img = document.createElement('img');
		img.hint = document.getElementById(id + '-scaledImageHint');
		frame.appendChild(img);
		img.onload = scaleImage;
		//Note: IE does not fire onload events for cached images
		//so we need to add a timestamp to the URL.
		img.src = url + '&time=' + new Date().getTime();
	}	
}

/*
 * Onload handler that scales the preview image to fit into the dimensions
 * of the surrounding element. The image ratio is preserved.
 */
function scaleImage(e) {
	var maxWidth = this.parentNode.offsetWidth ? this.parentNode.offsetWidth : 100;
	var maxHeight = this.parentNode.offsetHeight ? this.parentNode.offsetHeight : 100;
	
	this.originalWidth = this.width;
	this.originalHeight = this.height;
	
	var width;
	var height;
	
    var imageRatio = this.originalWidth / this.originalHeight;
        
    if (maxHeight > 0 && this.originalHeight > maxHeight) {
        width = Math.floor(maxHeight * imageRatio); 
        height = maxHeight;
        this.scaled = true;
    }
    else {
        width = this.originalWidth;
        height = this.originalHeight;
    } 
    
    if (maxWidth > 0 && width > maxWidth) {
        width = maxWidth;
        height = Math.floor(width / imageRatio);
        this.scaled = true;
    }
	
	this.style.width = width + 'px';
	this.style.height = height + 'px';
	
	if (this.scaled) {
		this.onclick = onClickThumb;
		if (this.hint) {
			this.hint.style.display = 'block';
			this.hint.thumb = this;
			this.hint.onclick = function() {
				openBlowup(this.thumb);
			}
		}
	}
	
	this.style.marginTop = Math.round(maxHeight / 2 - height / 2) + 'px';

}

function onClickThumb(e) {
	openBlowup(this);
}

function openBlowup(thumb) {
	if (!thumb.blowup) {

		var width = thumb.originalWidth;
		var height = thumb.originalHeight;
		
		var img = document.createElement('img');
		img.src = thumb.src;
		
		var blowup = document.createElement('div');
		blowup.className = 'blowupImage';
		blowup.thumbnail = thumb;
		blowup.onclick = onClickBlowup;
		thumb.blowup = blowup;
		
		document.body.appendChild(blowup);
		
		blowup.style.position = 'absolute';
		blowup.style.left = getXPos(thumb.parentNode) + 'px';
		blowup.style.top = getYPos(thumb.parentNode) + 'px';
		
		if (browserInfo.ie) {
			var iframe = document.createElement('iframe');
			iframe.width = width;
			iframe.height = height;
			iframe.style.position = 'absolute';
			iframe.style.top = '0';
			iframe.style.left = '0';
			blowup.appendChild(iframe);
			
			img.style.position = 'absolute';
			img.style.top = '0';
			img.style.left = '0';
		}
		
		blowup.appendChild(img);
				
	}
}

function onClickBlowup(e) {
	closeBlowup(this);
}
	
function closeBlowup(blowup) {
	blowup.parentNode.removeChild(blowup);
	blowup.thumbnail.blowup = null;
}

function getXPos(obj) {
	var x = 0;
	if (obj.offsetParent) {
		while (obj.offsetParent) {
			x += obj.offsetLeft
			obj = obj.offsetParent;
		}
	}
	else if (obj.x)	x += obj.x;
	return x;
}

function getYPos(obj) {
	var y = 0;
	if (obj.offsetParent) {
		while (obj.offsetParent) {
			y += obj.offsetTop
			obj = obj.offsetParent;
		}
	}
	else if (obj.y) y += obj.y;
	return y;
}