<div id="${element.id}" class="mapEditor collectionEditor">
	<ul id="${items.id}" class="items">
		<#list items.elements as item>
			${item.render()}
		</#list>
	</ul>
	<#if addButton?exists>
		<div class="addKey">
			${keyEditor.render()}
			${addButton.render()}
			${errors.renderErrors(keyEditor)}
		</div>
	</#if>
</div>