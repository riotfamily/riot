<#global currentPageScope = pageMacroHelper.getCurrentPage()! />

<#--
  - Returns the current Page as resolved by the PageHandlerMapping.
  -->
<#function currentPage>
	<#return pageMacroHelper.getCurrentPage() />
</#function>

<#--
  - Returns the current Site.
  -->
<#function currentSite>
	<#return currentPage().site />
</#function>

<#--
  - Returns a collection containing the visible child-pages of the given (or current) page.
  -->
<#function childPages page=currentPageScope>
	<#return visiblePages(page.childPages) />
</#function>

<#--
  - Returns a collection containing the given page with its siblings in the correct order.
  -->
<#function pageAndSiblings page=currentPageScope>
	<#return visiblePages(page.node.parent.getChildPages(page.site)) />
</#function>

<#--
  - Returns the system-page with the given handlerName. There must be only
  - one PageNode with that id, otherwise an exception is thrown.
  -->
<#function pageForHandler handlerName site=currentSite()>
	<#return pageMacroHelper.getPageForHandler(handlerName, site) />
</#function>

<#--
  - Returns all pages with the given handlerName.
  -->
<#function pagesForHandler handlerName site=currentSite()>
	<#return pageMacroHelper.getPagesForHandler(handlerName, site) />
</#function>

<#--
  - Returns all top-level pages.
  -->
<#function topLevelPages site=currentSite()>
	<#return pageMacroHelper.getTopLevelPages(site) />
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
	<#return page.isVisible(inplace.editMode) />
</#function>

<#--
  - Returns the given collection of pages containing only visible pages.
  -->
<#function visiblePages pages>
	<#return pageMacroHelper.getVisiblePages(pages, inplace.editMode) />
</#function>

<#--
  - Returns the page property with the given key.
  -->
<#function property page key>
	<#if !page?is_hash>
		<#local page = pageForHandler(page) />
	</#if>
	<#return inplace.buildModel(page.versionContainer)[key] />
</#function>

<#--
  - Returns the page property with the given key, falling back to the
  - pathComponent (converted to title-case) if the property is not set.
  -->
<#function title page=currentPageScope key="title">
	<#local title = page.getProperty(key, inplace.editMode)?if_exists />
	<#if !title?has_content>
		<#local title = common.toTitleCase(page.pathComponent) />
	</#if>
	<#return title />
</#function>

<#macro use page=currentPage() form="" tag="" attributes...>
	<#local previousPageScope = currentPageScope />
	<#global currentPageScope = page />
	<#local container = page.versionContainer />
	<#local attributes = inplace.unwrap(attributes) />
	<#local previousComponentScope = currentComponentScope />
	<#global currentComponentScope = inplace.buildModel(container) />
	${inplaceMacroHelper.tag(container)}
	<#if inplace.editMode>
		<#if !tag?has_content>
			<#local tag = "div" />
		</#if>
		<#local attributes = inplace.addContainerAttributes(attributes, container, form) />
	</#if>
	<#if tag?has_content>
		<${tag}${inplace.join(attributes)}>
			<#nested currentComponentScope>
		</${tag}>
	<#else>
		<#nested currentComponentScope>
	</#if>
	<#global currentComponentScope = previousComponentScope />
	<#global currentPageScope = previousPageScope />
</#macro>


<#--
  - Renders an editable HTML link to the given Page.
  -->
<#macro link page=currentPageScope tag="a" form="" titleKey="title" href="" attributes ...>
	<#local attributes = inplace.addContainerAttributes(attributes, page.versionContainer, form) />
	<#local attributes = attributes + {"href" : href?has_content?string(href, url(page))} />
	<#local previousComponentScope = currentComponentScope />
	<#global currentComponentScope = inplace.buildModel(page.versionContainer) />
	<@inplace.editable editor="text" key=titleKey tag=tag attributes=attributes>${title(page, titleProperty)}</@inplace.editable>
	<#global currentComponentScope = previousComponentScope />
</#macro>
