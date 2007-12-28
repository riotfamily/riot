<div id="${element.id}" class="composite pageProperty pageProperty${element.overwrite?string('Overwrite','Inherit')}">
	<div class="title">
		${toggleButton.render()}
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
		<#if element.overwrite>
			${editor.render()}
			${element.form.errors.renderErrors(editor)}
		<#else>
			${display.render()}
		</#if>
	</div>
</div>

