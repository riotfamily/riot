<#---
  - Macros to edit texts and images via the frontoffice.
  - @namespace inplace
  -->

<#--- @internal -->
<#assign currentModel = .data_model />

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

<#---
  - Macro that renders the Riot toolbar if the page is requested in edit-mode.
  - If no user is logged in, a JavaScript block is rendered that identifies the
  - page as Riot page, hence enables the use of the login bookmarklet.
  - @param bookmarklet Whether bookmarklet support should be enabled 
  -->
<#macro toolbar bookmarklet=true>
	<#if editMode>
		<#list inplaceMacroHelper.toolbarScripts as src>
			<@riot.script src = src + "?lang=" + .lang />
		</#list>
		<script type="text/javascript" language="JavaScript">
			var riotComponentFormParams = {};
			${inplaceMacroHelper.initScript}
			<#nested />
		</script>
	<#else>
		<#if inplaceMacroHelper.isLiveModePreview()>
			<@riot.stylesheet href="style/toolbar.css" />
			<script type="text/javascript" language="JavaScript">
				parent.riot.toolbar.renderProxy(document);
			</script>
		<#elseif bookmarklet>
			<script type="text/javascript" language="JavaScript">
				// This variable is read by the login-bookmarklet:
				var riotPagesUrl = '${riot.href("/pages")}';
			</script>
		</#if>
	</#if>
</#macro>

<#---
  - <p>
  - Macro that enables the Riot JavaScript edit-callbacks. The callback 
  - functions are invoked when a controller is re-rendered via AJAX. 
  - To register a custom callback, add the following code as nested content:
  - </p>
  - <pre>
  - addRiotEditCallback(function(el) {
  -     alert('ComponentList updated: ' + el.componentList.id);
  - });
  - </pre>
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

<#macro componentList key min=0 max=1000 initial=[] valid=[]>
	<#if !currentContainer??>
		<#stop "Use inplace.use to select a container">
	</#if>
	${inplaceMacroHelper.renderComponentList(currentContainer, key, min, max, initial, valid)!}
</#macro>

<#macro nestedComponentList key min=0 max=1000 initial=[] valid=[]>
	<#if !this??>
		<#stop "Nested lists can only be used inside a component view">
	</#if>
	${inplaceMacroHelper.renderNestedComponentList(this, key, min, max, initial, valid)!}
</#macro>

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
	<#if editMode>
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
		<#if editMode>
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
		<#if !currentContainer?? && !this??>
			<#stop "Use inplace macros can only be used inside components or inplace.use tags">
		</#if>
		<#local attributes = c.unwrapAttributes(attributes) />
		<#if alwaysUseNested>
			<#local value><#nested /></#local>
		<#else>
			<#local value = currentModel[key]?if_exists />
			<#if !value?has_content>
				<#local value><#nested /></#local>
			</#if>
		</#if>
		<#local value = value?trim />
		<#if transform?is_macro>
			<#local value = transform(value) />
		</#if>
		
		<#if editMode>
			<#if tag?has_content>
				<#local element=tag />
			<#else>
				<#local element="div" />
			</#if>
			<#local attributes = attributes + {"class" : ("riot-text-editor " + attributes.class?if_exists)?trim} />
			<${element} riot:key="${key}" riot:editorType="${editor}"${c.joinAttributes(attributes)}>${value}</${element}>
		<#elseif tag?has_content>
			<${tag}${c.joinAttributes(attributes)}>${value}</${tag}>
		<#else>
			${value}
		</#if>
	</#compress>
</#macro>

<#macro image key default="" tag="img" minWidth="10" maxWidth="1000" minHeight="10" maxHeight="1000" width="" height="" defaultWidth="100" defaultHeight="100" transform=c.url updateFromServer=false attributes... >
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
		<#local value = (currentModel[key].uri)!default>
		<#if value?has_content>
			<#if transform?is_string>
				<#local src = transform?replace("*", value) />
			<#else>
				<#local src = transform(value) />
			</#if>
			<#if tag == "img">
				<#local attributes = attributes + {"src": src} />
				<#if !attributes.width?has_content && currentModel[key]??>
					<#local attributes = attributes + {"width": currentModel[key].width?c} />
				</#if>
				<#if !attributes.height?has_content && currentModel[key]??>
					<#local attributes = attributes + {"height": currentModel[key].height?c} />
				</#if>
				<#if !attributes.alt?has_content>
					<#local attributes = attributes + {"alt": " "} />
				</#if>
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
				<#local srcTemplate = transform("/*")?replace("/*", "*") />
			</#if>
			<#local attributes = attributes + {
				"class": ("riot-image-editor " + attributes.class!)?trim,
				"riot:editorType": "image",
				"riot:key": key,
				"riot:srcTemplate": srcTemplate,
				"riot:minWidth": minWidth,
				"riot:maxWidth": maxWidth,
				"riot:minHeight": minHeight,
				"riot:maxHeight": maxHeight 
				} />
			<#if updateFromServer>
				<#local attributes = attributes + {
					"riot:updateFromServer": "true"
				} />
			</#if>
		</#if>
		<#if value?has_content || editMode>
			<#if tag == "img">	
				<img${c.joinAttributes(attributes)} />
			<#else>
				<${tag}${c.joinAttributes(attributes)}><#nested /></${tag}>
			</#if>
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
  -
  -->
<#macro use container form="" tag="" autoSizePopup=true attributes...>
	<#local attributes = c.unwrapAttributes(attributes) />
	<#local previousModel = currentModel />
	<#assign currentContainer = container />
	<#assign currentModel = container.unwrap(editMode) />
	<#if editMode>
		<#if !tag?has_content>
			<#local tag = "span" />
		</#if>
		<#local attributes = attributes + {
				"riot:containerId": container.id?c,
				"riot:contentId": container.getContent(editMode).id?c,
				"class": ("riot-container riot-content " + attributes.class!)?trim
		} />
		<#if form?has_content>
			<#local attributes = attributes + {
					"class": attributes.class + " riot-form",
					"riot:form": form,
					"riot:autoSizePopup": autoSizePopup?string	
			} />
		</#if>
		<#if container.dirty>
			<#local attributes = attributes + {
					"class": attributes.class + " riot-dirty"
			} />
		</#if>
	</#if>
	<#if tag?has_content>
		<${tag}${c.joinAttributes(attributes)}>
			<#nested>
		</${tag}>
	<#else>
		<#nested>
	</#if>
	<#assign currentModel = previousModel />
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
