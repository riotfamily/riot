<#---
  - Functions to look up pages.
  - @namespace pages
  -->
  
<#---
  - Returns all top-level pages for a site.
  -->
<#function topLevelPages site=currentSite>
	<#return pagesMacroHelper.getTopLevelPages(site) />
</#function>

<#---
  - Returns the page with the given handlerName. There must be only
  - one PageNode with that handlerName, otherwise an exception is thrown.
  -->
<#function pageForHandler handlerName site=currentSite>
	<#return pagesMacroHelper.getPageForHandler(handlerName, site) />
</#function>

<#---
  - Returns all pages with the given handlerName.
  -->
<#function pagesForHandler handlerName site=currentSite>
	<#return pagesMacroHelper.getPagesForHandler(handlerName, site) />
</#function>

<#---
  - Returns the page for the given url.
  - @param site The site to use in case the site can't be determined from the url
  -->
<#function pageForUrl url site=currentSite>
	<#return pagesMacroHelper.getPageForUrl(url, site) />
</#function>

<#---
  - @see <a href="inplace.html#use">inplace.use</a>
  -->
<#macro use page=currentPage form="" tag="" attributes...>
	<#local attributes = c.unwrapAttributes(attributes) />
	<@inplace.use container=page.contentContainer model=page.properties form=form tag=tag attributes=attributes>
		<#nested />
	</@inplace.use>
</#macro>

<#---
  - Renders an editable HTML link to the given Page.
  -->
<#macro link page tag="a" labelKey="title" form="" attributes...>
	<#local attributes = c.unwrapAttributes(attributes) />
	<@inplace.use container=page.contentContainer model=page.properties form=form>
		<@inplace.link key=labelKey href=c.url(page.url) tag=tag attributes=attributes>${page.title}</@inplace.link>
	</@inplace.use>
</#macro>