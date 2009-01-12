<div id="${element.id}" class="pageProperty pageProperty${element.overwrite?string('Overwrite','Inherit')}<#if editor.styleClass??> ${editor.styleClass}-pageProperty</#if>">
	<#assign composite = editor.compositeElement?default(false) && editor.label?? />
	<#if editor.label??>
		<div class="pageProperty-title title<#if composite> composite-title</#if>">
			<label for="${editor.id}">
				<#if editor.label?has_content>
					${editor.label}<#if editor.required>* </#if>
				<#else>
					<span class="no-label"></span>
				</#if>
				<#if editor.hint?exists>
					<span class="hint-trigger" onclick="toggleHint('${editor.id}-hint')">&nbsp;</span>
				</#if>
			</label>
			<#if display??>
				<b>${toggleButton.render()}</b>
			</#if>
		</div>
	<#else>
		<#if display??>
			<b>${toggleButton.render()}</b>
		</#if>
	</#if>
	<#if editor.hint?exists>
		<div id="${editor.id}-hint" class="hint">${editor.hint}</div>
	</#if>
	<div class="element<#if composite> composite-element</#if>">
		<#if display?? && !element.overwrite>
			${display.render()}
		<#else>
			${editor.render()}
			${element.form.errors.renderErrors(editor)}
		</#if>
	</div>
</div>

