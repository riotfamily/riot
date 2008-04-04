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
		<script type="text/javascript" language="JavaScript">
			path = new Path();
		</script>
	</head>
	<body id="chooser" class="path">
		<div id="body-wrapper">
			<div id="path"><@renderPath /></div>
		</div>
	</body>
</html>
</#if>

<#macro renderPath>
	<#list path.components as comp>
		<#if comp.enabled>
			<a href="${riot.url(comp.editorUrl)}" target="chooserList" class="node">${comp.label?default('[untitled]')}</a>
		<#else>
			<span class="node active node-active">${comp.label?default('[untitled]')}</span>
		</#if>
	</#list>
</#macro>
