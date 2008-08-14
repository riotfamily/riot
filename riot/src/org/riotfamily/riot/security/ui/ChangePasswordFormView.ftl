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
		<@riot.script src="style/tweak.js" />		
		<script type="text/javascript" language="JavaScript">
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
		</script>
	</head>
	<body>
		<div id="body-wrapper">

			<div id="wrapper">
				<div id="form" class="main">										
					${form}
				</div>
				<script type="text/javascript" language="JavaScript">
					TweakStyle.form();					
					$('form').observe('submit', hideFormForSaving);
				</script>
				<div id="saving" style="display:none">
					<@spring.messageText "label.form.saving", "Saving ..." />
				</div>
			</div>
						
	</body>
</html>
