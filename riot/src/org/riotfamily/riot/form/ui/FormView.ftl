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
			updatePath('${editorId}', '${objectId?if_exists}', '${parentId?if_exists}', '${parentEditorId?if_exists}');
			
			<#--
			  - Hides the form and displays a 'Saving ...' message.
			  - The function is invoked by save() or when the form is submitted
			  - via a submit button.
			  -->
			function hideFormForSaving() {
				Element.hide('form');
				Element.hide('extras');
				Element.show('saving');
			}
			
			<#--
			  - Function invoked by ajaxSubmit(). It performs all actions that 
			  - would be done by 'onsubmit' listeners if the form was submitted
			  - via a regular submit button. Currently this is a dirty hack, 
			  - which is quite okay, as TinyMCE is the only element that 
			  - requires special 'onsubmit'-handling. Once another element 
			  - needs this functionality it will be added to the riot-forms API.
			  -->
			function onSubmit() {
				if (window.tinymce) {
					tinymce.EditorManager.triggerSave();
				}
			}
			
			<#--
			  - Submits the form by clicking on the submit button. 
			  - This function is invoked by the 'Save' button
			  - in the right-hand column.
			  -->
			function save() {
				$('${formId}').down('input.button-save').click();
			}
			
			<#--
			  - Submits the form via AJAX. The function is invoked when Ctrl+S
			  - is pressed.
			  -->
			function ajaxSubmit() {
				onSubmit();
				submitForm('${formId}');
			}
			
			<#--
			  - Registers a keypress listener on the given document. Note: 
			  - this function is also invoked by the setupcontent_callback of
			  - TinyMCE elements.
			  -->
			function registerKeyHandler(doc) {
				Event.observe(doc, 'keydown', function(e) {
				
					//we have to use code for KEY_CTRL(17) explicitly as it is not defined in Event
					if (e.keyCode == 17 || !e.ctrlKey)
						return;
					
					var key = String.fromCharCode(e.charCode ? e.charCode : e.keyCode).toUpperCase();
					if (key == 'S') {
						ajaxSubmit();
						e.stop();
					}
				});
			}
			
			registerKeyHandler(document);
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
				<div class="box command-box">
					<div class="box-title">
						<span class="label"><@spring.messageText "label.commands", "Commands" /></span>
					</div>
					<div class="commands">
						<div id="formCommands"></div>
						<a class="action enabled" href="javascript:save()"><div class="icon saveButton"></div><span class="label"><@spring.messageText "label.form.button.save", "Save" /></span></a>
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
									<a href="${riot.url(item.editorUrl)}">${item.label}</a>
								</div>
							</#list>
						</div>
					</div>
				</#if>
				
				<script type="text/javascript" language="JavaScript">
					TweakStyle.form();
					var list = new RiotList('${listKey}');
					var item = {
						objectId: <#if objectId??>'${objectId}'<#else>null</#if>,
						parentId: <#if parentId??>'${parentId}'<#else>null</#if>,
						parentEditorId: <#if parentEditorId??>'${parentEditorId}'<#else>null</#if>
					};
					list.renderFormCommands(item, 'formCommands');
					$('${formId}').observe('submit', hideFormForSaving);
				</script>
			</div>
	
			<div id="saving" style="display:none">
				<@spring.messageText "label.form.saving", "Saving ..." />
			</div>

		</div>
	</body>
</html>
