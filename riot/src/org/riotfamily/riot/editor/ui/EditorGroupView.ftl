<#import "/spring.ftl" as spring />
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<link rel="stylesheet" href="${request.contextPath}${resourcePath}/style/group.css" type="text/css" />
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/path.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/prototype/prototype.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/scriptaculous/effects.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/effects.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/style/tweak.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/riot-js/util.js"></script>
		<script type="text/javascript" language="JavaScript">
			updatePath('${group.id}');
		</script>		
	</head>
	<body onload="TweakStyle.group()">
		<div id="wrapper">
			<div id="editors" class="main">
				<div class="title">${group.title}</div>
				<#list group.editors as ref>
					<a class="editor ${ref.styleClass?default('default')}" href="${url(ref.editorUrl)}" <#if ref.targetWindow?exists> target="${ref.targetWindow}"</#if>>
						<#if ref.icon?exists>
							<div class="icon" style="background-image:url(${request.contextPath}${resourcePath}/style/icons/editors/${ref.icon}.gif)"></div>
						</#if>
						<div class="text">
							<div class="iefix1"><div class="iefix2">
								<div class="label">${ref.label}</div>
								<div class="description">${ref.description?if_exists}</div>
							</div></div>
						</div>
					</a>
				</#list>
			</div>
		</div>
		<div class="extra">
			${include(servletPrefix + '/notifications')}
			${include(servletPrefix + '/status')}
		</div>
	</body>
</html>