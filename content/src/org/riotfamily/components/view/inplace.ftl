<#---
  - Macros to edit texts via the front-office.
  - @namespace inplace
  -->

<#---
  - Whether the page is viewed in edit-mode.
  - <h4>Example:</h4>
  - <pre>
  - &lt;#if inplace.editMode&gt;
  -	  Hello Riot user!
  - &lt;#else&gt;
  -	  Hello visitor!
  - &lt;/#if&gt;
  - </pre>
  -->
<#assign editMode = inplaceMacroHelper.isEditMode() />

<#macro register content>
	<#if editMode && content.container??>
		<a class="riot-container" style="display:none" rel="${content.container.id}" ></a>
	</#if>
</#macro>

<#---
  - Macro that renders the Riot toolbar if the page is requested in edit-mode.
  - If no user is logged in, a JavaScript block is rendered that identifies the
  - page as Riot page, hence enables the use of the login bookmarklet.
  - @param bookmarklet Whether bookmarklet support should be enabled 
  -->
<#macro toolbar bookmarklet=true>
	<#if editMode>
		<#if contentMap??>
			<@register contentMap />
		</#if>
		<#list inplaceMacroHelper.toolbarScripts as src>
			<@riot.script src = src + "?lang=" + .lang />
		</#list>
		<script type="text/javascript" language="JavaScript">
			var riotComponentFormParams = {};
			${inplaceMacroHelper.initScript}
			<#nested />
		</script>
	<#elseif bookmarklet>
		<script type="text/javascript" language="JavaScript">
			// This variable is read by the login-bookmarklet:
			var riotPagesUrl = '${c.url(riot.resource("/pages"))}';
		</script>
	</#if>
</#macro>

<#---
 - @deprecated Please use @inplace.components instead.
 -->
<#macro componentList key min=0 max=1000 initial=[] valid=[] x=0 y=0>
	<@components key min max initial valid x y />
</#macro>

<#macro components key min=0 max=1000 initial=[] valid=[] x=0 y=0>
	<#if !contentMap??>
		<#stop "No contentMap found in model">
	</#if>
	${inplaceMacroHelper.renderComponents(contentMap, key, min, max, initial, valid, x, y)!}
</#macro>

<#--- @internal -->
<#function readOnlyComponent request=request>
	<#return request.getAttribute("readOnlyComponent")!false /> 
</#function>

<#---
  - Macro that makes content editable via the Riot toolbar. The text is edited
  - in-line, which means that no further markup is supported, except for
  - line-breaks which are converted to &lt;br /&gt; tags.
  -
  - @param key The model key that contains the content.
  - @param tag Name of the HTML tag that surrounds the text. If not specified, 
  -	       no tag will be rendered (Note: for technical reasons a tag is 
  -		   always rendered when the page is viewed in edit-mode).
  - @param alwaysUseNested If set to true, the nested content is always 
  -        evaluated. Otherwise the nested content is used as default, if no 
  -        value is found in the model under the specified key. Use this flag
  -        if you need to perform additional transformations.
  - @param textTransform If set to true, the CSS text-transform property of 
  -        the edited element is also applied to the input field. You may turn
  -        this feature off so that users can verify the correct case of 
  -        their input.
  - @param hyphenate Whether soft hyphens should be inserted automatically. 
  -->
<#macro text key tag="" alwaysUseNested=false textTransform=true hyphenate=false attributes...>
	<#local attributes = c.unwrapAttributes(attributes) />
	<#if editMode && !readOnlyComponent()>
		<#local attributes = attributes + {'riot:textTransform': textTransform?string} />
	</#if>
	<#if hyphenate>
		<#local transform = c.hyphenate />
	<#else>
		<#local transform = false />
	</#if>
	<@editable key=key editor="text" tag=tag alwaysUseNested=alwaysUseNested transform=transform attributes=attributes><#nested /></@editable>
</#macro>

<#---
  - Macro that makes content editable via the Riot toolbar. The text is edited
  - via TinyMCE which is displayed as inline-popup.
  -
  - @param key The model key that contains the content.
  - @param tag Name of the HTML tag that surrounds the text. If not specified, 
  -	       no tag will be rendered (Note: for technical reasons a tag is 
  -		   always rendered when the page is viewed in edit-mode).
  - @param config Name of a TinyMCE config set.
  - @param alwaysUseNested If set to true, the nested content is always 
  -        evaluated. Otherwise the nested content is used as default, if no 
  -        value is found in the model under the specified key. Use this flag
  -        if you need to perform additional transformations.
  - @param chunk If set to true, the content will be split up into multiple
  -        components for each top-level block element.
  - @param hyphenate Whether soft hyphens should be inserted automatically.
  -->
<#macro richtext key tag="" config="default" alwaysUseNested=false chunk=false hyphenate=false attributes...>
	<#compress>
		<#if editMode && !readOnlyComponent()>
			<#local attributes = c.unwrapAttributes(attributes) + {"riot:config": config} />
		</#if>
		<#local editor = chunk?string("richtext-chunks", "richtext") />
		<#if hyphenate>
			<#local transform = c.hyphenateAndEncode />
		<#else>
			<#local transform = c.encodeLinks />
		</#if>
		<@editable key=key editor=editor tag=tag alwaysUseNested=alwaysUseNested transform=transform attributes=attributes><#nested /></@editable>
	</#compress>
</#macro>

