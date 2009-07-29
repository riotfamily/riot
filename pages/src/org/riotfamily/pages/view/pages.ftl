<#---
  - Functions to look up pages.
  - @namespace pages
  -->
  
<#---
  - @see <a href="inplace.html#use">inplace.use</a>
  -->
<#macro use page=currentPage form="" tag="" autoSizePopup=true attributes...>
	<#local attributes = c.unwrapAttributes(attributes) />
	<@inplace.use container=page.pageProperties form=form tag=tag autoSizePopup=autoSizePopup attributes=attributes>
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
		<@inplace.use container=page.pageProperties form=form>
			<@inplace.link key=labelKey href=c.url(page.url) tag=tag attributes=attributes>${page[labelKey]}</@inplace.link>
		</@inplace.use>
	<#else>
		<@c.link href=c.url(page.url) attributes=attributes>${page[labelKey]}</@c.link>
	</#if>
</#macro>
