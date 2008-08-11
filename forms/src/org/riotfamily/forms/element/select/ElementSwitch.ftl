<div class="indent elements">
	<div id="container-${select.id}" class="item ${select.styleClass!}"<#if !select.visible> style="display: none"</#if>>
		<div class="label">
    		<label for="${select.id}" class="field<#if select.form.errors.hasErrors(select)> error</#if>">
				${select.label?if_exists}<#if select.required>* </#if>
				<#if select.hint?exists>
					<span class="hint-trigger" onclick="toggleHint('${select.id}-hint')">i</span>
				</#if>
			</label> 
		</div>
		<div class="element">
			<#if select.hint?exists>
				<div id="${select.id}-hint" class="hint">${select.hint}</div>
			</#if>
			${select.render()} ${errors.renderErrors(select)}
		</div>
	</div>
	<div id="container-${element.id}" class="item ${element.styleClass!}"<#if !element.visible> style="display: none"</#if>>
		<div class="label">
    		<label for="${element.id}" class="field<#if element.form.errors.hasErrors(element)> error</#if>">
				${element.label?if_exists}<#if element.required>* </#if>
				<#if element.hint?exists>
					<span class="hint-trigger" onclick="toggleHint('${element.id}-hint')">i</span>
				</#if>
			</label> 
		</div>
		<div class="element">
			<#if element.hint?exists>
				<div id="${element.id}-hint" class="hint">${element.hint}</div>
			</#if>
			${element.render()} ${errors.renderErrors(element)}
		</div>
	</div>
</div>