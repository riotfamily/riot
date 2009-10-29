<#---
  - Functions to look up pages.
  - @namespace pages
  -->
 
<#function resolve type arg site=currentSite>
	<#return beans.pageResolver.getVirtualPage(site, type, arg) />
</#function>

<#function get type site=currentSite>
	<#return ContentPage.loadByTypeAndSite(type, site) />
</#function>

<#---
  - Renders an HTML link to the given Page. The link text will be inplace 
  - editable if the page is the current page.
  -->
<#macro link page tag="a" editable=page==currentPage labelKey="title" form="" attributes...>
	<#local attributes = c.unwrapAttributes(attributes) />
	<#if editable>
		<@inplace.link key=labelKey href=page.url tag=tag attributes=attributes>${page[labelKey]}</@inplace.link>
	<#else>
		<@c.link href=page.url attributes=attributes>${page[labelKey]}</@c.link>
	</#if>
</#macro>
