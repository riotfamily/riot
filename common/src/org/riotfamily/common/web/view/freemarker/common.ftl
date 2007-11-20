<#assign locale = commonMacroHelper.getLocale() />
<#assign documentUri = commonMacroHelper.getOriginatingRequestUri() />
<#assign includeUri = commonMacroHelper.getPathWithinApplication() />
<#assign topLevelHandlerName = commonMacroHelper.getTopLevelHandlerName()?if_exists />
<#assign templateName = .data_model['org.riotfamily.common.web.view.freemarker.RiotFreeMarkerView.templateName']! />

<#--
  - Includes the given URI using a RequestDispatcher. The argument may also be
  - a sequence of URIs, in which case multiple includes are performed.  
  -->
<#macro include uri="">
<#compress>
	<#if uri?is_sequence>
		<#list uri as item>
			${commonMacroHelper.include(item)}
		</#list>
	<#elseif uri?has_content>
		${commonMacroHelper.include(uri)}
	</#if>
</#compress>
</#macro>


<#function capture uri="">
	<#return commonMacroHelper.capture(uri) />
</#function>


<#macro setSharedProperty key value>
	<#local x = commonMacroHelper.setSharedProperty(key, value) />
</#macro>

<#function getSharedProperty key>
	<#return commonMacroHelper.getSharedProperty(key) />
</#function>

<#--
  - Adds the contextPath and sessionId to the given URI if necessary. 
  -->
<#function url uri>
	<#return commonMacroHelper.resolveAndEncodeUrl(uri?trim) />
</#function>

<#--
  - Adds the contextPath and a timestamp to the given URI.
  -->
<#function resource uri>
	<#return url(commonMacroHelper.addTimestamp(uri)) />
</#function>

<#--
  - Returns whether the given URL is external, i.e. has a schema part.
  -->
<#function isExternalUrl url>
	<#return commonMacroHelper.isExternalUrl(url?trim) />
</#function>

<#--
  - Converts the given path into an absolute URL by adding the protocol,
  - server-name, port and contextPath of the current request.
  -->
<#function absoluteUrl path>
	<#return commonMacroHelper.getAbsoluteUrl(path?trim) />
</#function>

<#--
  - Returns the URL for the given handlerName. 
  -->
<#function urlForHandler handlerName attributes={} prefix="">
	<#return url(commonMacroHelper.getUrlForHandler(handlerName, attributes, prefix)) />
</#function>

<#function isHandler(handlerName)>
	<#return handlerName == topLevelHandlerName />
</#function>

<#--
  - Returns a random item from the given collection.
  -->
<#function randomItem(collection)>
	<#local index = commonMacroHelper.random.nextInt(collection?size) />
	<#return collection[index] />
</#function>

<#--
  - Partitions a collection by inspecting the specified property
  - of the contained items. If the property value is different than the 
  - previous one, a new group is created and added to the returned sequence. 
  - Each group consists of a hash with a 'title' and an 'items' property.    
  -->
<#function partition collection property>
	<#return commonMacroHelper.partition(collection, property) />
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

<#function formatMillis millis>
	<#return commonMacroHelper.formatMillis(millis) />
</#function>

<#function toTitleCase s>
	<#return commonMacroHelper.toTitleCase(s) />
</#function>


<#function getMessage code args=[] default=code>
	<#return commonMacroHelper.getMessage(code, args, default) />
</#function>

<#macro message code args=[] default=code>${commonMacroHelper.getMessage(code, args, default)}</#macro>


<#function unwrapAttributes attributes>
	<#if attributes?is_sequence>
		<#return {} />
	<#elseif attributes.attributes?exists>
		<#return attributes.attributes />
	</#if>
	<#return attributes />
</#function>

<#function joinAttributes attributes>
	<#local attrs = "" />
	<#if attributes?is_hash>
		<#list attributes?keys as attributeName>
			<#if attributes[attributeName]?has_content>
				<#local attrs = attrs + " " + attributeName + "=\"" + attributes[attributeName]?html + "\"" />
			</#if>
		</#list>
	</#if>
	<#return attrs />
</#function>

<#macro if value="">
	<#if !(value?is_string && !value?has_content)><#nested value /></#if>
</#macro>

<#macro link href="" tag="a" externalClass="externalLink" externalTarget="_blank" attributes...>
	<#if href?has_content>
		<#local attributes = unwrapAttributes(attributes) + {"href": href} />
		<#if isExternalUrl(href)>
			<#local attributes = attributes + {
				"target": externalTarget,
				"class": ((attributes.class!) + " " + externalClass)?trim
			} />
		</#if>
		<${tag}${joinAttributes(attributes)}><#nested /></${tag}>
	<#else>
		<#nested />
	</#if>
</#macro>