var button = document.getElementById('${element.buttonId}')
button.onclick = function() {
	var chooser = window.open('${element.formContext.contextPath}${element.chooserUrl}', 'internalLink', 
			'width=400,height=600,dependent=yes,toolbar=no,location=no,menubar=no,status=no,scrollbars=yes');
			
	WindowCallback.register(chooser, function(path) {
		document.getElementById('${element.id}').value = path + '${element.linkSuffix?if_exists}';
	});
};