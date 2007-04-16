<#function handlerUrl handlerName>
	<#return common.url(pageMacroHelper.getHandlerUrl(handlerName)) />
</#function>

<#function url page>
	<#return common.url(pageMacroHelper.getPageUrl(page)) />
</#function>

<#macro text page, key, tag="" attributes ...>
	<#local props = page.versionContainer.latestVersion.properties />
	<#local attrs = {"riot:containerId": page.versionContainer.id} + attributes />
	<#local attrs = attrs + {"class": ("riot-component " + attrs["class"]?if_exists)?trim} />
	<#if page.versionContainer.previewVersion?exists>
		<#local attrs = attrs + {"riot:dirty": "true"} />	
	</#if>
	<@component.editable key=key tag=tag scope=props editor="text" attributes=attrs><#nested /></@component.editable>
</#macro>