<div id="${group.id}" class="indent group<#if !group.labelItems>-items-label-less</#if>">
	<#list group.elements as element>
		<div class="item<#if element.styleClass?exists> ${element.styleClass}-element</#if>">
			<#if group.labelItems>
				<div class="label">
					<label for="${element.id}" class="field <#if element.form.errors.hasErrors(element)> error</#if>">
						${element.label?if_exists}<#if element.required>* </#if>
						<#if element.hint?exists>
							<span class="hint-trigger" onclick="toggleHint('${element.id}-hint')"></span>
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
