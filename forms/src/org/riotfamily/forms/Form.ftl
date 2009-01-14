<form id="${form.id}" action="${form.action}" method="post" enctype="multipart/form-data">
<div id="elements">
	<#if form.hint?exists>
		<div class="form-hint">${form.hint}</div>
	</#if>
	<#if form.hasErrors()>
		<div class="form-error">${form.errors.generalFormError}</div>
	</#if>
	<#list elements.elements as element>
		<#assign composite = element.compositeElement?default(false) && element.label?? />
		<div id="container-${element.id}" class="${element.styleClass!}"<#if !element.visible> style="display: none"</#if>>
			<#if element.label?exists>
				<div class="title<#if composite> composite-title</#if>">
					<label for="${element.eventTriggerId}">
						<#if element.label?has_content>
							${element.label}<#if element.required && !composite>* </#if>
						<#else>
							<span class="no-label"></span>
						</#if>
						<#if element.hint?exists>
							<span class="hint-trigger" onclick="toggleHint('${element.id}-hint')">&nbsp;</span>
						</#if>
					</label>
				</div>
			</#if>
			<#if element.hint?exists>
				<div id="${element.id}-hint" class="hint">${element.hint}</div>
			</#if>
			<div class="element<#if composite> composite-element</#if>">
				${element.render()}
				${form.errors.renderErrors(element)}
			</div>
		</div>
	</#list>
</div>

<#if buttons?exists>
	<div class="buttons">
		<#list buttons.elements as button>
			${button.render()}
		</#list>
	</div>
</#if>
</form>