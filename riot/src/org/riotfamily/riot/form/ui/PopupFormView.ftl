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
		<style type="text/css">
			html, body {
				border: none;
			}
		</style>
	</head>
	<body>
		${form}
	</body>
</html>