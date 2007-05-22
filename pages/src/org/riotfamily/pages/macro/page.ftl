<#function currentPage>
	<#return pageMacroHelper.currentPage />
</#function>

<#function pageForHandler handlerName locale=commonMacroHelper.locale>
	<#return pageMacroHelper.getPageForHandler(handlerName, locale) />
</#function>

<#function pagesForHandler handlerName locale=commonMacroHelper.locale>
	<#return pageMacroHelper.getPagesForHandler(handlerName, locale) />
</#function>

<#function topLevelPages locale=commonMacroHelper.locale>
	<#return pageMacroHelper.getTopLevelPages(locale) />
</#function>

<#function handlerUrl handlerName locale=commonMacroHelper.locale>
	<#return common.url(pageMacroHelper.getHandlerUrl(handlerName, locale)) />
</#function>

<#function wildcardHandlerUrl handlerName replacement locale=commonMacroHelper.locale>
	<#return common.url(pageMacroHelper.getWildcardHandlerUrl(handlerName, replacement, locale)) />
</#function>

<#function wildcardMatch>
	<#return pageMacroHelper.wildcardMatch />
</#function>

<#function url page>
	<#return common.url(pageMacroHelper.getUrl(page)) />
</#function>

<#function visible page>
	<#return !page.node.hidden && (page.enabled || componentMacroHelper.isEditMode()) />
</#function>

<#function property page, key>
	<#if !page?is_hash>
		<#local page = pageForHandler(page, commonMacroHelper.locale) />
	</#if>
	<#if componentMacroHelper.isEditMode()>
		<#return page.versionContainer.latestVersion.properties[key] />
	<#elseif page.versionContainer.liveVersion?exists>
		<#return page.versionContainer.liveVersion.properties[key] />
	</#if>
</#function>

<#macro text key page=pageMacroHelper.currentPage tag="" form="" attributes ...>
	<#if componentMacroHelper.isEditMode()>
		<#local attrs = {"riot:containerId": page.versionContainer.id} + attributes />
		<#local attrs = attrs + {"class": ("riot-component " + attrs["class"]?if_exists)?trim} />
		<#if form?has_content>
			<#local formUrl = pageMacroHelper.getFormUrl(form, page.versionContainer.id)?if_exists />
			<#if formUrl?has_content>
				<#local attrs = attrs + {"riot:form": formUrl} />
			</#if>
		</#if>
		<#if page.dirty>
			<#local attrs = attrs + {"riot:dirty": "true"} />
		</#if>
	<#else>
		<#local attrs = attributes />
	</#if>
	<#local props = page.getProperties(componentMacroHelper.isEditMode()) />
	<@component.editable key=key tag=tag scope=props editor="text" attributes=attrs><#nested props /></@component.editable>
</#macro>

<#macro properties form page=pageMacroHelper.currentPage tag="" attributes ...>
	<#if componentMacroHelper.isEditMode()>
		<#if !tag?has_content>
			<#local tag = "div" />
		</#if>
		<#local attributes = attributes + {
			"riot:containerId": page.versionContainer.id,
			"riot:form": pageMacroHelper.getFormUrl(form, page.versionContainer.id),
			"class": ("riot-component " + attributes["class"]?if_exists)?trim
		} />
	</#if>
	<#local attrs = "" />
	<#local keys = attributes?keys />
	<#list keys as attributeName>
		<#if attributes[attributeName]?has_content>
			<#local attrs = attrs + " " + attributeName + "=\"" + attributes[attributeName] + "\"" />
		</#if>
	</#list>
	<#if tag?has_content>
		<${tag}${attrs}>
			<#nested page.getProperties(componentMacroHelper.isEditMode())>
		</${tag}>
	<#else>
		<#nested page.getProperties(componentMacroHelper.isEditMode())>
	</#if>
</#macro>

<#function group pages size>
	<#return pageMacroHelper.group(pages, size) />
</#function>