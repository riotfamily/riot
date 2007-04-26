<#import "/spring.ftl" as spring />
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<@riot.stylesheet href="style/group.css" />
		<@riot.script src="path.js" />
		<@riot.script src="prototype/prototype.js" />
		<@riot.script src="scriptaculous/effects.js" />
		<@riot.script src="effects.js" />
		<@riot.script src="style/tweak.js" />
		<@riot.script src="riot-js/util.js" />
		<script type="text/javascript" language="JavaScript">
			updatePath('${group.id}');
		</script>
	</head>
	<body>
		<div id="wrapper">
			<div id="editors" class="main">
				<div class="title"><span class="label">${group.title}</span></div>
				<#list group.editors as ref>
					<a class="editor ${ref.styleClass?default('default')}" href="${common.url(ref.editorUrl)}" <#if ref.targetWindow?exists> target="${ref.targetWindow}"</#if>>
						<div class="icon"<#if ref.icon?exists> style="background-image:url(${riot.resource("style/icons/editors/" + ref.icon + ".gif")})"</#if>></div>
						<div class="text">
							<div class="label">${ref.label}</div>
							<div class="description">${ref.description?if_exists}</div>
						</div>
					</a>
				</#list>
			</div>
		</div>
		<div class="extra">
			<@riot.controller "/notifications" />
			<@riot.controller "/status" />
		</div>
		<script>TweakStyle.group();</script>
	</body>
</html>