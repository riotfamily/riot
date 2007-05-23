<html>
<head>
	<title>Sitemap</title>
	<@riot.stylesheet href="tree/tree.css" />
	<@riot.script src="prototype/prototype.js" />
	<@riot.script src="tree/tree.js" />
	<#if mode == "tinyMCE">
		<@riot.script src="tiny_mce/tiny_mce_popup.js" />
	</#if>
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
			</#if>
			<#if locales?has_content>
				<select name="locale" onchange="this.form.submit()">
					<#list locales as locale>
						<option value="${locale}"<#if locale == selectedLocale>selected="selected"</#if>>${locale.displayName}</option>
					</#list>
				</select>
			</#if>
		</form>
	<ul id="tree" class="tree">
		<@renderPages pages />
	</ul>
	<script>
		Tree.create('tree', function(a) {
			<#if mode == "tinyMCE">
				tinyMCE.getWindowArg('window').document.getElementById(tinyMCE.getWindowArg('input')).value = this.getAttribute('href');
				tinyMCEPopup.close();
			<#else>
				opener.WindowCallback.invoke(self, this.getAttribute('href'));
				close();
				return false;
			</#if>
		});
	</script>
</body>
</html>

<#macro renderPages pages>
	<#list pages as page>
		<li>
			<a class="<#if page.published>published<#else>unpublished</#if>" href="${page.link}">${page.pathComponent}</a>
			<#if page.childPages?has_content>
				<ul>
					<@renderPages page.childPages />
				</ul>
			</#if>
		</li>
	</#list>
</#macro>