<html>
<head>
	<script type="text/javascript" src="${request.contextPath}${resourcePath}/prototype/prototype.js"></script>
	<script type="text/javascript" src="${request.contextPath}${resourcePath}/tree/tree.js"></script>
	<link rel="stylesheet" type="text/css" href="${request.contextPath}${resourcePath}/tree/tree.css" />
</head>
<body>
	<ul id="tree" class="tree">
		<@renderItems items />	
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

<#macro renderItems items>
	<#list items as item>
		<li>
			<a href="${item.link}">${item.label}</a>
			<#if item.childItems?has_content>
				<ul>
					<@renderItems item.childItems />
				</ul>
			</#if>
		</li>
	</#list>
</#macro>