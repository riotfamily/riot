<li id="${item.id}" class="item<#if !item.list.dragAndDrop> nodrag</#if>">
	<table class="item">
		<tbody>
			<#if item.list.sortable && !item.list.dragAndDrop>
				<tr>
					<td class="button up"><div class="disabled"></div></td>
					<td class="itemElement" rowspan="3">
						${editor.render()}
						${item.form.errors.renderErrors(editor)}
					</td>
					<td class="removeButton" rowspan="3">
						${removeButton.render()}
					</td>
				</tr>
				<tr>
					<td class="blind"></td>
				</tr>
				<tr>
					<td class="button down"><div class="disabled"></div></td>
				</tr>
			<#else>
				<tr<#if item.list.sortable> class="draggable"</#if>>
					<#if item.list.sortable><td class="handle"><div></div></td></#if>					
					<td class="itemElement">
						${editor.render()}
						${item.form.errors.renderErrors(editor)}
					</td>
					<td class="removeButton">${removeButton.render()}</td>
				</tr>
			</#if>
		</tbody>
	</table>
</li>