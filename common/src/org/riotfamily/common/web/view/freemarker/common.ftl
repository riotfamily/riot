<#assign locale = commonMacroHelper.getLocale() />
<#assign documentUri = commonMacroHelper.getOriginatingRequestUri() />
<#assign includeUri = commonMacroHelper.getPathWithinApplication() />
<#assign topLevelHandlerName = commonMacroHelper.getTopLevelHandlerName()?if_exists />
<#assign templateName = .data_model['org.riotfamily.common.web.view.freemarker.RiotFreeMarkerView.templateName']! />

<#--
  - Includes the given path using a RequestDispatcher. The argument may also be
  - a sequence of paths, in which case multiple includes are performed.  
  -->
<#macro include path="">
<#compress>
	<#if path?is_sequence>
		<#list path as item>
			${commonMacroHelper.include(item)}
		</#list>
	<#elseif path?has_content>
		${commonMacroHelper.include(path)}
	</#if>
</#compress>
</#macro>

<#--
  - Performs a request using a RequestDispatcher and returns the captured output.
  -->
<#function capture path="">
	<#return commonMacroHelper.capture(path) />
</#function>

<#--
  - Sets a shared property. The concept and purpose of shared properties is 
  - described at http://www.riotfamily.org/docs/push-ups.html#collaboration
  -->
<#macro setSharedProperty key value>
	<#local x = commonMacroHelper.setSharedProperty(key, value) />
</#macro>

<#--
  - Gets a shared property. The concept and purpose of shared properties is 
  - described at http://www.riotfamily.org/docs/push-ups.html#collaboration
  -->

<#function getSharedProperty key>
	<#return commonMacroHelper.getSharedProperty(key) />
</#function>

<#--
  - Adds the contextPath and sessionId to the given URI if necessary.
  - ${url('/foo.html')} => /context/foo.html;jsessionid=... 
  -->
<#function url href>
	<#return commonMacroHelper.resolveAndEncodeUrl(href?trim) />
</#function>

<#--
  - Adds the contextPath to the given path.
  - ${url('/foo.html')} => /context/foo.html 
  -->
<#function resolve href>
	<#return commonMacroHelper.resolveUrl(href?trim) />
</#function>

<#--
  - Adds the contextPath and sessionId to all links found in the given HTML if necessary.
  - ${encodeLinks('Hello <a href="/world.html">World</a>')} => Hello <a href="/context/world.html;jsessionid=...">World</a> 
  -->
<#function encodeLinks html>
	<#return commonMacroHelper.resolveAndEncodeLinks(html) />
</#function>

<#--
  - Adds the contextPath and a timestamp to the given URI.
  - ${resource('/style/main.css')} => /context/style/main.css?121345
  -->
<#function resource href>
	<#return url(commonMacroHelper.addTimestamp(href)) />
</#function>

<#--
  - Returns whether the given URL is external, i.e. has a schema part.
  -->
<#function isExternalUrl href>
	<#return commonMacroHelper.isExternalUrl(href?trim) />
</#function>

<#--
  - Converts the given path into an absolute URL by adding the protocol,
  - server-name, port and contextPath of the current request.
  -->
<#function absoluteUrl href>
	<#return commonMacroHelper.getAbsoluteUrl(href?trim) />
</#function>

<#--
  - Returns the URL for the given handlerName. 
  -->
<#function urlForHandler handlerName attributes={} prefix="">
	<#return url(commonMacroHelper.getUrlForHandler(handlerName, attributes, prefix)) />
</#function>

<#function isHandler handlerName>
	<#return handlerName == topLevelHandlerName />
</#function>

<#--
  - Tries to replace the given parameter's value in the given URL's query string
  - with the given new value or adds the parameter if it is not yet contained. 
  -->
<#function setParameter url name value>
	<#return commonMacroHelper.setParameter(url, name, value) />
</#function>

<#--
  - Adds the given parameter to the given URL's query string.
  -->
<#function addParameter url name value>
	<#return commonMacroHelper.addParameter(url, name, value) />
</#function>

<#--
  - Adds all request parameters to the given URLs query string.
  -->
<#function addRequestParameters url>
	<#return commonMacroHelper.addRequestParameters(url) />
</#function>

<#--
  - Returns a random item from the given collection.
  -->
<#function randomItem collection>
	<#local index = commonMacroHelper.random.nextInt(collection?size) />
	<#return collection[index] />
</#function>

<#--
  - Returns the current time as datetime value. The value does not change 
  - during template processing. 
  -->
<#function currentTime>
	<#return commonMacroHelper.getCurrentTime()?datetime />
</#function>

<#-- 
  - Returns whether the given date is in the future. 
  -->
<#function isInFuture date="">
	<#return (date?is_date && date?datetime > currentTime()) />
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

<#--
  - Strips directory names and the query-string from a path.
  - ${baseName('/hello/world.html?foo=bar')} => world.html  
  -->
<#function baseName path>
	<#return commonMacroHelper.baseName(path) />
</#function>

<#--
  - Returns the extension of the given file-name. If the validExtensions 
  - parameter is specified, the defaultExtension will be returned if the actual
  - extension is invalid.
  - ${fileExtension('foo.html')} => html
   -${fileExtension('foo.bar', ['gif', 'jpg'], 'unknown')} => unknown
  -->
<#function fileExtension filename validExtension=[] defaultExtension="">
	<#return commonMacroHelper.getFileExtension(filename, validExtension, defaultExtension) />
</#function>

<#--
  - Returns a formatted string using an appropriate unit (Bytes, KB or MB).
  -->
<#function formatByteSize bytes>
	<#return commonMacroHelper.formatByteSize(bytes) />
</#function>

<#--
  - Returns a formatted string using the pattern hh:mm:ss. The hours are
  - omitted if they are zero, the minutes are padded with a '0' character
  - if they are less than 10.
  -->
<#function formatMillis millis>
	<#return commonMacroHelper.formatMillis(millis) />
</#function>

<#--
  - Formats the given number using a custom pattern and locale.
  -->
<#function formatNumber number pattern="#0.#" locale="en_US">
	<#return commonMacroHelper.formatNumber(number, pattern, locale) />
</#function>

<#--
  - Rounds the number to a decimal number
  -->
<#function round number>
	<#return commonMacroHelper.round(number) />
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
				<#local attrs = attrs + " " + attributeName + "=\"" + attributes[attributeName]?trim?html + "\"" />
			</#if>
		</#list>
	</#if>
	<#return attrs />
</#function>

<#macro attribute name value=""><#if value!?trim?has_content> ${name}="${value?trim}"</#if></#macro>

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