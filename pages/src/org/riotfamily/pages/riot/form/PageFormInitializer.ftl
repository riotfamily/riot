<div id="${form.id}" class="nestedForm ${form.property?default('unbound')}">
<#list elements.elements as element>
	<div class="composite<#if element.styleClass?exists> ${element.styleClass}-element</#if>">
		<div class="title">
			<label for="${element.id}">
				<#if element.label?has_content>
					${element.label}
				<#else>
					<span class="no-label"></span>
				</#if>
				${element.toggleButton.render()}
				<#if element.hint?exists>
					<span class="hint-trigger" onclick="toggleHint('${element.id}-hint')"></span>
				</#if>
			</label>
		</div>
		<div class="element">
			<#if element.hint?exists>
				<div id="${element.id}-hint" class="hint">${element.hint}</div>
			</#if>
			${element.render()}
			${element.form.errors.renderErrors(element)}
		</div>
	</div>
</#list>			
</div>

