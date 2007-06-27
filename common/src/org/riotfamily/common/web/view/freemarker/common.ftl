<#assign locale = commonMacroHelper.getLocale() />

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

<#function resource uri>
	<#return commonMacroHelper.addTimestamp(uri) />
</#function>

<#function partition collection property>
	<#return commonMacroHelper.partition(collection, property) />
</#function>

<#function group collection size>
	<#return commonMacroHelper.group(collection, size) />
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

<#macro includeUriField>
	<input type="hidden" name="__includeUri" value="${request.requestUri}" />
</#macro>

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

