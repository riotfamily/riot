<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<link rel="stylesheet" href="${request.contextPath}${resourcePath}/style/form.css" type="text/css" />
		<link rel="stylesheet" href="${request.contextPath}${resourcePath}/style/component-form.css" type="text/css" />
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/prototype/prototype.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/style/tweak.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/riot-js/viewport.js"></script>
		<style type="text/css">
			input.button-save {
				display: none;
			}
		</style>
		<script>
			function save() {
				tags = document.getElementsByTagName('input');
				for (var i = 0; i < tags.length; i++) {
					if (Element.hasClassName(tags[i], 'button-save')) {
						tags[i].click();
						return;
					}
				}
			}
			
			Event.observe(window, 'load', function() {
				var p = parent.riot.popup;
				if (!p.isOpen) {
					p.content.style.height = document.body.offsetHeight + 'px';
					p.open();
				}
			});
			
		</script>
	</head>
	<body>
		${form}
	</body>
</html>