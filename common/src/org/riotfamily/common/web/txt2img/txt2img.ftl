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
	<#local attributes = attributes + {"class": (style + " txt2imgbtn " + attributes.class?if_exists)?trim} />
	<${tag} style="${txt2ImgMacroHelper.getButtonStyle(style, label)?html}" ${c.joinAttributes(attributes)}>${label?trim}</${tag}>
</#macro>

<#---
  - Returns the URL of a style-sheet that contains common styles for all
  - image-buttons.
  -->
<#function buttonStyleSheet>
	<#return c.pathForHandler('txt2imgButtonCssController') />
</#function>

<#---
  - Renders an inline JavaScript to support hover states for buttons that are 
  - no &lt;a&gt; elements in IE &lt; 7.
  - <p>
  - The macro outputs a function called <code>addButtonHoverHandler</code>
  - which is automatically invoked when the DOM is ready. The code is wrapped
  - inside a condtional comment so it won't be visible to other browsers.
  - </p>
  - <b>Note:</b> The code requires prototype.js to be loaded. 
  -->
<#macro insertButtonHoverHack>
	<script type="text/javascript">
	/*@cc_on
	/*@if (@_jscript_version < 5.7)
		function addButtonHoverHandler() {
			$$('.txt2imgbtn:not(a)').each(function(el) {
				el.observe('mouseover', function() {
					this._txt2imgClass = this.className; 
					this.className += this.className + 'Hover';
				});
				el.observe('mouseout', function() {
					this.className = this._txt2imgClass;
				});
			});
		}
		document.observe('dom:loaded', addButtonHoverHandler); 
	/*@end
	@*/
	</script>
</#macro>