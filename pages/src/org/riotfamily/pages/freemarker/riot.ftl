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
	<#if viewModeResolver?exists && viewModeResolver.isPreviewMode(request)>
		<#nested />
	</#if>
</#macro>

<#--
  - Macro that evaluates the nested content if the page is requested in
  - live mode.
  -->
<#macro ifLiveMode>
	<#if !viewModeResolver?exists || !viewModeResolver.isPreviewMode(request)>
		<#nested />
	</#if>
</#macro>

<#--
  - Returns the current page.
  -->
<#function getPage>
	<#return riotMacroPageHelper.getPage(request) />
</#function>

<#--
  - Macro that renders the Riot toolbar if the page is requested in preview mode.
  -->
<#macro toolbar>
	<#if viewModeResolver?exists>
		<#if viewModeResolver.isPreviewMode(request)>
			<#if riotToolbarResources?has_content>
				<#list riotToolbarResources as resource>
					<script type="text/javascript" src="${riotEncodeUrl(riotResourcePath + resource)}"></script>
				</#list>
			</#if>			
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

<#-- 
  - Macro that makes content editable via the Riot toolbar. The text is edited
  - in-line, which means that no further markup is supported, except for 
  - line-breaks which are converted to <br>-tags.
  -
  - The call delegated to the editable macro, using editor="text".
  - See: <@riot.editable> for a description of the supported parameters.
  -->
<#macro text key tag="" alwaysUseNested=false attributes...>
	<@editable key=key editor="text" tag=tag alwaysUseNested=alwaysUseNested attributes=attributes><#nested /></@editable>
</#macro>

<#-- 
  - Macro that makes content editable via the Riot toolbar. The text is edited
  - via TinyMCE which is displayed as 'inline-popup'.
  -
  - chunk: If set to true, the content will be split up into multiple 
  - components for each top-level block element. Default is false.
  - 
  - The call delegated to the editable macro, using editor="richtext" or
  - editor="richtext-chunks", depending on the chunk parameter.
  - See: <@riot.editable> for a description of the other parameters.
  -->
<#macro richtext key tag="" alwaysUseNested=false chunk=false attributes...>
	<#if chunk>
		<#local editor="richtext-chunks" />
	<#else>
		<#local editor="richtext" />
	</#if>
	<@editable key=key editor=editor tag=tag alwaysUseNested=alwaysUseNested attributes=attributes><#nested /></@editable>
</#macro>

<#--
  - Deprecated: Inline-text is always 'multiline'.
  -->
<#macro multiline key tag="" alwaysUseNested=false attributes...>
	<@editable key=key editor="text" tag=tag alwaysUseNested=alwaysUseNested attributes=attributes><#nested /></@editable>
</#macro>

<#--
  - Deprecated: Use the richtext macro instead.
  -->
<#macro inplace key editor="richtext" tag="" alwaysUseNested=false attributes...>
	<@editable key=key editor=editor tag=tag alwaysUseNested=alwaysUseNested attributes=attributes><#nested /></@editable>
</#macro>

<#-- 
  - Macro that makes content editable via the Riot toolbar.
  -
  - key: Name of the model-key/variable to edit
  -
  - editor: Name of the editor widget to use. Can be either 'text', 'richtext' 
  - or 'richtext-chunks'. Default is 'text'.
  -
  - alwaysUseNested: If true, the nested code is always evaluated, otherwise
  -   the nested code is only evaluated if no value is found in model under 
  -   the given key.
  -
  - tag: HTML tag that is used to surround the editable content. If omitted,
  -   a 'div' is used in edit-mode and no tag in live-mode.
  -
  - attributes...: Attributes to set on the surrounding tag.
  -->
<#macro editable key editor="text" tag="" alwaysUseNested=false attributes... >
	<#if alwaysUseNested>
		<#local value><#nested /></#local>
	<#else>
		<#local value = .vars[key]?if_exists />
		<#if !value?has_content>
			<#local value><#nested /></#local>
		</#if>
	</#if>
	
	<#if attributes?has_content>
		<#if attributes.attributes?exists>
			<#local attributes=attributes.attributes />
		</#if>
		<#local attrs="" />
		<#local keys=attributes?keys />
		<#list keys as key>
			<#local attrs=attrs + " " + key + "=\"" + attributes[key] + "\"" />
		</#list>
	</#if>
	
	<#if riotComponentEditMode?if_exists>
		<#if tag?has_content>
			<#local element=tag />
		<#else>
			<#local element="div" />
			<#local attrs=attrs + " class=\"riot-editor\"" />
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