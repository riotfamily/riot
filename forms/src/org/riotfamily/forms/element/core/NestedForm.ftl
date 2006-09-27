<div id="${form.id}" class="nestedForm ${form.property?default('unbound')}">
<#if form.required || form.present>
	
	<#if (elements.elements?size > 1)>
		<div class="elements<#if !form.required> optional</#if>">
			<#list elements.elements as element>
		    	<div class="item">
		    		<div class="label">
			    		<label for="${element.id}" class="field<#if element.form.errors.hasErrors(element)> error</#if>">
							${element.label?if_exists}<#if element.required>* </#if>
						</label> 
						<#if element.hint?exists>
							<div class="hint-trigger" onclick="toggleHint('${element.id}-hint')"></div>
						</#if>
					</div>
		    		<div class="element">
		    			<#if element.hint?exists>
							<div id="${element.id}-hint" class="hint">${element.hint}</div>
						</#if>
		    			${element.render()} ${element.form.errors.renderErrors(element)}
	    			</div>
		    	</div>
			</#list>			
		</div>
	<#else>
		<div class="element singleElement">
			<#list elements.elements as element>
				<#if element.hint?exists>
					<div id="${element.id}-hint" class="hint">${element.hint}</div>
				</#if>
				${element.render()} ${element.form.errors.renderErrors(element)}
			</#list>
		</div>
	</#if>
	
	<#if !form.required && form.present>
  		${toggleButton.render()}
	</#if>
	
<#else>
	<div class="setButton">${toggleButton.render()}</div>
</#if>
</div>