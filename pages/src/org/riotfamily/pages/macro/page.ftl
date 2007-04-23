<#function handlerUrl handlerName locale=commonMacroHelper.locale>
	<#return common.url(pageMacroHelper.getHandlerUrl(handlerName, locale)) />
</#function>

<#function url page>
	<#return common.url(pageMacroHelper.getPageUrl(page)) />
</#function>

<#macro text page key tag="" attributes ...>
	<#if componentMacroHelper.isEditMode()>
		<#local props = page.versionContainer.latestVersion.properties />
		<#local attrs = {"riot:containerId": page.versionContainer.id} + attributes />
		<#local attrs = attrs + {"class": ("riot-component " + attrs["class"]?if_exists)?trim} />
		<#if page.versionContainer.previewVersion?exists>
			<#local attrs = attrs + {"riot:dirty": "true"} />	
		</#if>
	<#else>
		<#if page.versionContainer.liveVersion?exists>
			<#local props = page.versionContainer.liveVersion.properties />
		<#else>
			<#local props = {} />
		</#if>
		<#local attrs = attributes />
	</#if>
	<@component.editable key=key tag=tag scope=props editor="text" attributes=attrs><#nested /></@component.editable>
</#macro>