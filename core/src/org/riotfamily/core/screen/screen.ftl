<@template.root>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Riot | ${context.title}</title>
	<@riot.scripts srcs=[
		"prototype/prototype.js",
		"riot-js/resources.js", 
		"riot-js/dialog.js"] 
	/>
	<@riot.stylesheets hrefs=[
		"style/common.css", "style/logo.css"
		] + template.vars.stylesheets![]
	/>
	
	<script language="JavaScript" type="text/javascript">
		Resources.basePath='${c.url(runtime.resourcePath)}';
	</script>
	
</head>
<body>
	<@template.block name="header">
		<div id="path">
			<#list context.path as screen>
				<#if screen_has_next>
					<b><a class="screen" href="${c.url(screen.url)}">${screen.title}</a></b>
				<#else>
					<b class="active"><span class="screen">${screen.title}</span></b>
				</#if>
			</#list>
		</div>
	</@template.block>
	<@template.block name="content">
	</@template.block>
</body>
</html>
</@template.root>