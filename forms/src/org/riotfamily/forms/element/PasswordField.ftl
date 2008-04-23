<div id="${element.id}" class="password">
	<#if element.passwordSet>${toggleButton.render()}</#if>
	<#if element.showInput>
		${input.render()}
		<#if element.togglePlaintext>
			<div class="toggle-plaintext">
				<input id="${element.id}-toggle" type="checkbox"><label for="${element.id}-toggle">${messageResolver.getMessage('label.passwordField.togglePlaintext')}</label>
			</div>
		</#if>
		${errors.renderErrors(input)}
	</#if>
</div>