<#import "/spring.ftl" as spring />
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title><@spring.messageText "title.login", "RiotV6 :: Login" /></title>
		<script type="text/javascript" language="JavaScript">
			if (window != top) {
				top.location.replace(window.location.href);
			}
		</script>
		<link rel="stylesheet" href="${request.contextPath}${resourcePath}/style/login.css" type="text/css" />
		<link rel="icon" href="${request.contextPath}${resourcePath}/style/images/favicon.ico" type="image/x-icon" />
    	<link rel="shortcut icon" href="${request.contextPath}${resourcePath}/style/images/favicon.ico" type="image/x-icon" /> 
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/prototype/prototype.js"></script>
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/style/tweak.js"></script>
	</head>
	<body onload="TweakStyle.login();window.focus();$('username').focus()">
		<div id="login">
			<div id="logo"></div>
			<div id="form">
				<form method="post">
					<#if username?exists>
						<div class="error">
							<@spring.messageText "error.login.invalidCredentials", "Invalid username or password" />
						</div>
					</#if>
					<label for="username"><@spring.messageText "label.login.username", "Username" /></label>
					<input id="username" type="text" name="riot-username" value="${username?if_exists}" />
					<label for="password"><@spring.messageText "label.login.password", "Password" /></label>
					<input id="password" type="password" name="riot-password" />
					<input id="submit" type="submit" class="button-login" value="${springMacroRequestContext.getMessage("label.login.submit", "Login")?html}" />
				</form>
			</div>
		</div>
	</body>
</html>
