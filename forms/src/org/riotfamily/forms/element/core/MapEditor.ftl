<div id="${element.id}" class="mapEditor">
	<ul id="${items.id}" class="mapItems">
		<#list items.elements as item>
			${item.render()}
		</#list>
	</ul>
</div>