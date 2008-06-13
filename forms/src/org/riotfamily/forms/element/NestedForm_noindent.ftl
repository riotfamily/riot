<div class="nestedForm ${form.property?default('unbound')}">
<#list elements.elements as element>
	<div id="container-${element.id}" class="${(element.compositeElement?default(false))?string('composite','single')} ${element.styleClass!}"<#if !element.visible> style="display: none"</#if>>
		<#if element.label?exists>
			<#if element.compositeElement?default(false)>
				<div class="box-title">
					<label for="${element.id}">
						<#if element.label?has_content>
							${element.label}
						<#else>
							<span class="no-label"></span>
						</#if>
						<#if element.hint?exists>
							<span class="hint-trigger" onclick="toggleHint('${element.id}-hint')">&nbsp;</span>
						</#if>
					</label>
				</div>
			<#else>
				<label for="${element.id}">
					${element.label}<#if element.required>* </#if>
					<#if element.hint?exists>
						<span class="hint-trigger" onclick="toggleHint('${element.id}-hint')">&nbsp;</span>
					</#if>
				</label>
			</#if>
		</#if>
		<div class="element">
			<#if element.hint?exists>
				<div id="${element.id}-hint" class="hint">${element.hint}</div>
			</#if>
			${element.render()}
			${errors.renderErrors(element)}
		</div>
	</div>
</#list>			
</div>

