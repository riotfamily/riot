<li id="${item.id}" class="item<#if !item.list.dragAndDrop> nodrag</#if>">
	<table class="item">
		<tbody>
			<#if item.list.dragAndDrop>
				<tr class="draggable">
					<td class="removeButton">${removeButton.render()}</td>
					<td class="itemElement">
						${editor.render()}
						${item.form.errors.renderErrors(editor)}
					</td>
					<td class="handle"><div></div></td>
				</tr>
			<#else>
				<tr>
					<td class="button up disabled"></td>
					<td class="itemElement" rowspan="3">
						${editor.render()}
						${item.form.errors.renderErrors(editor)}
					</td>
				</tr>
				<tr>
					<td class="removeButton">${removeButton.render()}</td>
				</tr>
				<tr>
					<td class="button down disabled"></td>
				</tr>
			</#if>
		</tbody>
	</table>
</li>