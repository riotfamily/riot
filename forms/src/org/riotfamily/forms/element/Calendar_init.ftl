(function() {
	var inputField = $('${element.eventTriggerId}');
	var button = new Element('span', {className: 'calendar-button'});
	inputField.insert({after: button});
	Calendar.setup({
		inputField: inputField,
		ifFormat: '${element.jsFormatPattern}',
		showsTime: ${element.showTime?string},
		button: button
	});
})();