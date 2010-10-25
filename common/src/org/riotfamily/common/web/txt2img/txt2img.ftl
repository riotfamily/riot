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
	<#local attributes = c.unwrapAttributes(attributes) />
	<#local attributes = attributes + { "class": ((attributes.class!) + " txt2imgbtn " + txt2ImgMacroHelper.getButtonClass(style))?trim } />
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
  - <b>Note:</b> The code requires prototype.js to be loaded. 
  -->
<#macro insertButtonHoverHack>
	<script type="text/javascript">
		if (Prototype.Browser.IE && typeof document.documentElement.style.maxHeight == 'undefined') {
			document.observe('dom:loaded', function() {
				$$('.txt2imgbtn').each(function(el) {
					if (el.nodeName != 'A') {
						el.observe('mouseover', function() {
							this._txt2imgClass = this.className;
							this.addClassName($w(this.className).map(function(s) { return s + 'Hover'}).join(' '));
						});
						el.observe('mouseout', function() {
							this.className = this._txt2imgClass;
						});
					}
					
					if (el.hasClassName('txt2imgbtn-alpha')) {
						var filter = el.style.backgroundImage.replace('url(','').replace(')','');
						el.setStyle({ backgroundImage : "none", overflow: 'hidden'}).update(
							new Element('span').addClassName('txt2imgbtn-ie6').setStyle({
								display: 'block',
								height: '1%',
								filter: "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"
							 		+ filter + "',sizingMethod='image')"
							})
						);
					}
				});
			});
		}
	</script>
</#macro>
