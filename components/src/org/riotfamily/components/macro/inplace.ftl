<#assign scope = .data_model />
<#assign currentListId = "" />
<#assign editMode = inplaceMacroHelper.isEditMode() />

<#--
  - Macro that renders the Riot toolbar if the page is requested in edit mode.
  -->
<#macro toolbar instantPublish=false>
	<#if editMode>
		<#if instantPublish>
			<script type="text/javascript" language="JavaScript">
				var riotInstantPublish = true;
			</script>
		</#if>
		<#list inplaceMacroHelper.toolbarScripts as src>
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
	<#if editMode>
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

<#macro entityList listId>
	<#assign currentListId = listId />
	<#if editMode && inplaceMacroHelper.storeContext()>
		<div class="riot-components" riot:wrapper="entityList" riot:listId="${listId}" riot:controllerId="${common.includeUri}">
			<#nested>
		</div>
	<#else>
		<#nested />
	</#if>
	<#assign currentListId = "" />
</#macro>

<#macro componentSet attributes...>
	<#if editMode && inplaceMacroHelper.storeContext()>
		<div class="riot-components" riot:wrapper="componentSet" riot:controllerId="${common.includeUri}"${common.joinAttributes(attributes)}>
			<#nested>
		</div>
	<#else>
		<#nested />
	</#if>
</#macro>

<#macro entity object form="">
	<#local previousScope = scope />
	<#assign scope = object />
	<#if editMode>
		<#local listId = currentListId />
		<#if !listId?has_content>
			<#local listId = inplaceMacroHelper.getDefaultListId(object) />
		</#if>
		<#local objectId = inplaceMacroHelper.getObjectId(listId, object) />
		
		<#local attributes = {"class": "riot-component", "riot:objectId": objectId} />
		
		<#if form?has_content>
			<#local attributes = attributes + {"riot:form": "/components/entity-form/" + listId + "/" + form + "/" + objectId} />
		</#if>
		
		<#if !currentListId?has_content && inplaceMacroHelper.storeContext()>
			<#local attributes = attributes + {
				"class": "riot-components riot-component",
				"riot:wrapper": "entity",
				"riot:listId": listId,
				"riot:controllerId": common.includeUri} />
		</#if>
		<div ${common.joinAttributes(attributes)}>
			<#nested />
		</div>
	<#else>
		<#nested />
	</#if>
	<#assign scope = previousScope />
</#macro>

<#--
  - Macro that makes content editable via the Riot toolbar. The text is edited
  - in-line, which means that no further markup is supported, except for
  - line-breaks which are converted to <br>-tags.
  -
  - The call delegated to the editable macro, using editor="text".
  - See: <@editable> for a description of the supported parameters.
  -->
<#macro text key tag="" alwaysUseNested=false textTransform=true attributes...>
	<#local attributes = common.unwrapAttributes(attributes) />
	<#if editMode>
		<#local attributes = attributes + {'riot:textTransform': textTransform?string} />
	</#if>
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
  - See: <@editable> for a description of the other parameters.
  -->
<#macro richtext key tag="" alwaysUseNested=false chunk=false attributes...>
	<#compress>
		<#if chunk>
			<#local editor="richtext-chunks" />
		<#else>
			<#local editor="richtext" />
		</#if>
		<@editable key=key editor=editor tag=tag alwaysUseNested=alwaysUseNested attributes=attributes><#nested /></@editable>
	</#compress>
</#macro>

<#macro editable key editor="text" tag="" alwaysUseNested=false attributes... >
	<#compress>
		<#local attributes = common.unwrapAttributes(attributes) />
		<#if alwaysUseNested>
			<#local value><#nested /></#local>
		<#else>
			<#local value = scope[key]?if_exists />
			<#if !value?has_content>
				<#local value><#nested /></#local>
			</#if>
		</#if>
		<#local value = value?trim />
		
		<#if editMode>
			<#if tag?has_content>
				<#local element=tag />
			<#else>
				<#local element="div" />
				<#local attributes = attributes + {"class" : ("riot-editor " + attributes.class?if_exists)?trim} />
			</#if>
			<${element} riot:key="${key}" riot:editorType="${editor}"${common.joinAttributes(attributes)}>${value}</${element}>
		<#elseif tag?has_content>
			<${tag}${common.joinAttributes(attributes)}>${value}</${tag}>
		<#else>
			${value}
		</#if>
	</#compress>
