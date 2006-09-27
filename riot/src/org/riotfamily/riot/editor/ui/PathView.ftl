<#if request.getHeader("X-Requested-With")?if_exists == "XMLHttpRequest">
<@renderPath />
<#else>
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<link rel="stylesheet" href="${request.contextPath}${resourcePath}/style/path.css" type="text/css" />
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/prototype/prototype.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/pathView.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/style/tweak.js"></script>
		<script type="text/javascript" language="JavaScript">
			path = new Path();
		</script>
	</head>
	<body onload="TweakStyle.path()">
		<div id="logo" title="Riot V${riotVersion}"></div>
		<div id="path"><@renderPath /></div>
	</body>
</html>
</#if>

<#macro renderPath>
	<#list path.components as comp>
		<#if comp.enabled>
			<a href="${url(comp.editorUrl)}" target="editor" class="${comp.editorType}">${comp.label?default('[untitled]')}<#if comp.editorType == "list">:</#if></a>
		<#else>
			<span class="${comp.editorType} active ${comp.editorType}-active">${comp.label?default('[untitled]')}</span>
		</#if>
	</#list>
</#macro>
