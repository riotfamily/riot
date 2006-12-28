<div id="elements">
<#list elements.elements as element>
	<div class="element">
		<#if element.label?exists>
			<label for="${element.id}">${element.label}</label>
		</#if>
		${element.render()}
	</div>
</#list>
</div>
<div class="buttons">
	<input type="button" value="Apply Filter" onclick="list.filter(RForm.getValues(this.form)); return false" />
</div>
