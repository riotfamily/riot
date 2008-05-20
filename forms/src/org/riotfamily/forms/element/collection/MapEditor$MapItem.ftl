<li id="${item.id}" class="item ${(editor.compositeElement?default(false))?string('composite','single')}MapItem">
	<table class="item">
		<tbody>
			<tr>
				<td class="removeButton">
					<#if removeButton?exists>${removeButton.render()}</#if>
				</td>
				<td class="itemElement">
					<label for="${editor.id}">${item.renderLabel()}<#if editor.required && !editor.compositeElement?default(false)>* </#if></label>
					${editor.render()}
					${errors.renderErrors(editor)}
				</td>
			</tr>
		</tbody>
	</table>
</li>