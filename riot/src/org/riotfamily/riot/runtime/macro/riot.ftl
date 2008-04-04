<#macro stylesheet href>
<link rel="stylesheet" type="text/css" href="${resource(href)}" />
</#macro>

<#macro script src>
<script language="JavaScript" type="text/javascript" src="${resource(src)}"></script>
</#macro>

<#macro controller url>
<@include riotMacroHelper.runtime.servletPrefix + url />
</#macro>

<#--
  - Includes the given path using a RequestDispatcher. The argument may also be
  - a sequence of paths, in which case multiple includes are performed.  
  -->
<#macro include path="">
<#compress>
	<#if path?is_sequence>
		<#list path as item>
			${riotMacroHelper.include(item)}
		</#list>
	<#elseif path?has_content>
		${riotMacroHelper.include(path)}
	</#if>
</#compress>
</#macro>

<#function resource src>
	<#return url(riotMacroHelper.runtime.resourcePath + src) />
</#function>


<#function href src>
	<#return url(riotMacroHelper.runtime.servletPrefix + src) />
</#function>

<#--
  - Adds the contextPath and sessionId to the given URI if necessary.
  - ${url('/foo.html')} => /context/foo.html;jsessionid=... 
  -->
<#function url href>
	<#return riotMacroHelper.resolveAndEncodeUrl(href?trim) />
</#function>
