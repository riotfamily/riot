<html>
<head>
	<title>Sitemap</title>
	<@riot.stylesheet href="tree/tree.css" />
	<@riot.script src="prototype/prototype.js" />
	<@riot.script src="tree/tree.js" />
</head>
<body>
	<#if locales?has_content>
		<form action="?" method="GET">
			<select name="locale" onchange="this.form.submit()">
				<#list locales as locale>
					<option value="${locale}"<#if locale == selectedLocale>selected="selected"</#if>>${locale.displayName}</option>
				</#list>
			</select>
		</form>
	</#if>
	<ul id="tree" class="tree">
		<@renderPages pages />
	</ul>
	<script>
		Tree.create('tree', function(a) {
			opener.WindowCallback.invoke(self, this.getAttribute('href'));
			close();
			return false;
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