<#import "/spring.ftl" as spring />
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<@riot.stylesheet href="style/error.css" />
		<@riot.script src="prototype/prototype.js" />
		<script>
			function showErrorDetail() {
				$('stacktrace').style.display = 'block';
			}
		</script>
	</head>
	<body id="error">
		<div id="body-wrapper">
			<div id="wrapper">	
				<h1><@spring.messageText "label.error.title", "An error has occurred" /></h1>
				<p id="message">
					${exception.getMessage()?if_exists}
				</p>
				<h2><a class="detail" href="javascript:showErrorDetail()"><@spring.messageText "label.error.detail", "Error Detail" /></a></h2>
				<p id="stacktrace">
				<#list rootCause.stackTrace as element>
					<span class="className">${element.className}</span>.<span class="methodName">${element.methodName}</span><#if element.fileName?exists> <span class="source">(<span class="fileName">${element.fileName}</span>:<span class="lineNumber">${element.lineNumber}</span>)</span></#if><br />
				</#list>
				</p>
			</div>
		</div>
	</body>
</html>
