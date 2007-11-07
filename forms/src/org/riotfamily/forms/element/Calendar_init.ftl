(function() {
	var inputField = document.getElementById('${element.id}');
	
	var button = document.createElement('button');
	button.className = 'calendar-button';
	inputField.parentNode.insertBefore(button, inputField.nextSibling);
	
	Calendar.setup({
		inputField: inputField,
		ifFormat: '${element.jsFormatPattern}',
		showsTime: ${element.showTime?string},
		button: button
	});
})();