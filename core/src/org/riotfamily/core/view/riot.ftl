<#---
  - Adds the Riot resource path to the given URL 
  - unless it starts with a slash, in which case 
  - only the servletPrefix is added.
  - 
  - <h4>Example:</h4>
  - <pre>${resource('style/main.css')}
  - ==> /riot/resources/1234567890/style/main.css
  -
  - ${resource('/style/main.css')}
  - ==> /riot/style/main.css
  - </pre>
  -->
<#function resource href>
	<#if href?starts_with("/")>
		<#return runtime.servletPrefix + href />
	</#if>
	<#return runtime.resourcePath + href />
</#function>

<#function resources paths>
	<#local res = [] />
	<#list paths as path>
		<#local res = res + [resource(path)] />
	</#list>
	<#return res />
</#function>

<#macro stylesheet href rel="stylesheet" type="text/css" attributes...>
	<link rel="${rel}" type="${type}" href="${c.resolve(resource(href))?xml}"${c.joinAttributes(attributes)} />
</#macro>

<#macro script src type="text/javascript" attributes...>
	<script src="${c.resolve(resource(src))?xml}" type="${type}"${c.joinAttributes(attributes)}></script>
</#macro>

<#macro stylesheets hrefs compress=commonMacroHelper.compressResources rel="stylesheet" type="text/css" attributes...>
	<@c.stylesheets resources(hrefs) compress rel type c.unwrapAttributes(attributes) />
</#macro>

<#macro scripts srcs compress=commonMacroHelper.compressResources type="text/javascript" attributes...>
	<@c.scripts resources(srcs) compress type c.unwrapAttributes(attributes) /> 
</#macro>

<#function iconStyle icon="">
	<#if icon?has_content>
		<#local url = c.resolve(resource("style/images/icons/" + icon + ".png")) />
		<#return "background-image:url(" + url + ");_background-image:none;_filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + url + "', sizingMethod='crop');" />
	<#else>
		<#return "" />
	</#if>
</#function>