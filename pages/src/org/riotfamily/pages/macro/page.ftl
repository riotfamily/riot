<#--
	- Returns the current Page as resolved by the PageHandlerMapping.
	-->
<#function currentPage>
	<#return pageMacroHelper.currentPage />
</#function>

<#--
	- Returns a collection containing the given page with its siblings in the correct order.
	-->
<#function pageAndSiblings page=currentPage>
	<#return getVisiblePages(page.node.parent.getChildPages(page.locale)) />
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
<#function isVisible page>
	<#return pageMacroHelper.isVisible(page) />
</#function>

<#--
	- Returns the given collection of pages containing only visible pages.
	-->
<#function getVisiblePages pages>
	<#return pageMacroHelper.getVisiblePages(pages) />
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
	<#local attributes = attributes + {"href" : href?has_content?string(href, url(page))} />
	<@component.editable editor="text" container=page.versionContainer key=titleKey tag=tag form=form attributes=attributes>${title(page, titleProperty)}</@component.editable>
</#macro>

<#--
	- Splits up the given pages into groups of the specified size. Pages that
	- are not visible are skipped.
	-->
<#function group pages size>
	<#return pageMacroHelper.group(pages, size) />
</#function>