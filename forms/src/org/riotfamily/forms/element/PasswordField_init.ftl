$('${element.buttonId}').onclick = function() {
	var s = $('${element.id}');
    var type = this.checked ? 'text' : 'password';
    s.replace(new Element('input', {name: s.name, id: s.id, value: s.value, className: s.className, type: type}));
}