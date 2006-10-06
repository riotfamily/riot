<#--
  - Macro that renders a message from a MessageSource. This is equivalent to the
  - message macro provided by Spring. If a Riot user is logged in the message 
  - is surrounded by a span that displays the message-code when the user hovers
  - the text with the mouse.
  -
  - If the message code is omitted, the span will display the location of the 
  - FreeMarker template. This can be useful for applications that don't use a
  - MessageSource.
  -->
<#macro message code="">
	<#if code != "">
		<#if viewModeResolver?exists && viewModeResolver.isPreviewMode(request)>
			<span style="padding:0;margin:0;" title="key=${code}">${springMacroRequestContext.getMessage(code)}</span>
		<#else>
			${springMacroRequestContext.getMessage(code)}
		</#if>
	<#else>
		<#if viewModeResolver?exists && viewModeResolver.isPreviewMode(request)>
			<span style="padding:0;margin:0;" title="template=${template}"><#nested /></span>
		<#else>
			<#nested />
		</#if>
	</#if>
</#macro>

<#--
  - Macro that renders a message from a MessageSource. This is equivalent to the
  - messageText macro provided by Spring. If a Riot user is logged in the message 
  - is surrounded by a span that displays the message-code when the user hovers
  - the text with the mouse.
  -->
<#macro messageText code, text>
	<#if viewModeResolver?exists && viewModeResolver.isPreviewMode(request)>
		<span title="key=${code}">${springMacroRequestContext.getMessage(code, text)}</span>
	<#else>
		${springMacroRequestContext.getMessage(code, text)}
	</#if>
</#macro>

<#--
  - Macro that includes the given URI using a RequestDispatcher.
  -->
<#macro include uri>
	${riotInclude(uri)}
</#macro>

<#--
  - Macro that evaluates the nested content if the page is requested in
  - preview mode.
  -->
<#macro ifPreviewMode>
	<#if viewModeResolver?exists>
		<#if viewModeResolver.isPreviewMode(request)>
			<#nested />
		</#if>
	</#if>
</#macro>

<#--
  - Macro that renders the Riot toolbar if the page is requested in preview mode.
  -->
<#macro toolbar stylesheet="">
	<#if viewModeResolver?exists>
		<#if viewModeResolver.isPreviewMode(request)>
			<#if stylesheet?has_content>
				<script type="text/javascript">
					var riotUserStylesheet = "${request.contextPath + stylesheet}";
				</script>
			</#if>
			<script type="text/javascript" src="${riotEncodeUrl(riotResourcePath + '/riot-js/resources.js')}"></script>
			<script type="text/javascript" src="${riotEncodeUrl(riotResourcePath + '/pages.js')}"></script>
		<#else>
			<script type="text/javascript">
				<#-- The following variable is read by the login-bookmarklet: -->
				var riotPagesUrl = '${request.contextPath}${riotServletPrefix}/pages';
			</script>
		</#if>
		
	<#else>
		<div style="background:red;color:#fff;font-weight:bold;border:2px solid #fff;padding:10px;position:absolute;top:0;left:0;width:100%">
			An instance of <code>org.riotfamily.pages.preview.ViewModeResolver</code> 
			must be placed in the FreeMarker context under the key <code>viewModeResolver</code>.
		</div>
	</#if>

</#macro>


<#macro text key tag="" class="">
	<@inplace key=key editor="text" tag=tag class=class><#nested /></@inplace>
</#macro>

<#macro multiline key tag="" class="">
	<@inplace key=key editor="multiline" tag=tag class=class><#nested /></@inplace>
</#macro>

<#macro inplace key editor="richtext" alwaysUseNested=false tag="" class="">
	<#if alwaysUseNested>
		<#local value><#nested /></#local>
	<#else>
		<#local value = (key + "?if_exists")?eval />
		<#if !value?has_content>
			<#local value><#nested /></#local>
		</#if>
	</#if>
		
	<#if class?has_content>
		<#local attrs=" class=\"${class}\"" />
	</#if>
	
	<#if riotComponentEditMode?if_exists>
		<#if tag?has_content>
			<#local element=tag />
		<#else>
			<#local element="div" />
		</#if>
		<${element} riot:key="${key}" riot:editorType="${editor}"${attrs?if_exists}>${value}</${element}>
	<#elseif tag?has_content>
		<${tag}${attrs?if_exists}>${value}</${tag}>
	<#else>
		${value}
	</#if>
</#macro>

<#macro includeUriField>
	<input type="hidden" name="${includeUriParam}" value="${springMacroRequestContext.getUrlPathHelper().getRequestUri(request)}" />
</#macro>