<@template.root>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Riot | ${context.title}</title>
	<@riot.scripts srcs=[
		"prototype/prototype.js",
		"scriptaculous/effects.js",
		"riot-js/resources.js", 
		"riot-js/window/dialog.js",
		"riot-js/notification/notification.js"] 
	/>
	<@riot.stylesheets hrefs=[
		"riot-js/window/dialog.css",
		"riot-js/notification/notification.css",
		"style/common.css", "style/logo.css"
		] + (customStyleSheets![]) + (template.vars.stylesheets![])
	/>
	
	<script language="JavaScript" type="text/javascript">
		Resources.basePath='${c.url(runtime.resourcePath)}';
	</script>
	
</head>
<body>
	<@template.block name="header">
		<@renderPath path!context.path />	
	</@template.block>
	<div id="content">
		<@template.block name="content">
		</@template.block>
	</div>
</body>
</html>
</@template.root>

<#macro renderPath path>
	<div id="path">
		<#list path as link>
			<#if link_has_next>
				<b><a class="screen" href="${c.url(link.url)}">${link.title}</a></b>
			<#else>
				<b class="active"><span class="screen">${link.title}</span></b>
			</#if>
		</#list>
	</div>
</#macro>