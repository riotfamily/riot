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
		<@riot.stylesheet href="style/form.css" />
		<@riot.stylesheet href="style/form-custom.css" />
		<@riot.script src="prototype/prototype.js" />
		<@riot.script src="path.js" />
		<@riot.script src="dwr/engine.js" />
		<@riot.script src="dwr/util.js" />
		<@riot.script src="dwr/interface/ListService.js" />
		<@riot.script src="style/tweak.js" />
		<script type="text/javascript" language="JavaScript">
			updatePath('${editorId}', '${objectId?if_exists}', '${parentId?if_exists}');
			function save() {
				$$('input.button-save').first().click();
			}
		</script>
	</head>
	<body>
		<div id="form">
			<#if saved>
				<div id="message">
					<@spring.messageText "label.form.saved", "The data has been saved." />
				</div>
			</#if>
			<#if options?has_content>
				<div id="form-options">
					<@spring.messageText "label.formChooser.choose", "Please select the kind of object you would like to create." />
					<form method="get">
						<#if parentId?exists><input type="hidden" name="parentId" value="${parentId}" /></#if>
						<select name="form" onchange="this.form.submit()">
						<#list options as option>
							<option value="${option.value}"<#if option.value == formId> selected="selected"</#if>>${option.label}</option>
						</#list>
						</select>
					</form>
				</div>
			</#if>
			${form}
		</div>
		<div id="saving" style="display:none">
			<@spring.messageText "label.form.saving", "Saving ..." />
		</div>

		<script type="text/javascript" language="JavaScript">
			TweakStyle.form();
			$$('form[id]').first().observe('submit', function() {
				Element.hide('form');
				Element.show('saving');
			});
		</script>

	</body>
</html>
