<#import "/spring.ftl" as spring />
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN"
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title><@spring.messageText "title.riot", "Riot V6" /></title>
		<link rel="icon" href="${riot.resource("/style/images/favicon.ico")}" type="image/x-icon" />
    	<link rel="shortcut icon" href="${riot.resource("/style/images/favicon.ico")}" type="image/x-icon" />
    	<@riot.stylesheet href="style/frameset.css" />
    	<@riot.script src="prototype/prototype.js" />
    	<@riot.script src="riot-js/viewport.js" />
    	<@riot.script src="frameset.js" />
	</head>
	<frameset id="rows" rows="119,*,37" border="0">
		<frame name="path" src="${riot.href("/path")}" />
		<frame name="editor" src="${riot.href("/group")}" />
		<frame name="statusbar" src="${riot.href("/statusbar")}" />
	</frameset>
</html>
