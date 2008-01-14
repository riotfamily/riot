<#import "/spring.ftl" as spring />
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>${title?if_exists}</title>
		<@riot.stylesheet href="style/list.css" />
		<@riot.stylesheet href="style/list-custom.css" />
		<#list customStyleSheets as item>
			<@riot.stylesheet href=item />
		</#list>
		<@riot.script src="prototype/prototype.js" />
		<@riot.script src="scriptaculous/effects.js" />
		<@riot.script src="riot-js/util.js" />
		<@riot.script src="riot-js/pager.js" />
		<@riot.script src="dwr/engine.js" />
		<@riot.script src="dwr/util.js" />
		<@riot.script src="dwr/interface/ListService.js" />
		<@riot.script src="list.js" />
		<@riot.script src="path.js" />
		<@riot.script src="style/tweak.js" />
		<script type="text/javascript" language="JavaScript">
			updatePath('${editorId}', null, '${parentId?if_exists}');
		</script>
	</head>

	<#assign hasExtraColumn = hasCommands || filterForm?exists />

	<body onload="TweakStyle.list()"<#if !hasExtraColumn> class="wide"</#if>>
		<div id="body-wrapper">
		
			<div id="wrapper">
				<div class="main">
					<div id="list"></div>
				</div>
			</div>
			<div class="extra">
	
				<#if filterForm?exists>
					<div id="filter" class="box">
						<div class="box-title">
							<span class="label"><@spring.messageText "label.list.filter", "Filter" /></span>
						</div>
						<div id="filterForm">
						</div>
					</div>
				</#if>
	
				<#if hasCommands>
					<div id="commands" class="box">
						<div class="box-title">
							<span class="label"><@spring.messageText "label.commands", "Commands" /></span>
						</div>
						<div id="listCommands">
						</div>
					</div>
				</#if>
	
			</div>
			
		</div>		
		
		<script type="text/javascript" language="JavaScript">
			var list = new RiotList('${listKey}');
			list.render('list', 'listCommands'<#if filterForm?exists>, 'filterForm'</#if>);
		</script>
	</body>
</html>