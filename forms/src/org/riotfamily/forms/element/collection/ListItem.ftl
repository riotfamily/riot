<li id="${item.id}" class="item">
	<table class="item">
		<tbody>
			<tr<#if item.showDragHandle> class="draggable"</#if>>
				<td class="removeButton">${removeButton.render()}</td>
				<td class="itemElement">
					${editor.render()}
					${item.form.errors.renderErrors(editor)}
				</td>
				<#if item.showDragHandle>
					<td class="handle"><div></div></td>
				</#if>
			</tr>
		</tbody>
	</table>
</li>