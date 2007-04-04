<div id="${element.id}" class="dynamicList">
	<ul id="${items.id}" class="dynamicList">
		<#list items.elements as item>
			${item.render()}
		</#list>
	</ul>
</div>