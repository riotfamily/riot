<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>Sitemap</title>
	<@riot.stylesheet href="tree/tree.css" />
	<@riot.script src="prototype/prototype.js" />
	<@riot.script src="tree/tree.js" />
</head>
<body>
	<form action="?" method="get">
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
	<script type="text/javascript">
		new Tree('tree', function(href) {
			<#if mode == "tinyMCE">
				var win = window.dialogArguments || opener || parent || top;
				var input = win.tinymce.EditorManager.activeEditor.windowManager.params['input'];
				input.value = href;
			<#else>
				opener.WindowCallback.invoke(self, href);
			</#if>
				close();
		});
	</script>
</body>
</html>

<#macro renderPages pages>
<#compress>
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
</#compress>
</#macro>