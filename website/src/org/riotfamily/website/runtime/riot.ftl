<#--
  - Macro that includes the given URI using a RequestDispatcher.
  -->
<#macro include uri="">
	<#if uri?has_content>${riotInclude(uri)}</#if>
</#macro>

<#--
  - Macro that evaluates the nested content if the page is requested in
  - preview mode.
  -->
<#macro ifPreviewMode>
	<#if riotPrincipalBinder?exists && riotPrincipalBinder.getPrincipal(request)?exists>
		<#nested />
	</#if>
</#macro>

<#--
  - Macro that evaluates the nested content if the page is requested in
  - live mode.
  -->
<#macro ifLiveMode>
	<#if !riotPrincipalBinder?exists || !riotPrincipalBinder.getPrincipal(request)?exists>
		<#nested />
	</#if>
</#macro>

<#macro includeUriField>
	<input type="hidden" name="${includeUriParam}" value="${springMacroRequestContext.getUrlPathHelper().getRequestUri(request)}" />
</#macro>