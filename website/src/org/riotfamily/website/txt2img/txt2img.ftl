<#---
  - Macros for text-to-image replacements.
  - @namespace txt2img
  -->
  
<#---
  - Writes a script tag that loads the txt2img.js file.
  -->
<#macro init>
	<script type="text/javascript" src="${c.resource(c.pathForHandler("txt2ImgController", "js") + '?locale=' + .locale)?xml}"></script>
</#macro>

<#---
  - Returns the URL of a style-sheet that hides all texts which will be 
  - replaced to avoid flickering during page load. As this would render texts
  - invisible if the browser does not support JavaScript the body tag must
  - have a <em>noscript</em> class. 
  -->
<#function styleSheet>
	<#return c.pathForHandler("txt2ImgController", "css") />
</#function>

<#---
  - Renders an image-button. 
  -->
<#macro button style tag="button" attributes...>
	<#local label><#nested /></#local>
	<#local attributes = attributes + {"class": style + " " + attributes.class!} />
	<${tag} style="${txt2ImgMacroHelper.getButtonStyle(style, label)}" ${c.joinAttributes(attributes)}>${label?trim}</${tag}>
</#macro>

<#---
  - Returns the URL of a style-sheet that contains common styles for all
  - image-buttons.
  -->
<#function buttonStyleSheet>
	<#return c.pathForHandler('txt2imgButtonController') />
</#function>