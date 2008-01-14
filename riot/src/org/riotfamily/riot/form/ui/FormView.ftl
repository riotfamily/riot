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
		<@riot.script src="riot-js/util.js" />
		<@riot.script src="dwr/engine.js" />
		<@riot.script src="dwr/util.js" />
		<@riot.script src="dwr/interface/ListService.js" />
		<@riot.script src="list.js" />
		<@riot.script src="style/tweak.js" />
		<script type="text/javascript" language="JavaScript">
			updatePath('${editorId}', '${objectId?if_exists}', '${parentId?if_exists}');
			function save() {
				$$('input.button-save').first().click();
			}
		</script>
	</head>
	<body>
		<div id="body-wrapper">

			<div id="wrapper">
				<div id="form" class="main">
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
				</div>
			</div>
			<div id="extras" class="extra">
				<div id="commands" class="box">
					<div class="box-title">
						<span class="label"><@spring.messageText "label.commands", "Commands" /></span>
					</div>
					<div id="formCommands" class="list">
						<div class="item"><a class="saveButton action" href="javascript:save()"><span class="label"><@spring.messageText "label.form.button.save", "Save" /></span></a></div>
					</div>
				</div>
	
				<#if childLists?has_content>
					<div id="childLists" class="box">
						<div class="box-title">
							<span class="label"><@spring.messageText "label.childLists", "Sub-Elements" /></span>
						</div>
						<div class="list">
							<#list childLists as item>
								<div class="item">
									<a href="${common.url(item.editorUrl)}">${item.label}</a>
								</div>
							</#list>
						</div>
					</div>
				</#if>
				
				<script type="text/javascript" language="JavaScript">
					TweakStyle.form();
					var list = new RiotList('${listKey}');
					list.renderFormCommands(<#if objectId?exists>'${objectId}'<#else>null</#if>, 'formCommands');
					$$('form[id]').first().observe('submit', function() {
						Element.hide('form');
						Element.hide('extras');
						Element.show('saving');
					});
				</script>
			</div>
	
			<div id="saving" style="display:none">
				<@spring.messageText "label.form.saving", "Saving ..." />
			</div>

		</div>
	</body>
</html>
