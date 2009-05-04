<@template.set stylesheets=[
	"style/group.css", 
	"style/group-custom.css"
] />
<@template.extend file="screen.ftl">
	<@template.block name="main">
		<div class="screens">
			<#list links as link>
				<a class="screen" href="${c.url(link.url)}">
					<#assign url = c.resolve(riot.resource("style/images/icons/"+(link.icon!"brick")+".png")) />
					<div class="label" style="background-image:url(${url});_background-image:none;_filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='${url}', sizingMethod='crop');">${link.title}</div>
					<div class="description"></div>
				</a>
			</#list>
		</div>
	</@template.block>
</@template.extend>