</#macro>

<#macro image key default="" tag="img" minWidth="10" maxWidth="1000" minHeight="10" maxHeight="1000" width="" height="" defaultWidth="100" defaultHeight="100" transform=common.url attributes... >
	<#compress>
		<#if width?has_content>
			<#local minWidth = width />
			<#local maxWidth = width />
			<#local defaultWidth = width />
		</#if>
		<#if height?has_content>
			<#local minHeight = height />
			<#local maxHeight = height />
			<#local defaultHeight = height />
		</#if>
		<#local value = scope[key]!default>
		<#if value?has_content>
			<#if transform?is_string>
				<#local src = transform?replace("*", value) />
			<#else>
				<#local src = transform(value) />
			</#if>
			<#if tag == "img">
				<#local attributes = attributes + {"src": src} />
			<#else>
				<#local attributes = attributes + {"style": "background-image:url(" + src + ");" + attributes.style!} />
			</#if>
		<#elseif editMode>
			<#if tag == "img">
				<#local attributes = attributes + {
					"src": riot.resource("style/images/pixel.gif"),
					"class": ("nosrc " + attributes.class!)?trim,
					"width": defaultWidth,
					"height": defaultHeight		
					} />
			<#else>
				<#local attributes = attributes + {
					"class": ("nosrc " + attributes.class!)?trim
					} />
			</#if>	
		</#if>
		<#if editMode>
			<#if transform?is_string>
				<#local srcTemplate = transform />
			<#else>
				<#local srcTemplate = transform("/*") />
			</#if>
			<#local attributes = attributes + {
				"class": ("riot-editor " + attributes.class!)?trim,
				"riot:editorType": "image",
				"riot:key": key,
				"riot:srcTemplate": srcTemplate,
				"riot:minWidth": minWidth,
				"riot:maxWidth": maxWidth,
				"riot:minHeight": minHeight,
				"riot:maxHeight": maxHeight 
				} />
		</#if>
		<#if value?has_content || editMode>
			<#if tag == "img">	
				<img${common.joinAttributes(attributes)} />
			<#else>
				<${tag}${common.joinAttributes(attributes)}><#nested /></${tag}>
			</#if>
		</#if>
	</#compress>
</#macro>

<#macro link key href tag="a" externalClass="externalLink" externalTarget="_blank" alwaysUseNested=false textTransform=true attributes...>
	<#local attributes = common.unwrapAttributes(attributes) + {"href": href} />
	<#if common.isExternalUrl(href)>
		<#local attributes = attributes + {
			"target": externalTarget,
			"class": ((attributes.class!) + " " + externalClass)?trim
		} />
	</#if>
	<@text key=key tag=tag alwaysUseNested=alwaysUseNested textTransform=textTransform attributes=attributes><#nested /></@text>
</#macro>

<#--
  - Returns the properties for the given container.
  -->
<#function buildModel container>
	<#return inplaceMacroHelper.buildModel(container) />
</#function>

<#--
  -
  -->
<#macro use container form="" tag="" attributes...>
	<#local attributes = common.unwrapAttributes(attributes) />
	<#local previousScope = scope />
	<#assign scope = buildModel(container) />
	${inplaceMacroHelper.tag(container)}
	<#if editMode>
		<#if !tag?has_content>
			<#local tag = "span" />
			<#local attributes = attributes + {
				"class": ("riot-phantom " + attributes.class!)?trim
			} />
		</#if>
		<#local attributes = attributes + {
				"riot:containerId": container.id?c,
				"class": ("riot-component " + attributes.class!)?trim
		} />
		<#if form?has_content>
			<#local formUrl = inplaceMacroHelper.getFormUrl(form, container.id)! />
			<#local attributes = attributes + {"riot:form": formUrl} />
		</#if>
		<#if container.dirty>
			<#local attributes = attributes + {"riot:dirty": "true"} />
		</#if>
	</#if>
	<#if tag?has_content>
		<${tag}${common.joinAttributes(attributes)}>
			<#nested>
		</${tag}>
	<#else>
		<#nested>
	</#if>
	<#assign scope = previousScope />
</#macro>
