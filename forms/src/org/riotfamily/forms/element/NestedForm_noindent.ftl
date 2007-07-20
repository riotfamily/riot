<div id="${form.id}" class="nestedForm ${form.property?default('unbound')}">
<#if form.required || form.present>
	
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
						${element.label}
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
				${form.errors.renderErrors(element)}
			</div>
		</div>
	</#list>			
	
	<#if !form.required && form.present>
  		${toggleButton.render()}
	</#if>
	
<#else>
	<div class="setButton">${toggleButton.render()}</div>
</#if>
</div>