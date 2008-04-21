<div id="${group.id}" class="group">
	<#if group.collapsible>
		${expandButton.render()}
	</#if>
	<#if !group.collapsible || group.expanded>
		<div id="${group.id}-elements" class="indent<#if !group.labelItems> unlabeled</#if><#if group.styleClass??> ${group.styleClass}</#if>">
			<#list group.elements as element>
				<div class="item<#if element.styleClass?exists> ${element.styleClass}-element</#if>">
					<#if group.labelItems>
						<div class="label">
							<label for="${element.id}" class="field <#if element.form.errors.hasErrors(element)> error</#if>">
								${element.label?if_exists}<#if element.required>* </#if>
								<#if element.hint?exists>
									<span class="hint-trigger" onclick="toggleHint('${element.id}-hint')">&nbsp;</span>
								</#if>
							</label>
						</div>
					</#if>
					<div class="element">
						<#if element.hint?exists>
							<div id="${element.id}-hint" class="hint">${element.hint}</div>
						</#if>
						${element.render()} ${element.form.errors.renderErrors(element)}
					</div>
				</div>
			</#list>
		</div>
	</#if>
</div>
