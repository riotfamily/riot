<div class="nestedForm<#if form.styleClass??> ${form.styleClass}</#if> ${form.property?default('unbound')}">
	<#if !form.required>
		<div class="<#if form.present>present<#else>not-present</#if>">
	  		${toggleButton.render()}
		</div>	
	</#if>
	<#if form.required || form.present>
		<#list elements.elements as element>
			<#assign composite = element.compositeElement?default(false) && element.label?? />
			<div id="container-${element.id}" class="${element.styleClass!}"<#if !element.visible> style="display: none"</#if>>
				<#if element.label?? && (elements.elements?size > 0)>
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
					${element.render()}
					${errors.renderErrors(element)}
				</div>
			</div>
		</#list>
	</#if>
</div>

