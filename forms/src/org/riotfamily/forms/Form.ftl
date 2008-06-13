<form id="${form.id}" action="${form.action}" method="post" enctype="multipart/form-data">
<div id="elements">
	<#if form.hint?exists>
		<div class="form-hint">${form.hint}</div>
	</#if>
	<#if form.hasErrors()>
		<div class="form-error">${form.errors.generalFormError}</div>
	</#if>
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