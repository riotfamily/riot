<#macro text page, key, tag="" attributes ...>
	<#local props = page.versionContainer.latestVersion.properties />
	<#local attrs = {"riot:containerId": page.versionContainer.id} + attributes />
	<#if page.versionContainer.previewVersion?exists>
		<#local attrs = attrs + {"riot:dirty": "true"} />	
	</#if>
	<@component.editable key=key scope=props editor="text" attributes=attrs><#nested /></@component.editable>
</#macro>