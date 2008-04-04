<#if request.getHeader("X-Requested-With")?if_exists == "XMLHttpRequest">
<@renderPath />
<#else>
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<@riot.stylesheet href="style/path.css" />
		<@riot.script src="prototype/prototype.js" />
		<@riot.script src="pathView.js" />
		<@riot.script src="style/tweak.js" />
		<script type="text/javascript" language="JavaScript">
			path = new Path();
		</script>
	</head>
	<body onload="TweakStyle.path()" class="path">
		<div id="body-wrapper">
			<div id="logo" title="Riot V${riotMacroHelper.runtime.versionString}"></div>
			<div id="pathWrapper"><div id="path"><@renderPath /></div></div>
		</div>
	</body>
</html>
</#if>

<#macro renderPath>
	<#list path.components as comp>
		<#if comp.enabled>
			<a href="${riot.url(comp.editorUrl)}" target="editor" class="${comp.editorType}">${comp.label?default('[untitled]')}<#if comp.editorType == "list">:</#if></a>
		<#else>
			<span class="${comp.editorType} active ${comp.editorType}-active">${comp.label?default('[untitled]')}</span>
		</#if>
	</#list>
</#macro>
