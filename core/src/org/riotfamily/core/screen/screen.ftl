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
<body class="${template.vars.bodyClass!"screen"}">
	<div id="page">
		<div id="header">
			<@template.block name="header">
				<@renderPath path!context.path />	
			</@template.block>
			<div id="logo"></div>
		</div>
		<div id="content">
			<@template.block name="content">
				<div class="main">
					<@template.block name="main" />	
				</div>
				<div id="extra" class="extra">
					<@template.block name="extra">
					</@template.block>
					<@renderScreenlets />
				</div>
			</@template.block>
		</div>
	</div>
	<div id="footer">
		<div id="footer-content"></div>
	</div>
	<#if notification??>
		<script language="JavaScript" type="text/javascript">
			riot.notification.show(${c.toJSON(notification)});
		</script>
	</#if>
</body>
</html>
</@template.root>

<#macro renderScreenlets>
	<#if context.screen.screenlets??>
		<div id="screenlets">
			<#list context.screen.screenlets as screenlet>
				${screenlet.render(context)}
			</#list>
		</div>
	</#if>
</#macro>

<#macro renderPath path>
	<div id="path">
		<#list path as link>
			<#if link_has_next>
		 	 	<a href="${c.url(link.url)}" class="screen<#if link_index == 0> first</#if>"><@renderLabel link /></a><#t>
			<#else>
				<span class="screen <#if link_index == 0> first</#if>"><@renderLabel link true /></span><#t>
			</#if>
		</#list>
	</div>
</#macro>

<#macro renderLabel link active=false>
	<b<#if active> class="active"</#if>><b><#if link.icon??><span class="icon" style="${riot.iconStyle(link.icon)}"></span></#if><span class="title<#if link.new> new</#if>">${link.title}</span></b></b><#t>
</#macro>