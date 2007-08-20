<#global currentVersionContainer = "" />

<#--
  - Returns whether the page is requested in edit mode.
  -->
<#function isEditMode>
	<#return componentMacroHelper.isEditMode() />
</#function>

<#--
  - Macro that renders the nested content if the page is requested in edit mode.
  -->
<#macro ifEditMode>
	<#if isEditMode()><#nested></#if>
</#macro>

<#--
  - Macro that renders the nested content if the page is requested in live mode.
  -->
<#macro ifLiveMode>
	<#if !isEditMode()><#nested></#if>
</#macro>

<#--
  - Macro that renders the Riot toolbar if the page is requested in edit mode.
  -->
<#macro toolbar instantPublish=false>
	<#if isEditMode()>
		<#if instantPublish>
			<script type="text/javascript" language="JavaScript">
				var riotInstantPublish = true;
			</script>
		</#if>
		<#list componentMacroHelper.toolbarScripts as src>
			<@riot.script src=src />
		</#list>
	<#else>
		<script type="text/javascript" language="JavaScript">
			// This variable is read by the login-bookmarklet:
			var riotPagesUrl = '${riot.href("/pages")}';
		</script>
	</#if>
</#macro>

<#--
  - Macro that enables the Riot JavaScript edit-callbacks. The callback functions
  - are invoked when a component-list is re-rendered. To register a custom callback
  - add the following code as nested content:
  - addRiotEditCallback(function(el) {
  -     alert('ComponentList updated: ' + el.componentList.id);
  - });
  -->
<#macro callbacks>
	<#if isEditMode()>
		<!-- Riot edit callbacks -->
		<script type="text/javascript" language="JavaScript">
		var riotEditCallbacks = [];
		function addRiotEditCallback(callback) {
			riotEditCallbacks.push(callback);
		}
		<#nested />
		</script>
	</#if>
</#macro>

<#--
  - Macro that makes content editable via the Riot toolbar. The text is edited
  - in-line, which means that no further markup is supported, except for
  - line-breaks which are converted to <br>-tags.
  -
  - The call delegated to the editable macro, using editor="text".
  - See: <@editable> for a description of the supported parameters.
  -->
<#macro text key container=currentVersionContainer form="" tag="" alwaysUseNested=false attributes...>
	<@editable key=key container=container form=form editor="text" tag=tag alwaysUseNested=alwaysUseNested attributes=attributes><#nested /></@editable>
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
  - See: <@editable> for a description of the other parameters.
  -->
<#macro richtext key container=currentVersionContainer form="" tag="" alwaysUseNested=false chunk=false attributes...>
	<#compress>
		<#if chunk>
			<#local editor="richtext-chunks" />
		<#else>
			<#local editor="richtext" />
		</#if>
		<@editable key=key container=container form=form editor=editor tag=tag alwaysUseNested=alwaysUseNested attributes=attributes><#nested /></@editable>
	</#compress>
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
<#macro editable key container=currentVersionContainer editor="text" form="" tag="" alwaysUseNested=false attributes... >
	<#compress>
		<#local previousContainer = currentVersionContainer />
		<#global currentVersionContainer = container />
		<#if container != previousContainer>
			${componentMacroHelper.tag(container)}
		</#if>
		<#if attributes.attributes?exists>
			<#local attributes=attributes.attributes />
		</#if>
	
		<#if alwaysUseNested>
			<#local value><#nested /></#local>
		<#else>
			<#if container?has_content>
				<#local value = container.getProperty(key, isEditMode())?if_exists />
			<#else>
				<#local value = .data_model[key]?if_exists />
			</#if>
			<#if !value?has_content>
				<#local value><#nested /></#local>
			</#if>
		</#if>
		<#local value = value?trim />
		
		<#if isEditMode()>
			<#if container != previousContainer>
				<#local attributes = addContainerAttributes(attributes, container, form) />
			</#if>
			<#if tag?has_content>
				<#local element=tag />
			<#else>
				<#local element="div" />
				<#local attributes = attributes + {"class" : ("riot-editor " + attributes["class"]?if_exists)?trim} />
			</#if>
			<${element} riot:key="${key}" riot:editorType="${editor}"${join(attributes)}>${value}</${element}>
		<#elseif tag?has_content>
			<${tag}${join(attributes)}>${value}</${tag}>
		<#else>
			${value}
		</#if>
		<#global currentVersionContainer = previousContainer />
	</#compress>
</#macro>

<#--
  - Returns the properties for the given container.
  -->
<#function properties container>
	<#return componentMacroHelper.getProperties(container) />
</#function>

<#--
  -
  -->
<#macro use container form="" tag="" attributes ...>
	<#local previousContainer = currentVersionContainer />
	<#global currentVersionContainer = container />
	<#if container != previousContainer>
		${componentMacroHelper.tag(container)}
	</#if>
	<#if isEditMode() && container != previousContainer>
		<#if !tag?has_content>
			<#local tag = "div" />
		</#if>
		<#local attributes = addContainerAttributes(attributes, container, form) />
	</#if>
	<#if tag?has_content>
		<${tag}${join(attributes)}>
			<#nested container.getProperties(isEditMode())>
		</${tag}>
	<#else>
		<#nested container.getProperties(isEditMode())>
	</#if>
	<#global currentVersionContainer = previousContainer />
</#macro>

<#function addContainerAttributes attributes versionContainer form="">
	<#if attributes.attributes?exists>
		<#local attributes = attributes.attributes />
	</#if>
	<#if versionContainer?has_content && component.isEditMode()>
		<#local attributes = attributes + {
				"riot:containerId": versionContainer.id,
				"class": ("riot-component " + attributes["class"]?if_exists)?trim
		} />
		<#if form?has_content>
			<#local formUrl = componentMacroHelper.getFormUrl(form, versionContainer.id)?if_exists />
			<#if formUrl?has_content>
				<#local attributes = attributes + {"riot:form": formUrl} />
			</#if>
		</#if>
		<#if versionContainer.dirty>
			<#local attributes = attributes + {"riot:dirty": "true"} />
		</#if>
	</#if>
	<#return attributes />
</#function>

<#function join attributes>
	<#local attrs = "" />
	<#list attributes?keys as attributeName>
		<#if attributes[attributeName]?has_content>
			<#local attrs = attrs + " " + attributeName + "=\"" + attributes[attributeName]?html + "\"" />
		</#if>
	</#list>
	<#return attrs />
</#function>
