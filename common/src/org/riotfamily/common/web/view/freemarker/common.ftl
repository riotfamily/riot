<#--
  - Macro that includes the given URI using a RequestDispatcher.
  -->
<#macro include uri="">
	<#if uri?has_content>${commonMacroHelper.include(uri)}</#if>
</#macro>

<#function url uri>
	<#return commonMacroHelper.resolveAndEncodeUrl(uri) />
</#function>

<#macro includeUriField>
	<input type="hidden" name="__includeUri" value="${commonMacroHelper.getOriginatingRequestUri()}" />
</#macro>