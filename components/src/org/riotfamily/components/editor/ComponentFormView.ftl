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
		<@riot.stylesheet href="style/component-form.css" />
		<@riot.stylesheet href="style/component-form-custom.css" />
		<@riot.script src="prototype/prototype.js" />
		<@riot.script src="style/tweak.js" />
		<@riot.script src="riot-js/viewport.js" />
		<style type="text/css">
			html, body {
				border: none;
			}
			.buttons input.button-save {
				display: none;
			}
		</style>
		<script>
			function save() {
				$$('input.button-save').first().click();
			}

			if (parent && parent.riot) {
				Event.observe(window, 'load', function() {
					var p = parent.riot.popup;
					if (parent.riot.activePopup != p) {
						var h = Math.min(
								Math.round(Viewport.getInnerHeight(parent) * 0.8),
								Viewport.getBodyHeight() + 32);

						p.content.style.height = h + 'px';

						var w = Math.max(600, Viewport.getBodyWidth() + 32);
						p.div.style.width = w + 'px';

						p.open();
					}
				});
			}
		</script>
	</head>
	<body>
		${form}
	</body>
</html>