<li id="${item.id}" class="item">
	<table class="item">
		<tbody>
			<tr<#if item.showDragHandle> class="draggable"</#if>>
				<td class="removeButton">${removeButton.render()}</td>
				<td class="itemElement">
					${element.render()}
					${element.form.errors.renderErrors(element)}
				</td>
				<#if item.showDragHandle>
					<td class="handle"><div></div></td>
				</#if>
			</tr>
		</tbody>
	</table>
</li>