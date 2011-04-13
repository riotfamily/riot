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
  - Includes the given path using a RequestDispatcher. 
  - @param path The path to include
  -->
<#macro include path dynamic=true>
	${commonMacroHelper.include(path, dynamic)}
</#macro>

<#---
  - Performs a request using a RequestDispatcher and returns the captured output.
  - @param path The path to capture
  -->
<#function capture path="">
	<#return commonMacroHelper.capture(path) />
</#function>

<#function handle handlerName attributes...>
	<#return commonMacroHelper.include(commonMacroHelper.getUrlForHandler(handlerName, attributes), true) />
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
  - Adds the sessionId to the given URI if necessary.
  - <h4>Example:</h4>
  - <pre>${encode('/foo.html')}
  - ==> /context/foo.html;jsessionid=...
  - </pre> 
  -->
<#function encode href>
	<#return commonMacroHelper.encodeUrl(href?trim) />
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
  - Adds the current locale to the given path and checks whether such a file
  - exists. The search strategy is the same as the one used by the Java
  - ResourceBundle class.
  - <p>
  - The given path must be context-relative, i.e. it must not contain the 
  - contextPath.
  - </p>
  - <p>
  - <b>Example:</b> If the request locale is <code>de_DE</code>, the expression
  - <ode>${resource(localize('/foo/bar.jpg'))}</code> will look for the 
  - following files and return the first match:
  - <ol>
  -  <li><i>&lt;webapp-root&gt;</i>/foo/bar_de_DE.jpg</li>
  -  <li><i>&lt;webapp-root&gt;</i>/foo/bar_de.jpg</li>
  -  <li><i>&lt;webapp-root&gt;</i>/foo/bar.jpg</li>  
  - </ol>
  - </p>
  -->
<#function localize path>
	<#return commonMacroHelper.localize(path) />
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
<#function pathForHandler handlerName attributes...>
	<#return commonMacroHelper.getUrlForHandler(handlerName, attributes) />
</#function>

<#---
  - Returns the URL for the given handlerName. 
  -->
<#function urlForHandler handlerName attributes...>
	<#return url(commonMacroHelper.getUrlForHandler(handlerName, attributes)) />
</#function>

<#---
  - Returns the absolute URL for the given handlerName. 
  -->
<#function absoluteUrlForHandler handlerName attributes...>
	<#return absoluteUrl(commonMacroHelper.getUrlForHandler(handlerName, attributes)) />
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

<#function dateFormat>
	<#return commonMacroHelper.getDateFormat() />
</#function>

<#function dateDelimiter>
	<#return commonMacroHelper.getDateDelimiter() />
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
  - Returns the extension of the given file-name. If the validExtensions 
  - parameter is specified, the defaultExtension will be returned if the actual
  - extension is invalid.
  - <h4>Example:</h4>
  - <pre>${fileExtension('foo.html')} ==> html
  - ${fileExtension('foo.bar', ['gif', 'jpg'], 'unknown')} ==> unknown
  - </pre>
  -->
<#function fileExtension filename validExtension=[] defaultExtension="">
	<#local ext = FormatUtils.getExtension(filename) />
	<#if !validExtensions?has_content || validExtensions?seq_contains(ext)>
		<#return ext />
	</#if>
	<#return defaultExtension />
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

<#function toDelimitedString collection delim=",">
	<#return commonMacroHelper.toDelimitedString(collection, delim) />
</#function>

<#function getMessage code args...>
	<#return commonMacroHelper.getMessage(code, args) />
</#function>

<#function getMessageWithDefault code default args...>
	<#return commonMacroHelper.getMessageWithDefault(code, default, args, locale)! />
</#function>

<#macro message code args=[] locale=locale><#local default><#nested></#local>${commonMacroHelper.getMessageWithDefault(code, default, args, locale)!}</#macro>

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
  - Wraps the nested content inside the specified tag, if parameter "if" 
  - evaluates to true.
  -->
<#macro wrap if tag attributes...>
	<#if if><${tag}${joinAttributes(attributes)}><#nested /></${tag}><#else><#nested /></#if><#t>
</#macro>

<#---
  - Wraps the nested content inside an &lt;a&gt;-tag if the href parameter 
  - has any content.
  -->
