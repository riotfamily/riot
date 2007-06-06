<#--
  - Returns the current Page as resolved by the PageHandlerMapping.
  -->
<#function currentPage>
	<#return pageMacroHelper.currentPage />
</#function>

<#--
  - Returns the system-page with the given handlerName. There must be only
  - one PageNode with that id, otherwise an exception is thrown.
  -->
<#function pageForHandler handlerName locale=common.locale>
	<#return pageMacroHelper.getPageForHandler(handlerName, locale) />
</#function>

<#--
  - Returns all pages with the given handlerName.
  -->
<#function pagesForHandler handlerName locale=common.locale>
	<#return pageMacroHelper.getPagesForHandler(handlerName, locale) />
</#function>

<#--
  - Returns all top-level pages for the current site.
  -->
<#function topLevelPages locale=common.locale>
	<#return pageMacroHelper.getTopLevelPages(locale) />
</#function>

<#--
  - Returns the URL for the system-page with the given handlerName.
  -->
<#function handlerUrl handlerName locale=common.locale>
	<#return url(pageForHandler(handlerName, locale)) />
</#function>

<#--
  - Returns the URL for the wildcard-system-page with the given handlerName.
  -->
<#function wildcardHandlerUrl handlerName replacement locale=common.locale>
	<#return common.url(pageMacroHelper.getWildcardHandlerUrl(handlerName, replacement, locale)) />
</#function>

<#--
  - Returns the pathComponent that matched the wildcard, or null if the
  - current Page has no wildcard mapping.
  -->
<#function wildcardMatch>
	<#return pageMacroHelper.wildcardMatch />
</#function>

<#--
  - Returns the URL of the given page.
  -->
<#function url page>
	<#return common.url(pageMacroHelper.getUrl(page)) />
</#function>

<#--
  - Returns whether the given Page is visible and should be displayed in menus.
  -->
<#function visible page>
	<#return !page.node.hidden && (page.enabled || component.isEditMode()) />
</#function>

<#--
  - Returns the page property with the given key.
  -->
<#function property page, key>
	<#if !page?is_hash>
		<#local page = pageForHandler(page, common.locale) />
	</#if>
	<#return page.getProperty(key, component.isEditMode()) />
</#function>

<#--
  - Outputs a page property and makes it editable via the text-tool.
  -->
<#macro text key page=pageMacroHelper.currentPage tag="" form="" attributes ...>
	<#if attributes.attributes?exists>
		<#local attributes = attributes.attributes />
	</#if>
	<#if component.isEditMode()>
		<#local attributes = {"riot:containerId": page.versionContainer.id} + attributes />
		<#local attributes = attributes + {"class": ("riot-component " + attributes["class"]?if_exists)?trim} />
		<#if form?has_content>
			<#local formUrl = pageMacroHelper.getFormUrl(form, page.versionContainer.id)?if_exists />
			<#if formUrl?has_content>
				<#local attributes = attributes + {"riot:form": formUrl} />
			</#if>
		</#if>
		<#if page.dirty>
			<#local attributes = attributes + {"riot:dirty": "true"} />
		</#if>
	</#if>
	<#local props = page.getProperties(component.isEditMode()) />
	<@component.editable key=key tag=tag scope=props editor="text" attributes=attributes><#nested props /></@component.editable>
</#macro>

<#--
  - Makes the nested content editable via the properties-tool.
  -->
<#macro properties form page=pageMacroHelper.currentPage tag="" attributes ...>
	<#if component.isEditMode()>
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
			<#nested page.getProperties(component.isEditMode())>
		</${tag}>
	<#else>
		<#nested page.getProperties(component.isEditMode())>
	</#if>
</#macro>

<#--
  - Returns the page property with the given key, falling back to the
  - pathComponent (converted to title-case) if the property is not set.
  -->
<#function title page=pageMacroHelper.currentPage, key="title">
	<#local title = page.getProperty(key, component.isEditMode())?if_exists />
	<#if !title?has_content>
		<#local title = common.toTitleCase(page.pathComponent) />
	</#if>
	<#return title />
</#function>

<#--
  - Renders an editable HTML link to the given Page.
  -->
<#macro link page=pageMacroHelper.currentPage tag="a" titleKey="title" href="" form="" attributes ...>
	<#local attributes = attributes + {"href": href} />
	<@text page=page key=titleKey tag=tag form=form href=href?has_content?string(href, url(page)) attributes=attributes>${title(page, titleProperty)}</@text>
</#macro>

<#--
  - Splits up the given pages into groups of the specified size. Pages that
  - are not visible are skipped.
  -->
<#function group pages size>
	<#return pageMacroHelper.group(pages, size) />
</#function>