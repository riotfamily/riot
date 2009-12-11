<#---
  - Functions to look up pages.
  - @namespace pages
  -->
 
<#function resolve type args...>
	<#return statics["org.riotfamily.pages.mapping.PageResolver"].resolvePage(currentSite, type, args) />
</#function>

<#function get type site=currentSite>
	<#return ContentPage.loadByTypeAndSite(type, site) />
</#function>

<#macro renderComponents page key>
	<#local components = page.contentContainer.getContent(false)[key] />
	<#if components??>
		<#list components as component>
			${inplaceMacroHelper.renderComponent(component)!}
		</#list>
	</#if>
</#macro>

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
