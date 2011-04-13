<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<@riot.script "prototype/prototype.js" />
	<@riot.stylesheets hrefs=[
		"style/common.css", 
		"style/form.css", 
		"style/form-custom.css"
	] />
</head>
<body>
	<div id="body-wrapper">
		<div id="wrapper">
			<div class="main change-password">
				<div id="form">${form}</div>
			</div>
		</div>
	</div>		
</body>
</html>