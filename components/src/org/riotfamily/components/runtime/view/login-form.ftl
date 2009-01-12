<#import "/spring.ftl" as spring />
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title><@spring.messageText "title.login", "Riot Login" /></title>
		<script type="text/javascript" language="JavaScript">
			if (window != top) {
				top.location.replace(window.location.href);
			}
			function riotBookmarletUsage() {
				alert("${springMacroRequestContext.getMessage("bookmarklet.usage", "To use this bookmarket, add it to your browser's bookmark toolbar, goto the page you want to edit and click on the Riot-Login button.")?js_string}");
			}
		</script>
		<@riot.stylesheet href="style/login.css" />
		<@riot.stylesheet href="style/login-custom.css" />
		<link rel="icon" href="${riot.resource("/style/images/favicon.ico")}" type="image/x-icon" />
    	<link rel="shortcut icon" href="${riot.resource("/style/images/favicon.ico")}" type="image/x-icon" />
    	<@riot.script src="prototype/prototype.js" />
	</head>
	<body onload="$('username').focus()">
		<div id="login">
			<div id="logo"></div>
			<div id="form">
				<form method="post" action="${request.getRequestURI()}">
					<#if username?exists>
						<div class="error">
							<@spring.messageText "error.login.invalidCredentials", "Invalid username or password" />
						</div>
					</#if>
					<div class="username">
						<label for="username"><@spring.messageText "label.login.username", "Username" /></label>
						<input id="username" type="text" name="riot-username" value="${username!?html}" />
					</div>
					<div class="password">
						<label for="password"><@spring.messageText "label.login.password", "Password" /></label>
						<input id="password" type="password" name="riot-password" />
					</div>
					<div class="button">
						<input id="submit" type="submit" class="button-login" value="${springMacroRequestContext.getMessage("label.login.submit", "Login")?html}" />
					</div>
				</form>
			</div>
			<div id="tip">
				<p>
					<#assign alreadyLoggedIn = springMacroRequestContext.getMessage("bookmarklet.alreadyLoggedIn", "You are already logged in.")?js_string />
					<#assign noToolbarSupport = springMacroRequestContext.getMessage("bookmarklet.noToolbarSupport", "Sorry, this page has no Riot Toolbar support.")?js_string />
					<#assign bookmarklet = "javascript:(function(){if(window.riotPagesUrl){window.open(riotPagesUrl+'/login','riotLogin','width=760,height=400,dependent=yes,toolbar=no,location=no,menubar=no,status=no,scrollbars=yes,resizable=yes').focus();}else{if(window.riot){alert('" + alreadyLoggedIn + "');}else{if(typeof riotBookmarletUsage=='function'){riotBookmarletUsage();}else{alert('" + noToolbarSupport + "');}}}})()" />
					
					<#assign installation = 'To open a login window from any page, drag this <a href="{0}">Riot-Login</a> link to your browser\'s bookmark toolbar.' />
					${springMacroRequestContext.getMessage('bookmarklet.installation', [bookmarklet], installation)}
				</p>
			</div>
		</div>
	</body>
</html>
