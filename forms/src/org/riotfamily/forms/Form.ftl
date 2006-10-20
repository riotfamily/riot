<div id="elements">
	<#if form.hint?exists>
		<div class="form-hint">${form.hint}</div>
	</#if>
	<#if form.hasErrors()>
		<div class="form-error">${form.errors.generalFormError}</div>
	</#if>
	<#list elements.elements as element>
		<#if element.compositeElement?if_exists>
			<#assign class = "composite" />
		<#else>
			<#assign class = "single" />
		</#if>
		<div class="form-element">
			<#if element.label?exists>
				<div class="title">
					<div class="icon"></div><span>${element.label}</span>
					<#if element.hint?exists>
						<div class="hint-trigger" onclick="toggleHint('${element.id}-hint')"></div>
					</#if>
				</div>
			</#if>
			<div class="${class}">
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