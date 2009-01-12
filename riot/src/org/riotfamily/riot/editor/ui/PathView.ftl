<#if request.getHeader("X-Requested-With")?if_exists == "XMLHttpRequest">
<@renderPath />
<#else>
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<@riot.stylesheet href="style/path.css" />
		<@riot.script src="prototype/prototype.js" />
		<@riot.script src="pathView.js" />
		<script type="text/javascript" language="JavaScript">
			path = new Path();
			window.onload = function() {
				if (parent && parent.frameset) {
					parent.frameset.resizeFrame(window);
				}
			};
		</script>
	</head>
	<body class="path" onload="parent.frameset.resizeFrame(window)">
		<table>
			<tbody>
				<tr>
					<td id="path">
						<@renderPath />
					</td>
					<td id="riot">
						<a href="http://www.riotfamily.org" title="Riot V${riotMacroHelper.runtime.versionString}"></a>
					</td>
					<td id="project">
						<a id="logo" href="/"></a>
					</td>
				</tr>
			</tbody>
		</table>
	</body>
</html>
</#if>

<#macro renderPath>
	<#list path.components as comp>
		<#if comp.enabled>
			<b><a href="${riot.url(comp.editorUrl)}" target="editor" class="editor ${comp.editorType}">${comp.label?default('[untitled]')}</a></b>
		<#else>
			<b<#if !comp_has_next> class="active"</#if>><span class="editor ${comp.editorType}">${comp.label?default('[untitled]')}</span></b>
		</#if>
	</#list>
</#macro>
