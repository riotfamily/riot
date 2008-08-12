<#---
  - Provides common utility macros and functions.
  - @namespace c
  -->

<#--- The locale resolved by Spring's LocaleResolver. -->
<#assign locale = commonMacroHelper.getLocale() />

<#--- The originating request URI. -->
<#assign documentUri = commonMacroHelper.getOriginatingRequestUri() />

<#--- 
  - The path within the application.
  - If the request is included via a RequestDispatcher, the variable contains
  - the URI of the include, otherwise it's the same as documentUri.
  - @see <a href="http://www.riotfamily.org/api/latest/org/riotfamily/website/view/CommonMacroHelper.html#getPathWithinApplication()">
  -      commonMacroHelper.getPathWithinApplication()</a>
 -->
<#assign includeUri = commonMacroHelper.getPathWithinApplication() />

<#--- 
  - The name of the current FreeMarker template (may be useful for debugging).
  - @see <a href="http://www.riotfamily.org/api/latest/org/riotfamily/common/web/view/freemarker/RiotFreeMarkerView.html">RiotFreeMarkerView</a>
 -->
<#assign templateName = .data_model['org.riotfamily.common.web.view.freemarker.RiotFreeMarkerView.templateName']! />

<#---
  - Includes the given path using a RequestDispatcher. 
  - @param path The path to include
  -->
<#macro include path>
	${commonMacroHelper.include(path)}
</#macro>

<#---
  - Performs a request using a RequestDispatcher and returns the captured output.
  -->
<#function capture path="">
	<#return commonMacroHelper.capture(path) />
</#function>

<#---
  - Sets a shared property. The concept and purpose of shared properties is 
  - described <a href="http://www.riotfamily.org/docs/push-ups.html#collaboration">here</a>.
  -->
<#macro setSharedProperty key value>
	<#local x = commonMacroHelper.setSharedProperty(key, value) />
</#macro>

<#---
  - Gets a shared property. The concept and purpose of shared properties is 
  - described <a href="http://www.riotfamily.org/docs/push-ups.html#collaboration">here</a>.
  -->

<#function getSharedProperty key>
	<#return commonMacroHelper.getSharedProperty(key) />
</#function>

<#---
  - Adds the contextPath and sessionId to the given URI if necessary.
  - <h4>Example:</h4>
  - <pre>${url('/foo.html')}
  - ==> /context/foo.html;jsessionid=...
  - </pre> 
  -->
<#function url href>
	<#return commonMacroHelper.resolveAndEncodeUrl(href?trim) />
</#function>

<#---
  - Adds the contextPath to the given path.
  - <h4>Example:</h4>
  - <pre>${url('/foo.html')}
  - ==> /context/foo.html
  - </pre> 
  -->
<#function resolve href>
	<#return commonMacroHelper.resolveUrl(href?trim) />
</#function>

<#---
  - Adds the contextPath and sessionId to all links found in the given HTML if necessary.
  - <h4>Example:</h4>
  - <pre>${encodeLinks('Hello &lt;a href="/world.html"&gt;World&lt;/a&gt;')} 
  - ==> Hello &lt;a href="/context/world.html;jsessionid=..."&gt;World&lt;/a&gt;
  - </pre>
  -->
<#function encodeLinks html>
	<#return commonMacroHelper.resolveAndEncodeLinks(html) />
</#function>

<#---
  - Adds the contextPath and a timestamp to the given URI.
  - <h4>Example:</h4>
  - <pre>${resource('/style/main.css')}
  - ==> /context/style/main.css?121345
  - </pre>
  -->
<#function resource href>
	<#return resolve(stamp(href)) />
</#function>

<#---
  - Adds the server startup time to the given URI.
  - <h4>Example:</h4>
  - <pre>${stamp('main.css')}
  - ==> main.css?121345
  - </pre>
  -->
<#function stamp href>
	<#return commonMacroHelper.addTimestamp(href) />
</#function>

<#---
  - Adds the current timestamp to the given URI.
  - <h4>Example:</h4>
  - <pre>${stamp('main.css')}
  - ==> main.css?121345
  - </pre>
  -->
<#function stampNow href>
	<#return commonMacroHelper.addCurrentTimestamp(href) />
</#function>


<#---
  - Returns whether the given URL is external, i.e. has a schema part.
  -->
<#function isExternalUrl href>
	<#return commonMacroHelper.isExternalUrl(href?trim) />
</#function>

<#---
  - Returns the hostName of the given URL
  -->
<#function hostName url>
	<#return commonMacroHelper.getHostName(url?trim) />
</#function>

<#---
  - Converts the given path into an absolute URL by adding the protocol,
  - server-name, port and contextPath of the current request.
  -->
<#function absoluteUrl href>
	<#return commonMacroHelper.getAbsoluteUrl(href?trim) />
