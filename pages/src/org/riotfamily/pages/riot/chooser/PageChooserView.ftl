<html>
<head>
	<title>Sitemap</title>
	<@riot.stylesheet href="tree/tree.css" />
	<@riot.script src="prototype/prototype.js" />
	<@riot.script src="tree/tree.js" />
</head>
<body>
	<form action="?" method="GET">
		<input type="hidden" name="mode" value="${mode?if_exists}" />
		<#if sites?has_content && sites?size &gt; 1>
			<select name="site" onchange="this.form.submit()">
				<#list sites as site>
					<option value="${site.id}"<#if site == selectedSite>selected="selected"</#if>>${site.name}</option>
				</#list>
			</select>
		<#else>
			<input type="hidden" name="site" value="${selectedSite.id}" />
		</#if>
	</form>
	<ul id="tree" class="tree">
		<@renderPages pages />
	</ul>
	<script>
		new Tree('tree', function(href) {
			<#if mode == "tinyMCE">
				var win = window.dialogArguments || opener || parent || top;
				var input = win.tinyMCE.activeEditor.windowManager.getParam('input');
				input.value = '${request.contextPath}' + href;
			<#else>
				opener.WindowCallback.invoke(self, href);
			</#if>
				close();
		});
	</script>
</body>
</html>

<#macro renderPages pages>
	<#list pages as page>
		<li<#if page.expanded> class="expanded"</#if>>
			<a class="<#if page.published>published<#else>unpublished</#if>" href="${page.link}">${page.pathComponent}</a>
			<#if page.childPages?has_content>
				<ul>
					<@renderPages page.childPages />
				</ul>
			</#if>
		</li>
	</#list>
</#macro>