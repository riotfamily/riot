<#macro stylesheet href>
<link rel="stylesheet" type="text/css" href="${resource(href)}" />
</#macro>

<#macro script src>
<script langauge="JavaScript" type="text/javascript" src="${resource(src)}"></script>
</#macro>

<#macro controller url>
<@common.include riotMacroHelper.runtime.servletPrefix + url />
</#macro>

<#macro include url>
<@common.include url />
</#macro>

<#function resource url>
	<#return common.url(riotMacroHelper.runtime.resourcePath + url) />
</#function>


<#function href url>
	<#return common.url(riotMacroHelper.runtime.servletPrefix + url) />
</#function>
