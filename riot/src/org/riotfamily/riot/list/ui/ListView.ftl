<#import "/spring.ftl" as spring />
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<link rel="stylesheet" href="${request.contextPath}${resourcePath}/style/list.css" type="text/css" />
		<#list customStyleSheets as item>
			<link rel="stylesheet" href="${request.contextPath}${resourcePath}/${item}" type="text/css" />
		</#list>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/prototype/prototype.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/riot-js/util.js"></script>
		
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/dwr/engine.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/dwr/util.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/dwr/interface/ListService.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/list.js"></script>
		
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/path.js"></script>		
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/style/tweak.js"></script>		
		<script type="text/javascript" language="JavaScript">
			updatePath('${editorId}', null, '${parentId?if_exists}');
		</script>		
	</head>
	
	<#assign hasExtraColumn = commands?has_content || search?exists || filterForm?exists />
	
	<body onload="TweakStyle.list()"<#if !hasExtraColumn> class="wide"</#if>>
		<div id="wrapper">
			<div class="main">
				<div id="list"></div>
			</div>		
		</div>
		<div class="extra">
			<#if commands?has_content>
				<div id="commands" class="box">
					<div class="title">
						<div class="icon"></div>
						<span><@spring.messageText "label.commands", "Commands" /></span>
					</div>
					<div id="listCommands" class="list">
					</div>
				</div>
			</#if>

			<#--			
			<#if list.search?exists>
				<div id="search" class="box">
					<div class="title">
						<div class="icon"></div>
						<span><@spring.messageText "label.search", "Search" /></span>
					</div>
					<div class="list">
						<form method="post">
							<input type="text" name="search" value="" />
						</form>
					</div>
				</div>
			</#if>
			-->
			
			<#if filterForm?exists>
				<div id="filter" class="box">
					<div class="title">
						<div class="icon"></div>
						<span><@spring.messageText "label.list.filter", "Filter" /></span>
					</div>
					${filterForm}
				</div>
			</#if>
		</div>
		<script type="text/javascript" language="JavaScript">
			var list = new RiotList('${listKey}');
			list.render('list', 'listCommands');
		</script>
	</body>
</html>