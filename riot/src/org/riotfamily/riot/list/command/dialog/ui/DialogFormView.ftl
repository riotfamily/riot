<#import "/spring.ftl" as spring />
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<@riot.stylesheet href="style/form.css" />
		<@riot.stylesheet href="style/form-custom.css" />
		<@riot.script src="prototype/prototype.js" />
		<@riot.script src="path.js" />
		<@riot.script src="style/tweak.js" />
	</head>
	<body>
		<div id="form" class="main">
			${form}
		</div>
		<script>
			subPage('${title}');
			TweakStyle.dialogForm();
		</script>
	</body>
</html>
