$('${element.id}-toggle').onclick = function() {
	var type = this.checked ? 'text' : 'password';
	$('${input.id}').select('input').each(function(i) {
   		i.replace(new Element('input', {name: i.name, id: i.id, value: i.value, className: i.className, type: type}));
	});
}