</#function>

<#---
  - Returns the path for the given handlerName. 
  -->
<#function pathForHandler handlerName attributes={} prefix="">
	<#return commonMacroHelper.getUrlForHandler(handlerName, attributes, prefix) />
</#function>

<#---
  - Returns the URL for the given handlerName. 
  -->
<#function urlForHandler handlerName attributes={} prefix="">
	<#return url(pathForHandler(handlerName, attributes, prefix)) />
</#function>

<#---
  - Returns the absolute URL for the given handlerName. 
  -->
<#function absoluteUrlForHandler handlerName attributes={} prefix="">
	<#return absoluteUrl(pathForHandler(handlerName, attributes, prefix)) />
</#function>

<#---
  - Tries to replace the given parameter's value in the given URL's query string
  - with the given new value or adds the parameter if it is not yet contained. 
  -->
<#function setParameter url name value>
	<#return commonMacroHelper.setParameter(url, name, value) />
</#function>

<#---
  - Adds the given parameter to the given URL's query string.
  -->
<#function addParameter url name value>
	<#return commonMacroHelper.addParameter(url, name, value) />
</#function>

<#---
  - Adds all request parameters to the given URLs query string.
  -->
<#function addRequestParameters url>
	<#return commonMacroHelper.addRequestParameters(url) />
</#function>

<#---
  - Returns a random item from the given collection.
  -->
<#function randomItem collection>
	<#local index = commonMacroHelper.random.nextInt(collection?size) />
	<#return collection[index] />
</#function>

<#---
  - Returns the current time as datetime value. The value does not change 
  - during template processing. 
  -->
<#function currentTime>
	<#return commonMacroHelper.getCurrentTime()?datetime />
</#function>

<#---
  - Returns whether the given date is in the future. 
  -->
<#function isInFuture date="">
	<#return (date?is_date && date?datetime > currentTime()) />
</#function>

<#---
  - Partitions a collection by inspecting the specified property
  - of the contained items. If the property value is different than the 
  - previous one, a new group is created and added to the returned sequence. 
  - Each group consists of a hash with a 'title' and an 'items' property.    
  -->
<#function partition collection property>
	<#return commonMacroHelper.partition(collection, property) />
</#function>

<#---
  - Shuffles the given collection.
-->
<#function shuffle collection>
	<#return commonMacroHelper.shuffle(collection) />
</#function>

<#---
  - Strips directory names and the query-string from a path.
  - <h4>Example:</h4>
  - <pre>${baseName('/hello/world.html?foo=bar')}
  - ==> world.html
  - </pre>  
  -->
<#function baseName path>
	<#return commonMacroHelper.baseName(path) />
</#function>

<#---
  - Returns the extension of the given file-name. If the validExtensions 
  - parameter is specified, the defaultExtension will be returned if the actual
  - extension is invalid.
  - <h4>Example:</h4>
  - <pre>${fileExtension('foo.html')} ==> html
  - ${fileExtension('foo.bar', ['gif', 'jpg'], 'unknown')} ==> unknown
  - </pre>
  -->
<#function fileExtension filename validExtension=[] defaultExtension="">
	<#return commonMacroHelper.getFileExtension(filename, validExtension, defaultExtension) />
</#function>

<#---
  - Returns a formatted string using an appropriate unit (Bytes, KB or MB).
  -->
<#function formatByteSize bytes>
	<#return commonMacroHelper.formatByteSize(bytes) />
</#function>

<#---
  - Returns a formatted string using the pattern hh:mm:ss. The hours are
  - omitted if they are zero, the minutes are padded with a '0' character
  - if they are less than 10.
  -->
<#function formatMillis millis>
	<#return commonMacroHelper.formatMillis(millis) />
</#function>

<#---
  - Formats the given number using a custom pattern and locale.
  -->
<#function formatNumber number pattern="#0.#" locale="en_US">
	<#return commonMacroHelper.formatNumber(number, pattern, locale) />
</#function>

<#---
  - Rounds the number to a decimal number.
  -->
<#function round number>
	<#return commonMacroHelper.round(number) />
</#function>

<#function toTitleCase s>
	<#return commonMacroHelper.toTitleCase(s) />
</#function>

<#---
  - Strips HTML tags and whitespaces from the given String
  -->
<#function stripTagsAndWhitespaces s>
	<#return commonMacroHelper.stripTagsAndWhitespaces(s) />
</#function>

<#function toDelimitedString collection delim=",">
	<#return commonMacroHelper.toDelimitedString(collection, delim) />
</#function>

<#function getMessage code args...>
	<#return commonMacroHelper.getMessage(code, args) />
</#function>

<#function getMessageWithDefault code default args...>
	<#return commonMacroHelper.getMessageWithDefault(code, default, args) />
</#function>


