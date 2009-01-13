<div class="elementGroup">
	<#if group.collapsible>
		${expandButton.render()}
	</#if>
	<#if !group.collapsible || group.expanded>
		<div id="${group.id}-elements" class="indent<#if !group.labelItems> unlabeled</#if><#if group.styleClass??> ${group.styleClass}</#if>">
			<#list group.elements as element>
				<#assign composite = element.compositeElement?default(false) && element.label?? />
				<div id="container-${element.id}" class="item<#if element.styleClass?exists> ${element.styleClass}-element</#if>"<#if !element.visible> style="display: none"</#if>>
					<#if group.labelItems>
						<div class="title<#if composite> composite-title</#if>">
							<label for="${element.eventTriggerId}" class="field <#if element.form.errors.hasErrors(element)> error</#if>">
								${element.label?if_exists}<#if element.required>* </#if>
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
						${element.render()} ${errors.renderErrors(element)}
					</div>
				</div>
			</#list>
		</div>
	</#if>
</div>
