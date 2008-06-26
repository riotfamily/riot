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
  - Returns the page with the given pageType. There must be only
  - one PageNode with that pageType, otherwise an exception is thrown.
  -->
<#function pageOfType pageType site=currentSite>
	<#return pagesMacroHelper.getPageOfType(pageType, site) />
</#function>

<#---
  - Returns all pages with the given pageType.
  -->
<#function pagesOfType pageType site=currentSite>
	<#return pagesMacroHelper.getPagesOfType(pageType, site) />
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
	<@inplace.use container=page.contentContainer form=form tag=tag attributes=attributes>
		<#nested />
	</@inplace.use>
</#macro>

<#---
  - Renders an HTML link to the given Page. The link text will be inplace 
  - editable if the page is the current page.
  -->
<#macro link page tag="a" editable=page==currentPage labelKey="title" form="" attributes...>
	<#local attributes = c.unwrapAttributes(attributes) />
	<#if editable>
		<@inplace.use container=page.contentContainer form=form>
			<@inplace.link key=labelKey href=c.url(page.url) tag=tag attributes=attributes>${page[labelKey]}</@inplace.link>
		</@inplace.use>
	<#else>
		<@c.link href=c.url(page.url) attributes=attributes>${page[labelKey]}</@c.link>
	</#if>
</#macro>

<#macro componentList key page=currentPage min=0 max=1000 initial=[] valid=[]>
	<@inplace.componentList page.contentContainer key min max initial valid />
</#macro>