<#macro link href="" tag="a" externalClass="externalLink" externalTarget="_blank" transform=false attributes...>
	<#if href?has_content>
		<#if transform?is_macro>
			<#local href = transform(href) />
		</#if>
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
		<#local files = statics["org.riotfamily.common.web.performance.MinifyCssController"].buildParam(hrefs) />
		<link rel="${rel}" type="${type}" href="${resource(pathForHandler("minifyCssController") + "?files=" + files?xml)}"${joinAttributes(attributes)} />
	<#else>
		<#if attributes?is_sequence>
			<#local attributes = {} />
		</#if>
		<#list hrefs as sheet>
			<#local attrs = attributes />
			<#if sheet?is_hash>
				<#local href = sheet.href />
				<#if sheet.media??>
					<#local attrs = attrs + {"media": sheet.media} />
				</#if>
			<#else>
				<#local href = sheet />
			</#if>
			<link rel="${rel}" type="${type}" href="${resource(href)?xml}"${joinAttributes(attrs)} />
		</#list>
	</#if>
</#macro>

<#macro stylesheet href rel="stylesheet" type="text/css" attributes...>
	<link rel="${rel}" type="${type}" href="${resource(href)?xml}"${joinAttributes(attributes)} />
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
 - Renders a body tag with condtional comments to target different IE versions via CSS selectors.
 - The output will look like this:
 - <pre>
 - &lt;!--[if lt IE 7]&gt;  &lt;body class="noscript ie ie6"&gt; &lt;![endif]--&gt; 
 - &lt;!--[if IE 7]&gt;     &lt;body class="noscript ie ie7"&gt; &lt;![endif]--&gt; 
 - &lt;!--[if IE 8]&gt;     &lt;body class="noscript ie ie8"&gt; &lt;![endif]--&gt; 
 - &lt;!--[if !IE]&gt;&lt;!--&gt; &lt;body class="noscript"&gt;&lt;!--    &lt;![endif]--&gt;
 - 	...
 - &lt;/body&gt;
 - </pre>
 - Inside the body tag a script will be rendered that removes the 'noscript' class
 - (see <a href="#removeNoscriptClass">&lt;@c.removeNoscriptClass /&gt;</a>).
 -->
<#macro body class="" attributes...>
<#local class = (class + " noscript")?trim />
<!--[if lt IE 7]>  <body class="${class} ie ie6"${joinAttributes(attributes)}> <![endif]--> 
<!--[if IE 7]>     <body class="${class} ie ie7"${joinAttributes(attributes)}> <![endif]--> 
<!--[if IE 8]>     <body class="${class} ie ie8"${joinAttributes(attributes)}> <![endif]--> 
<!--[if !IE]><!--> <body class="${class}"${joinAttributes(attributes)}><!--    <![endif]-->
	<@removeNoscriptClass />
	<#nested>
</body>
</#macro>

<#---
  - Emits a script block that removes the 'noscript' class from the document's body element. 
  -->
<#macro removeNoscriptClass>
	<script type="text/javascript">
		(function() {
			var b = document.body;
			var noscriptClass = b.className.replace(/(^|\s+)noscript(\s+|$)/, ' ');
			if (b.className != noscriptClass) {
				b.className = noscriptClass;
			}
		})();
	</script>
</#macro>

<#--- 
  - Renders a pager.
  - @see <a href="http://www.riotfamily.org/api/latest/org/riotfamily/website/generic/view/Pager.html">Pager</a>
  -->
<#macro pager pager prev="&lt;&lt;" next="&gt;&gt;" gap="&hellip;">
	<#if pager?has_content && pager.pages gt 1>
		<#if pager.prevPage?exists>
			<a class="prev-page" href="${pager.prevPage.link?html}">${prev}</a>
		</#if>
		
		<#if pager.firstPage?exists>
			<a class="page" href="${pager.firstPage.link?html}">1</a>
			<#if pager.gapToFirstPage><span class="gap">${gap}</span></#if>
		</#if>
		
		<#list pager.prevPages as page>
			<a class="page" href="${page.link?html}">${page.number}</a>
		</#list>
		
		<span class="current-page">${pager.currentPage}</span>
		
		<#list pager.nextPages as page>
			<a class="page" href="${page.link?html}">${page.number}</a>
		</#list>
		
		<#if pager.lastPage?exists>
			<#if pager.gapToLastPage><span class="gap">${gap}</span></#if>
			<a class="page" href="${pager.lastPage.link?html}">${pager.lastPage.number}</a>
		</#if>
		
		<#if pager.nextPage?exists>
			<a class="next-page" href="${pager.nextPage.link?html}">${next}</a>
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

<#assign exposeAsVariables = commonMacroHelper.exposeAsVariablesDirective />
