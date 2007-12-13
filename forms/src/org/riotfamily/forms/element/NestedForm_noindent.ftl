<div id="${form.id}" class="nestedForm ${form.property?default('unbound')}">
<#list elements.elements as element>
	<div class="${(element.compositeElement?default(false))?string('composite','single')}<#if element.styleClass?exists> ${element.styleClass}-element</#if>">
		<#if element.label?exists>
			<#if element.compositeElement?default(false)>
				<div class="title">
					<label for="${element.id}">
						<#if element.label?has_content>
							${element.label}
						<#else>
							<span class="no-label"></span>
						</#if>
						<#if element.hint?exists>
							<span class="hint-trigger" onclick="toggleHint('${element.id}-hint')"></span>
						</#if>
					</label>
				</div>
			<#else>
				<label for="${element.id}">
					${element.label}<#if element.required>* </#if>
					<#if element.hint?exists>
						<span class="hint-trigger" onclick="toggleHint('${element.id}-hint')"></span>
					</#if>
				</label>
			</#if>
		</#if>
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

