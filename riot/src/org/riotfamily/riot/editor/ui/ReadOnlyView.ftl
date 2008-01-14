<#import "/spring.ftl" as spring />
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<@riot.stylesheet href="style/form.css" />
		<@riot.stylesheet href="style/form-custom.css" />
		<#list customStyleSheets as item>
			<@riot.stylesheet href=item />
		</#list>
		<@riot.script src="prototype/prototype.js" />
		<@riot.script src="path.js" />
		<@riot.script src="riot-js/util.js" />
		<@riot.script src="dwr/engine.js" />
		<@riot.script src="dwr/util.js" />
		<@riot.script src="dwr/interface/ListService.js" />
		<@riot.script src="list.js" />
		<@riot.script src="style/tweak.js" />

		<script type="text/javascript" language="JavaScript">
			updatePath('${editorId}', '${objectId?if_exists}', '${parentId?if_exists}');
		</script>
	</head>
	<body>
		<div id="body-wrapper">
			<div id="wrapper">
				<div id="form" class="main">
					${form}
				</div>
			</div>
			<div id="extras" class="extra">
				<div id="commands" class="box">
					<div class="box-title">
						<span class="label"><@spring.messageText "label.commands", "Commands" /></span>
					</div>
					<div id="formCommands" class="list"></div>
				</div>
	
				<script type="text/javascript" language="JavaScript">
					TweakStyle.form();
					var list = new RiotList('${listKey}');
					list.renderFormCommands('${objectId}', 'formCommands');
				</script>
			</div>
		</div>
	</body>
</html>
