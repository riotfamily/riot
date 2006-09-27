<li id="${item.id}" class="listItem">
	<table class="listItem">
		<tbody>
			<tr<#if item.showDragHandle> class="draggable"</#if>>
				<#if item.showDragHandle>
					<td class="handle"><div></div></td>
				</#if>
				<td class="itemElement">
					${element.render()}
					${element.form.errors.renderErrors(element)}
				</td>
				<td class="removeButton">${removeButton.render()}</td>
			</tr>
		</tbody>
	</table>
</li>