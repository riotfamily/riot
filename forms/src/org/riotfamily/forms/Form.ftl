<form id="${form.id}" action="${form.action}" method="post" enctype="multipart/form-data">
<script type="text/javascript" language="JavaScript">
	function toggleHint(id) {
		var el = document.getElementById(id);
		if (el.offsetWidth > 0) {
			el.style.display = 'none';
		}
		else {
			el.style.display = 'block';
		}
	}
</script>
<div id="elements">
	<#if form.hint?exists>
		<div class="form-hint">${form.hint}</div>
	</#if>
	<#if form.hasErrors()>
		<div class="form-error">${form.errors.generalFormError} ${form.errors.renderGlobalErrors()}</div>
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
					</label>
					<#if element.hint?exists>
						<span class="hint-trigger" onclick="toggleHint('${element.id}-hint')">&nbsp;</span>
					</#if>
				</div>
			</#if>
			<#if element.hint?exists>
				<div id="${element.id}-hint" class="hint">${element.hint}</div>
			</#if>
			<div class="element<#if composite> composite-element</#if>">
				<#-- Uncomment to debug dimensions: <div style="position:absolute;border:1px solid red;${element.dimension.css}"></div> -->
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