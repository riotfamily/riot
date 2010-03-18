<form onsubmit="list.filter(Form.serialize(this, true)); return false">
	<#list elements.elements as element>
		<div class="element<#if element.styleClass?exists> ${element.styleClass}-element</#if>">
			<#if element.label?exists>
				<label for="${element.id}">${element.label}</label>
			</#if>
			${element.render()}
		</div>
	</#list>
	<div class="buttons">
		<span><input type="submit" value="${messageResolver.getMessage('label.list.filter.apply', 'Apply')}" /></span>
		<span><input type="button" onclick="list.reset(); return false" value="${messageResolver.getMessage('label.list.filter.reset', 'Reset')}" /></span>
	</div>
</form>