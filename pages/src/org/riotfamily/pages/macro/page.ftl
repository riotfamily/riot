<#assign scope = false />

<#function currentScope>
	<#if scope?is_boolean>
		<#return currentPage() />
	</#if>
	<#return scope />
</#function>

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
  - Returns all top-level pages.
  -->
<#function topLevelPages site=currentSite()>
	<#return pageMacroHelper.getTopLevelPages(site) />
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
  - Returns the URL of the given page.
  -->
<#function url page=currentScope()>
	<#return common.url(pageMacroHelper.getUrl(page)) />
</#function>

<#--
  - Returns whether the given Page is visible and should be displayed in menus.
  -->
<#function visible page=currentScope()>
	<#return page.isVisible(inplace.editMode) />
</#function>

<#--
  - Returns the given collection of pages containing only visible pages.
  -->
<#function visiblePages pages>
	<#return pageMacroHelper.getVisiblePages(pages, inplace.editMode) />
</#function>

<#--
  - Returns a collection containing the visible child-pages of the given (or current) page.
  -->
<#function childPages page=currentScope()>
	<#return visiblePages(page.childPages) />
</#function>

<#--
  - Returns a collection containing the given page with its siblings in the correct order.
  -->
<#function pageAndSiblings page=currentScope()>
	<#return visiblePages(page.node.parent.getChildPages(page.site)) />
</#function>

<#--
  - Returns a sequence containing the page itself (as last element) 
  - preceded by its ancestors.  
  -->
<#function ancestors page=currentScope()>
	<#return page.ancestors />
</#function>

<#--
  - Returns the page property 'title', falling back to the
  - pathComponent (converted to title-case) if the property is not set.
  -->
<#function title page=currentScope()>
	<#return page.getTitle(inplace.editMode)! />
</#function>


<#function properties>
	<#if !propertyScope??>
		<#assign propertyScope = inplace.buildModel(currentPage().versionContainer) />
	</#if>
	<#return propertyScope />
</#function>

<#macro use page=currentScope() form="" tag="" attributes...>
	<#if !tag?has_content && !scope?is_boolean && scope == page>
		<#nested />
	<#else>
		<#local attributes = common.unwrapAttributes(attributes) />
		<#local previousScope = scope />
		<#local previousPropertyScope = properties() />
		<#assign scope = page />
		<@inplace.use container=page.versionContainer form=form tag=tag attributes=attributes>
			<#assign propertyScope = inplace.scope />
			<#nested />
		</@inplace.use>
		<#assign propertyScope = previousPropertyScope />
		<#assign scope = previousScope />
	</#if>
</#macro>

<#--
  - Renders an editable HTML link to the given Page.
  -->
<#macro link page=currentScope() tag="a" labelKey="title" attributes...>
	<#local attributes = common.unwrapAttributes(attributes) />
	<@use page=page>
		<@inplace.link key=labelKey href=url() tag=tag attributes=attributes>${title()}</@inplace.link>
	</@use>
</#macro>