<#--
  - Returns all top-level pages.
  -->
<#function topLevelPages site=currentPage.site>
	<#return pagesMacroHelper.getTopLevelPages(site) />
</#function>

<#--
  - Returns the system-page with the given handlerName. There must be only
  - one PageNode with that id, otherwise an exception is thrown.
  -->
<#function pageForHandler handlerName site=currentPage.site>
	<#return pagesMacroHelper.getPageForHandler(handlerName, site) />
</#function>

<#--
  - Returns all pages with the given handlerName.
  -->
<#function pagesForHandler handlerName site=currentPage.site>
	<#return pagesMacroHelper.getPagesForHandler(handlerName, site) />
</#function>

<#macro use page=currentPage form="" tag="" attributes...>
	<#local attributes = common.unwrapAttributes(attributes) />
	<@inplace.use container=page.versionContainer model=page.properties form=form tag=tag attributes=attributes>
		<#nested />
	</@inplace.use>
</#macro>

<#--
  - Renders an editable HTML link to the given Page.
  -->
<#macro link page tag="a" labelKey="title" form="" attributes...>
	<#compress>
		<#local attributes = common.unwrapAttributes(attributes) />
		<@inplace.use container=page.versionContainer model=page.properties form=form>
			<@inplace.link key=labelKey href=common.url(page.url) tag=tag attributes=attributes>${page.title}</@inplace.link>
		</@inplace.use>
	</#compress>
</#macro>