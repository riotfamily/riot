<div id="${element.id}" class="dynamicList">
	<ul id="${items.id}" class="dynamicList">
		<#list items.elements as item>
			${item.render()}
		</#list>
	</ul>
	${addButton.render()}
	<#if element.sortable>
		<input type="hidden" name="${element.paramName}" id="${element.id}-order" />
	</#if>
</div>