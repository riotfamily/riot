<#---
  - Functions to look up pages.
  - @namespace pages
  -->
 
<#---
  - Renders an HTML link to the given Page. The link text will be inplace 
  - editable if the page is the current page.
  -->
<#macro link page tag="a" editable=page==currentPage labelKey="title" form="" attributes...>
	<#local attributes = c.unwrapAttributes(attributes) />
	<#if editable>
		<@inplace.link key=labelKey href=c.url(page.url) tag=tag attributes=attributes>${page[labelKey]}</@inplace.link>
	<#else>
		<@c.link href=c.url(page.url) attributes=attributes>${page[labelKey]}</@c.link>
	</#if>
</#macro>
