<#--
  - Returns the current Page as resolved by the PageHandlerMapping.
  -->
<#function currentPage>
	<#return pageMacroHelper.getCurrentPage() />
</#function>

<#--
  - Returns a collection containing the given page with its siblings in the correct order.
  -->
<#function pageAndSiblings page=currentPage()>
	<#return visiblePages(page.node.parent.getChildPages(page.locale)) />
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
<#function visiblePages pages>
	<#return pageMacroHelper.getVisiblePages(pages) />
</#function>

<#--
  - Returns the page property with the given key.
  -->
<#function property page, key>
	<#if !page?is_hash>
		<#local page = pageForHandler(page, common.locale) />
	</#if>
	<#return component.buildModel(page.versionContainer)[key] />
</#function>

<#--
  - Returns the page property with the given key, falling back to the
  - pathComponent (converted to title-case) if the property is not set.
  -->
<#function title page=currentPage(), key="title">
	<#local title = page.getProperty(key, component.editMode)?if_exists />
	<#if !title?has_content>
		<#local title = common.toTitleCase(page.pathComponent) />
	</#if>
	<#return title />
</#function>

<#macro use page=currentPage() form="" tag="" attributes...>
	<#if attributes?is_sequence>
		<#local attributes = {} />
	</#if>
	<@component.use container=page.versionContainer form=form tag=tag attributes=attributes>
		<#nested />
	</@component.use>
</#macro>


<#--
  - Renders an editable HTML link to the given Page.
  -->
<#macro link page=currentPage() tag="a" form="" titleKey="title" href="" attributes ...>
	<#local attributes = component.addContainerAttributes(attributes, page.versionContainer, form) />
	<#local attributes = attributes + {"href" : href?has_content?string(href, url(page))} />
	<#local previousComponentScope = currentComponentScope />
	<#global currentComponentScope = component.buildModel(page.versionContainer) />
	<@component.editable editor="text" key=titleKey tag=tag attributes=attributes>${title(page, titleProperty)}</@component.editable>
	<#global currentComponentScope = previousComponentScope />
</#macro>