<#--- @internal -->
<#macro editable key editor="text" tag="" alwaysUseNested=false transform=false attributes... >
	<#compress>
		<#if !contentMap??>
			<#stop "No contentMap found in model">
		</#if>
		<#local attributes = c.unwrapAttributes(attributes) />
		<#if alwaysUseNested>
			<#local value><#nested /></#local>
		<#else>
			<#local value = contentMap[key]?if_exists />
			<#if !value?has_content>
				<#local value><#nested /></#local>
			</#if>
		</#if>
		<#local value = value?trim />
		<#if transform?is_macro>
			<#local value = transform(value) />
		</#if>
		
		<#if inplaceMacroHelper.isEditable(contentMap)  && !readOnlyComponent()>
			<#if tag?has_content>
				<#local element=tag />
			<#else>
				<#local element="div" />
			</#if>
			<#local attributes = attributes + {"class" : ("riot-content riot-text-editor " + attributes.class?if_exists)?trim} />
			<${element} riot:key="${key}" riot:contentId="${contentMap.compositeId}" riot:editorType="${editor}"${c.joinAttributes(attributes)}>${value}</${element}>
		<#elseif tag?has_content>
			<${tag}${c.joinAttributes(attributes)}>${value}</${tag}>
		<#else>
			${value}
		</#if>
	</#compress>
</#macro>

<#macro properties form tag="div" content=contentMap>
	<#if inplaceMacroHelper.isEditable(content)>
		<${tag} class="riot-content riot-form" riot:contentId="${content.compositeId}" riot:form="${form}"><#nested /></${tag}>
	<#else>
		<#nested />
	</#if>
</#macro>

<#macro use content>
	<#local previousContent = contentMap!{} />
	<#global contentMap = content />
	<@register content />
	<#nested />
	<#global contentMap = previousContent />
</#macro>

<#macro image key default="" transform=c.resolve attributes... >
	<#compress>
		<#local value = (contentMap[key].uri)!default>
		<#if value?has_content>
			<#if transform?is_string>
				<#local src = transform?replace("*", value) />
			<#else>
				<#local src = transform(value) />
			</#if>
			<#local attributes = attributes + {"src": src} />
			<#if !attributes.width?has_content && contentMap[key]??>
				<#local attributes = attributes + {"width": contentMap[key].width?c} />
			</#if>
			<#if !attributes.height?has_content && contentMap[key]??>
				<#local attributes = attributes + {"height": contentMap[key].height?c} />
			</#if>
			<#if !attributes.alt?has_content>
				<#local attributes = attributes + {"alt": " "} />
			</#if>
			<img${c.joinAttributes(attributes)} />
		<#elseif editMode>
			<#nested />
		</#if>
	</#compress>
</#macro>

<#macro link key href tag="a" externalClass="externalLink" externalTarget="_blank" alwaysUseNested=false textTransform=true hyphenate=false attributes...>
	<#local attributes = c.unwrapAttributes(attributes) + {"href": href} />
	<#if c.isExternalUrl(href)>
		<#local attributes = attributes + {
			"target": externalTarget,
			"class": ((attributes.class!) + " " + externalClass)?trim
		} />
	</#if>
	<@text key=key tag=tag alwaysUseNested=alwaysUseNested textTransform=textTransform hyphenate=hyphenate attributes=attributes><#nested /></@text>
</#macro>

<#---
  - Returns either <code>"even"</code> or <code>"odd"</code>, depending on
  - the position of a component within a list.
  -->
<#function zebraClass>
	<#if position % 2 == 0>
		<#return "even" />
	<#else>
		<#return "odd" />
	</#if>
</#function>

<#---
  - Returns <code>"every-xxx"</code>, depending on the position of a component 
  - within a list. For example <code>moduloClass(3)</code> returns 
  - <code>"every-3rd"</code> for every third component in the list and an empty
  - string for all other components.
  -->
<#function moduloClass pos>
	<#if pos == 2>
		<#local indicator = "nd" />
	<#elseif pos == 3>
		<#local indicator = "rd" />
	<#else>
		<#local indicator = "th" />
	</#if>
	<#if (position + 1) % pos == 0>
		<#return "every-" + pos + indicator />
	<#elseif editMode>
		<#return "not-every-" + pos + indicator />
	<#else>
		<#return "" />
	</#if>
</#function>

<#---
  - Returns <code>"every-xxx-remainder-y"</code>, depending on the position of a component 
  - within a list. For example <code>moduloClass(3,1)</code> returns 
  - <code>"every-3rd-remainder-1"</code> for the first, fourth, seventh, ... component in the list and an empty
  - string for all other components.
  -->
<#function moduloRemainderClass pos remainder>
	<#if pos == 2>
		<#local indicator = "nd" />
	<#elseif pos == 3>
		<#local indicator = "rd" />
	<#else>
		<#local indicator = "th" />
	</#if>
	<#if (position + 1) % pos == remainder>
		<#return "every-" + pos + indicator + "-remainder-" + remainder />
	<#elseif editMode>
		<#return "not-every-" + pos + indicator + "-remainder-" + remainder />
	<#else>
		<#return "" />
	</#if>
</#function>


<#--- 
  - Returns <code>"first"</code> when the component is the first component
  - within a list, an empty string otherwise.
  -->
<#function firstClass>
	<#if position == 0>
		<#return "first" />
	<#elseif editMode>
		<#return "not-first" />
	<#else>
		<#return "" />
	</#if>
</#function>

<#--- 
  - Returns <code>"last"</code> when the component is the last component
  - within a list, an empty string otherwise.
  -->
<#function lastClass>
	<#if position == listSize - 1>
		<#return "last" />
	<#elseif editMode>
		<#return "not-last" />
	<#else>
		<#return "" />
	</#if>
</#function>
