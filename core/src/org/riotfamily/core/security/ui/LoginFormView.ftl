<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title><@c.message "title.login">Riot Login</@c.message></title>
		
		<@riot.stylesheets ["style/login.css", "style/login-custom.css"] />
		<link rel="icon" href="${riot.resource("style/images/favicon.ico")}" type="image/x-icon" />
    	<link rel="shortcut icon" href="${riot.resource("style/images/favicon.ico")}" type="image/x-icon" />
    	
    	<@riot.script "prototype/prototype.js" />
	</head>
	<body onload="window.focus();$('username').focus()">
		<div id="login">
			<div id="logo"></div>
			<div id="form">
				<form method="post" action="${request.getRequestURI()}">
					<#if username?exists>
						<div class="error">
							<@c.message "error.login.invalidCredentials">Invalid username or password</@c.message>
						</div>
					</#if>
					<div class="username">
						<label for="username"><@c.message "label.login.username">Username</@c.message></label>
						<input id="username" type="text" name="riot-username" value="${username!?html}" />
					</div>
					<div class="password">
						<label for="password"><@c.message "label.login.password">Password</@c.message></label>
						<input id="password" type="password" name="riot-password" />
					</div>
					<div class="button">
						<input id="submit" type="submit" class="button-login" value="${c.getMessageWithDefault("label.login.submit", "Login")?html}" />
					</div>
				</form>
			</div>
		</div>
	</body>
</html>
