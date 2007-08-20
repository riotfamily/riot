<#assign locale = commonMacroHelper.getLocale() />
<#assign documentUri = commonMacroHelper.getOriginatingRequestUri() />
<#assign includeUri = commonMacroHelper.getIncludeUri() />

<#--
  - Macro that includes the given URI using a RequestDispatcher.
  -->
<#macro include uri="">
	<#if uri?has_content>${commonMacroHelper.include(uri)}</#if>
</#macro>

<#macro setAttribute name value>
	${request.setAttribute(name, value)?if_exists}
</#macro>

<#function url uri>
	<#return commonMacroHelper.resolveAndEncodeUrl(uri) />
</#function>

<#function isExternalUrl url>
	<#return commonMacroHelper.isExternalUrl(url) />
</#function>

<#function absoluteUrl uri>
	<#return commonMacroHelper.getAbsoluteUrl(uri) />
</#function>

<#function urlForHandler handlerName attributes...>
	<#if attributes?size == 0>
		<#local uri = commonMacroHelper.getUrlForHandler(handlerName) />
	<#else>
		<#local attributes = attributes[0] />
		<#if attributes?is_hash>
			<#local uri = commonMacroHelper.getUrlForHandlerWithAttributes(handlerName, attributes) />
		<#else>
			<#local uri = commonMacroHelper.getUrlForHandlerWithAttribute(handlerName, attributes) />
		</#if>
	</#if>
	<#return url(uri) />
</#function>

<#function resource uri>
	<#return url(commonMacroHelper.addTimestamp(uri)) />
</#function>

<#function partition collection property>
	<#return commonMacroHelper.partition(collection, property) />
</#function>

<#function randomItem(collection)>
	<#local index = commonMacroHelper.random.nextInt(collection?size) />
	<#return collection[index] />
</#function>

<#function baseName url>
	<#return commonMacroHelper.baseName(url) />
</#function>

<#function fileExtension filename validExtension=[] defaultExtension="">
	<#return commonMacroHelper.getFileExtension(filename, validExtension, defaultExtension) />
</#function>

<#function formatByteSize bytes>
	<#return commonMacroHelper.formatByteSize(bytes) />
</#function>

<#function toTitleCase s>
	<#return commonMacroHelper.toTitleCase(s) />
</#function>

<#macro wrap value="" tag="div" attributes...>
	<#if value?has_content>
		<#local attrs = "" />
		<#if attributes?has_content>
			<#list attributes?keys as attributeName>
				<#if attributes[attributeName]?has_content>
					<#local attrs=attrs + " " + attributeName + "=\"" + attributes[attributeName] + "\"" />
				</#if>
			</#list>
		</#if>
		<${tag}${attrs?if_exists}>${value}</${tag}>
	</#if>
</#macro>

