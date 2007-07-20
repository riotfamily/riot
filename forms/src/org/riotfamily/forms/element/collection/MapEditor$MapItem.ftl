<li id="${item.id}" class="item ${(element.compositeElement?default(false))?string('composite','single')}MapItem">
	<table class="item">
		<tbody>
			<tr>
				<td class="removeButton">
					<#if removeButton?exists>${removeButton.render()}</#if>
				</td>
				<td class="itemElement">
					<label for="${element.id}">${item.label}<#if element.required && !element.compositeElement?default(false)>* </#if></label>
					${element.render()}
					${element.form.errors.renderErrors(element)}
				</td>
			</tr>
		</tbody>
	</table>
</li>