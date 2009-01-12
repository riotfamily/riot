<div class="password">
	<#if element.passwordSet>${toggleButton.render()}</#if>
	<#if element.showInput>
		<div class="indent">
			${input.render()}
			<#if element.strengthMeter>
				<div class="strength">
					<div id="${element.id}-strength"></div>
				</div>
			</#if>
			<#if element.togglePlaintext>
				<div class="toggle-plaintext">
					<input id="${element.id}-toggle" type="checkbox"><label for="${element.id}-toggle">${messageResolver.getMessage('label.passwordField.togglePlaintext')}</label>
				</div>
			</#if>
			${errors.renderErrors(input)}
		</div>
	</#if>
</div>