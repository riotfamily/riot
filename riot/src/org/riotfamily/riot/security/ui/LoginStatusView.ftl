<#import "/spring.ftl" as spring />
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<@riot.stylesheet href="style/status.css" />
		<@riot.script src="prototype/prototype.js" />
		<@riot.script src="style/tweak.js" />
	</head>
	<body onload="TweakStyle.status()">
		<div id="panel">
			<div id="status">
				<div id="loading"><@spring.messageText "label.status.busy", "Loading ..." /></div>
				<b><a href="${riot.href("/logout")}"><@spring.messageText "label.status.logout", "Logout" /></a></b>
				<b><a href="${riot.href("/changePassword")}" target="editor"><@spring.messageText "label.status.changePassword", "Change Password" /></a></b>
				<span class="label"><@spring.messageText "label.status.username", "User" />:</span> <span class="value">${sessionData.userName?if_exists}</span>
				<span class="label"><@spring.messageText "label.status.lastLogin", "Last login" />: </span><span class="value">${sessionData.lastLoginDate?if_exists} [${sessionData.lastLoginIP?if_exists}]</span>
			</div>
		</div>
		<script>
			new PeriodicalExecuter(function() {
				new Ajax.Request('${riot.href("/ping")}');
			}, 180);
		</script>
	</body>
</html>