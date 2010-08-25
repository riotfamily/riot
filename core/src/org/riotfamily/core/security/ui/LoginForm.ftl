<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Riot Login</title>
	<@riot.scripts srcs=[
		"prototype/prototype.js",
		"scriptaculous/effects.js",
		"riot/resources.js", 
		"riot/window/dialog.js",
		"riot/notification/notification.js"] 
	/>
	<@riot.stylesheets hrefs=[
		"riot/window/dialog.css",
		"riot/notification/notification.css",
		"style/common.css", "style/login.css"]
	/>
</head>
<body class="login">
	<div id="wrapper">
		<@center>
            <div class="box">
                <form method="post" action="${request.getRequestURI()}">
                    <#if username?exists>
                    <div class="error">
                    	<@c.message "error.login.invalidCredentials">Invalid username or password</@c.message>
                    </div>
                    </#if>
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
            </div>
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
