<#import "/spring.ftl" as spring />
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<#list customStyleSheets as item>
			<@riot.stylesheet href=item />
		</#list>
		<@riot.stylesheet href="style/form-commands.css" />
		<@riot.script src="dwr/engine.js" />
		<@riot.script src="dwr/util.js" />
		<@riot.script src="dwr/interface/ListService.js" />
		<@riot.script src="prototype/prototype.js" />
		<@riot.script src="riot-js/util.js" />
		<@riot.script src="list.js" />
		<@riot.script src="style/tweak.js" />
	</head>
	<body class="extra">
		<#if childLists?has_content>
			<div id="childLists" class="box">
				<div class="title">
					<span class="label"><@spring.messageText "label.childLists", "Sub-Elements" /></span>
				</div>
				<div class="list">
					<#list childLists as item>
						<div class="item">
							<#if item.enabled>
								<a href="${common.url(item.editorUrl)}" target="_parent">${item.label}</a>
							<#else>
								${item.label}
							</#if>
						</div>
					</#list>
				</div>
			</div>
		</#if>

		<div id="commands" class="box">
			<div class="title">
				<span class="label"><@spring.messageText "label.commands", "Commands" /></span>
			</div>
			<div id="formCommands" class="list">
				<div class="item"><a class="saveButton action" href="javascript:parent.frames.form.save()"><span class="label"><@spring.messageText "label.form.button.save", "Save" /></span></a></div>
			</div>
		</div>

		<script type="text/javascript" language="JavaScript">
			TweakStyle.formCommands();
			var list = new RiotList('${listKey}');
			list.renderFormCommands(<#if objectId?exists>'${objectId}'<#else>null</#if>, 'formCommands');
		</script>

	</body>
</html>
