<#function currentPage>
	<#return pageMacroHelper.currentPage />
</#function>

<#function handlerUrl handlerName locale=commonMacroHelper.locale>
	<#return common.url(pageMacroHelper.getHandlerUrl(handlerName, locale)) />
</#function>

<#function wildcardHandlerUrl handlerName replacement locale=commonMacroHelper.locale>
	<#return common.url(pageMacroHelper.getWildcardHandlerUrl(handlerName, replacement, locale)) />
</#function>

<#function url page>
	<#return common.url(pageMacroHelper.getPageUrl(page)) />
</#function>

<#function property page, key>
	<#if componentMacroHelper.isEditMode()>
		<#return page.versionContainer.latestVersion.properties[key] />
	<#elseif page.versionContainer.liveVersion?exists>
		<#return page.versionContainer.liveVersion.properties[key] />
	</#if>
</#function>

<#macro text page key tag="" form="" attributes ...>
	<#if componentMacroHelper.isEditMode()>
		<#local props = page.versionContainer.latestVersion.properties />
		<#local attrs = {"riot:containerId": page.versionContainer.id} + attributes />
		<#local attrs = attrs + {"class": ("riot-component " + attrs["class"]?if_exists)?trim} />
		<#if form?has_content>
			<#local formUrl = pageMacroHelper.getFormUrl(form, page.versionContainer.id)?if_exists />
			<#if formUrl?has_content>
				<#local attrs = attrs + {"riot:form": formUrl} />
			</#if>
		</#if>
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