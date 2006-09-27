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
	<input type="submit" name="submit" value="Apply Filter" />
</div>
