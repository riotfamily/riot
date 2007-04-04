<#import "/spring.ftl" as spring />
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<@riot.stylesheet href="style/common.css" />
	</head>
	<body id="packages">
		<h1><@spring.messageText "label.packages.title", "Version Information" /></h1>
		<#list packages as package>
			<p>
				${package.implementationTitle} <span class="version">${package.implementationVersion?if_exists}</span>
			</p>
		</#list>
	</body>
</html>
