<#--
  - Returns all top-level pages.
  -->
<#function topLevelPages site=currentSite>
	<#return pagesMacroHelper.getTopLevelPages(site) />
</#function>

<#--
  - Returns the system-page with the given handlerName. There must be only
  - one PageNode with that id, otherwise an exception is thrown.
  -->
<#function pageForHandler handlerName site=currentSite>
	<#return pagesMacroHelper.getPageForHandler(handlerName, site) />
</#function>

<#--
  - Returns all pages with the given handlerName.
  -->
<#function pagesForHandler handlerName site=currentSite>
	<#return pagesMacroHelper.getPagesForHandler(handlerName, site) />
</#function>

<#--
  - Returns the page for the given url. The site is being used in case the site
  - could not be determined from the url.
  -->
<#function pageForUrl url site=currentSite>
	<#return pagesMacroHelper.getPageForUrl(url, site) />
</#function>


<#macro use page=currentPage form="" tag="" attributes...>
	<#local attributes = common.unwrapAttributes(attributes) />
	<@inplace.use container=page.contentContainer model=page.properties form=form tag=tag attributes=attributes>
		<#nested />
	</@inplace.use>
</#macro>

<#--
  - Renders an editable HTML link to the given Page.
  -->
<#macro link page tag="a" labelKey="title" form="" attributes...>
	<#local attributes = common.unwrapAttributes(attributes) />
	<@inplace.use container=page.contentContainer model=page.properties form=form>
		<@inplace.link key=labelKey href=common.url(page.url) tag=tag attributes=attributes>${page.title}</@inplace.link>
	</@inplace.use>
</#macro>