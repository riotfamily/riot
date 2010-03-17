<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<@riot.stylesheet href="style/form.css" />
</head>
<body>
	<form action="${request.requestURI}" method="post">
		<div class="title">
			<label for="code">Code</label>
		</div>
		<div class="element">
			<input type="text" class="text" id="code" name="code" readonly="readonly" value="${code}" /> 
		</div>
		<div class="title">
			<label for="message-text">Message</label>
		</div>
		<div class="element">
			<textarea class="text" id="message-text" name="message-text"></textarea>
		</div>
		<input type="submit" value="Save" />
	</form>
</body>
</html>