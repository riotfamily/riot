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
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/readOnlyView.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/style/tweak.js"></script>
		<script type="text/javascript" language="JavaScript">
			updatePath('${editorId}', '${objectId?if_exists}', '${parentId?if_exists}');
			
			function initCommands() {								
				var container = $('commands');
				var cmds = document.getElementsByClassName('command-.*', container);
				for (var i = 0; i < cmds.length; i++) {
					var e = cmds[i];
					if (!e.command)	e.command = getCommand(e);
					e.onclick = executeCommand;
				}
			}
			
		</script>
	</head>
	<body onload="TweakStyle.form()">
		<table id="columns">
			<tbody>
			<tr>
				<td id="form">
					<#if options?has_content>
						<div id="form-options">
							<@spring.messageText "label.formChooser.choose", "Please select the kind of object you would like to create." />
							<form method="get">
								<#if parentId?exists><input type="hidden" name="parentId" value="${parentId}" /></#if>
								<#if objectId?exists><input type="hidden" name="objectId" value="${objectId}" /></#if>
								<select name="form" onchange="this.form.submit()">
								<#list options as option>
									<option value="${option.value}"<#if option.value == formId> selected="selected"</#if>>${option.label}</option>
								</#list>
								</select>
							</form>
						</div>
					</#if>
					${form}
				</td>
				<td id="sidebar">
					<#if childLists?has_content>
					<div id="childLists" class="box">
						<div class="title">
							<div class="icon"></div>
							<span><@spring.messageText "label.sidebar.childLists", "Sub-Elements" /></span>
						</div>
						<div class="list">
							<#list childLists as item>
								<div class="item">
									<#if item.enabled>
										<a href="${url(item.editorUrl)}">${item.label}</a>
									<#else>
										${item.label}
									</#if>
								</div>
							</#list>
						</div>
					</div>
					</#if>
					
					<#if commands?has_content>
					<div id="commands" class="box">
						<div class="title">
							<div class="icon"></div>
							<span><@spring.messageText "label.sidebar.commands", "Actions" /></span>
						</div>
						<div class="list">							
							<#list commands as command>
								<div class="item">${command}</div>
							</#list>
						</div>
					</div>
					<script type="text/javascript" language="JavaScript">
						initCommands();				
					</script>
					</#if>				
				</td>
			</tr>
			</tbody>
		</table>
	</body>
</html>
