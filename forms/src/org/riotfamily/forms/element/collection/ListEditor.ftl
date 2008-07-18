<div class="listEditor collectionEditor">
	<ul id="${items.id}" class="items<#if element.sortable && element.dragAndDrop> drag-drop</#if>">
		<#list items.elements as item>
			${item.render()}
		</#list>
	</ul>
	${addButton.render()}
	<#if element.sortable>
		<input type="hidden" name="${element.paramName}" id="${element.id}-order" />
	</#if>
</div>