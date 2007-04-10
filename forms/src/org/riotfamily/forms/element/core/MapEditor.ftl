<div id="${element.id}" class="mapEditor">
	<div id="${items.id}" class="indent">
		<#list items.elements as item>
			${item.render()}
		</#list>
	</div>
</div>