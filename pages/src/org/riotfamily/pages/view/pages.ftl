<#--
  - Returns all top-level pages.
  -->
<#function topLevelPages site=currentPage.site>
	<#return pageDao.getRootNode().getChildPages(site) />
</#function>

<#--
  - Returns the system-page with the given handlerName. There must be only
  - one PageNode with that id, otherwise an exception is thrown.
  -->
<#function pageForHandler handlerName site=currentPage.site>
	<#return pageDao.findPageForHandler(handlerName, site) />
</#function>

<#--
  - Returns all pages with the given handlerName.
  -->
<#function pagesForHandler handlerName site=currentPage.site>
	<#return pageDao.findPagesForHandler(handlerName, site) />
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
<#macro link page tag="a" labelKey="title" attributes...>
	<#local attributes = common.unwrapAttributes(attributes) />
	<@inplace.link key=labelKey href=common.url(page.url) tag=tag attributes=attributes>${page.title}</@inplace.link>
</#macro>