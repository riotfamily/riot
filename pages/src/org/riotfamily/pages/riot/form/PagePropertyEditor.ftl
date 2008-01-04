<div id="${element.id}" class="composite pageProperty pageProperty${element.overwrite?string('Overwrite','Inherit')}">
	<div class="title">
		<#if display??>
			${toggleButton.render()}
		</#if>
		<label for="${editor.id}">
			<#if editor.label?has_content>
				${editor.label}
			<#else>
				<span class="no-label"></span>
			</#if>
			<#if editor.hint?exists>
				<span class="hint-trigger" onclick="toggleHint('${editor.id}-hint')"></span>
			</#if>
		</label>
	</div>
	<div class="element">
		<#if editor.hint?exists>
			<div id="${editor.id}-hint" class="hint">${editor.hint}</div>
		</#if>
		<#if display?? && !element.overwrite>
			${display.render()}
		<#else>
			${editor.render()}
			${element.form.errors.renderErrors(editor)}
		</#if>
	</div>
</div>

