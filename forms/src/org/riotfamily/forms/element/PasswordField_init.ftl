$('${element.id}-toggle').onclick = function() {
	var type = this.checked ? 'text' : 'password';
	$('${element.id}').select('input.password').each(function(i) {
		var strengthMeter = i.strengthMeter;
		var el = new Element('input', {name: i.name, id: i.id, value: i.value, className: i.className, type: type});
   		i.replace(el);
   		if (strengthMeter) {
   			strengthMeter.setInput(el);
   		}
	});
}
<#if element.strengthMeter>
	new PasswordStrengthMeter($('${element.id}').down('input.password'), '${element.id}-strength').labels = {
	 	weak: '${messageResolver.getMessage('label.passwordField.strength.weak')}',
        fair: '${messageResolver.getMessage('label.passwordField.strength.fair')}',
        strong: '${messageResolver.getMessage('label.passwordField.strength.strong')}'
	};
</#if> 