<div id="${element.id}" class="${(editor.compositeElement?default(false))?string('composite','single')}	pageProperty pageProperty${element.overwrite?string('Overwrite','Inherit')}<#if editor.styleClass??> ${editor.styleClass}-pageProperty</#if>">
	<#if editor.label?exists>
		<#if editor.compositeElement?default(false)>
			<div class="box-title">
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
						<span class="hint-trigger" onclick="toggleHint('${editor.id}-hint')">&nbsp;</span>
					</#if>
				</label>
			</div>
		<#else>
			<#if display??>
				${toggleButton.render()}
			</#if>
			<label for="${editor.id}">
				${editor.label}<#if editor.required>* </#if>
				<#if editor.hint?exists>
					<span class="hint-trigger" onclick="toggleHint('${editor.id}-hint')">&nbsp;</span>
				</#if>
			</label>
		</#if>
	<#else>
		<#if display??>
			${toggleButton.render()}
		</#if>
	</#if>
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

