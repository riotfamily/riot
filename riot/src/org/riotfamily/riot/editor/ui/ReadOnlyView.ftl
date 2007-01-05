<#import "/spring.ftl" as spring />
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<#list customStyleSheets as item>
			<link rel="stylesheet" href="${request.contextPath}${resourcePath}/${item}" type="text/css" />
		</#list>
		<link rel="stylesheet" href="${request.contextPath}${resourcePath}/style/form.css" type="text/css" />
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/prototype/prototype.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/path.js"></script>
		
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/riot-js/util.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/dwr/engine.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/dwr/util.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/dwr/interface/ListService.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/list.js"></script>
		
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/style/tweak.js"></script>
		
		<script type="text/javascript" language="JavaScript">
			updatePath('${editorId}', '${objectId?if_exists}', '${parentId?if_exists}');
		</script>
	</head>
	<body>
		<div id="wrapper">
			<div id="form" class="main">
				${form}
			</div>
		</div>
		<div id="extras" class="extra">
			<div id="commands" class="box">
				<div class="title">
					<div class="icon"></div>
					<span><@spring.messageText "label.commands", "Commands" /></span>
				</div>
				<div id="formCommands" class="list"></div>
			</div>
			
			<script type="text/javascript" language="JavaScript">
				TweakStyle.form();
				var list = new RiotList('${listKey}');
				list.renderFormCommands('${objectId}', 'formCommands');
			</script>
		</div>
	</body>
</html>