<#macro message code args=[]><#local default><#nested></#local>${commonMacroHelper.getMessageWithDefault(code, default, args)}</#macro>

<#---
  - @internal
  -->
<#function unwrapAttributes attributes>
	<#if attributes?is_sequence>
		<#return {} />
	<#elseif attributes.attributes?exists>
		<#return attributes.attributes />
	</#if>
	<#return attributes />
</#function>

<#---
  - @internal
  -->
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

<#---
  - Renders an attribute in XML syntax. If the value is an empty string or 
  - contains only whitespaces nothing is rendered. 
  -->
<#macro attribute name value=""><#if value!?trim?has_content> ${name}="${value?trim}"</#if></#macro>

<#---
  - Renders the nested content if the given value is a string and has content.
  - The given value can be accessed in the nested block with ${value}. 
  -->
<#macro if value="">
	<#if !(value?is_string && !value?has_content)><#nested value /></#if>
</#macro>

<#---
  - Wraps the nested content inside an &lt;a&gt;-tag if the href parameter 
  - has any content.
  -->
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

<#macro stylesheets hrefs compress=commonMacroHelper.compressResources rel="stylesheet" type="text/css" attributes...>
	<#if compress>
		<link rel="${rel}" type="${type}" href="${resource(pathForHandler("minifyCssController") + "?files=" + toDelimitedString(hrefs))?xml}"${joinAttributes(attributes)} />
	<#else>
		<#list hrefs as href>
			<link rel="${rel}" type="${type}" href="${resource(href)?xml}"${joinAttributes(attributes)} />
		</#list>
	</#if>
</#macro>

<#macro scripts srcs compress=commonMacroHelper.compressResources type="text/javascript" attributes...>
	<#if compress>
		<script src="${resource(pathForHandler("minifyScriptController") + "?files=" + toDelimitedString(srcs))?xml}" type="${type}"${joinAttributes(attributes)}></script>
	<#else>
		<#list srcs as src>
			<script src="${resource(src)?xml}" type="${type}"${joinAttributes(attributes)}></script>
		</#list>
	</#if>
</#macro>

<#---
  - Emits a script block that removes the 'noscript' class from the document's body element. 
  -->
<#macro removeNoscriptClass>
	<script type="text/javascript">
		var b = document.body;
		var noscriptClass = b.className.replace(/(^|\s+)noscript(\s+|$)/, ' ');
		if (b.className != noscriptClass) {
			b.className = noscriptClass;
		}
	</script>
</#macro>

<#--- 
  - Renders a pager.
  - @see <a href="http://www.riotfamily.org/api/latest/org/riotfamily/website/generic/view/Pager.html">Pager</a>
  -->
<#macro pager prev="&lt;&lt;" next="&gt;&gt;" gap="...">
	<#if pager.pages gt 1>
		<#if pager.prevPage?exists>
			<a class="prev-page" href="${pager.prevPage.link}">${prev}</a>
		</#if>
		
		<#if pager.firstPage?exists>
			<a class="page" href="${pager.firstPage.link}">1</a>
			<#if pager.gapToFirstPage><span class="gap">${gap}</span></#if>
		</#if>
		
		<#list pager.prevPages as page>
			<a class="page" href="${page.link}">${page.number}</a>
		</#list>
		
		<span class="current-page">${pager.currentPage}</span>
		
		<#list pager.nextPages as page>
			<a class="page" href="${page.link}">${page.number}</a>
		</#list>
		
		<#if pager.lastPage?exists>
			<#if pager.gapToLastPage><span class="gap">${gap}</span></#if>
			<a class="page" href="${pager.lastPage.link}">${pager.lastPage.number}</a>
		</#if>
		
		<#if pager.nextPage?exists>
			<a class="next-page" href="${pager.nextPage.link}">${next}</a>
		</#if>
	</#if>
</#macro>

<#function hyphenate html>
	<#return commonMacroHelper.hyphenate(html) />
</#function>

<#function hyphenateAndEncode html>
	<#return encodeLinks(commonMacroHelper.hyphenate(html)) />
</#function>

<#function hyphenatePlainText text>
	<#return commonMacroHelper.hyphenatePlainText(text) />
</#function>

<#---
  - Splits a list into a specified number of groups. The items are distributed
  - evenly. Example:
  - <pre>
  - 1 | 4 | 7
  - 2 | 5 | 8
  - 3 | 6
  - </pre>
  - @param items The items to split
  - @param groups The number of groups (NOT number of group-items)
  - @return The splitted list
  -->
<#function split items groups>
	<#return commonMacroHelper.split(items, groups) />
</#function>

<#---
  - Tags the current cacheItem with the given className
  - @param className The className to tag the cacheItem with
  -->
<#macro tag className>
	${commonMacroHelper.tag(className)}
</#macro>