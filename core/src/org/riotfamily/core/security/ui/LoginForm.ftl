<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Riot Login</title>
	<@riot.scripts srcs=[
		"prototype/prototype.js",
		"scriptaculous/effects.js",
		"riot-js/resources.js", 
		"riot-js/window/dialog.js",
		"riot-js/notification/notification.js"] 
	/>
	<@riot.stylesheets hrefs=[
		"riot-js/window/dialog.css",
		"riot-js/notification/notification.css",
		"style/common.css", "style/logo.css", "style/login.css"]
	/>
</head>
<body class="login">
	<div id="wrapper">
		<@center>
			<@box>
				<#if username?exists>
					<div class="error">
						<@c.message "error.login.invalidCredentials">Invalid username or password</@c.message>
					</div>
				</#if>
				<form method="post" action="${request.getRequestURI()}">
					<table>
						<tr class="username">
							<td class="label">
								<label for="username"><@c.message "label.login.username">Username</@c.message></label>
							</td>
							<td class="input">
								<input id="username" type="text" name="riot-username" value="${username!?html}" />
							</td>
						</tr>
						<tr class="password">
							<td class="label">
								<label for="password"><@c.message "label.login.password">Password</@c.message></label>
							</td>
							<td class="input">
								<input id="password" type="password" name="riot-password" />
							</td>
						</tr>
						<tr class="button">
							<td class="label">
							</td>
							<td class="input">
								<span><input id="submit" type="submit" class="button-login" value="${c.getMessageWithDefault("label.login.submit", "Login")?html}" /></span>							
							</td>
						</tr>
					</table>
				</form>
			</@box>
		</@center>
	</div>
</body>
</html>

<#macro center>
	<table class="center">
		<tbody>
			<tr>
				<td class="center"><#nested  /></td>
			</tr>
		</tbody>
	</table>
</#macro>

<#macro box>
	<table class="box">
		<tbody>
			<tr class="t">
				<td class="l"></td>
				<td class="c"></td>
				<td class="r"></td>
			</tr>
			<tr class="m">
				<td class="l"></td>
				<td class="c"><#nested /></td>
				<td class="r"></td>
			</tr>
			<tr class="b">
				<td class="l"></td>
				<td class="c"></td>
				<td class="r"></td>
			</tr>
		</tbody>
	</table>
</#macro>
