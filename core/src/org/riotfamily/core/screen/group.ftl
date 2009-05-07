<@template.set stylesheets=[
	"style/group.css", 
	"style/group-custom.css"
] />
<@template.extend file="screen.ftl">
	<@template.block name="main">
		<ul class="screens">
			<#list links as link>
				<li class="screen">
					<#assign url = c.resolve(riot.resource("style/images/icons/"+(link.icon!"brick")+".png")) />
					<a href="${c.url(link.url)}" class="label" style="background-image:url(${url});_background-image:none;_filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='${url}', sizingMethod='crop');">${link.title}</a>
					<#if link.childLinks??>
						<span class="nested">
							<#list link.childLinks as child>
								<#assign url = c.resolve(riot.resource("style/images/icons/"+(child.icon!"brick")+".png")) />
								<a class="nested-screen" href="${c.url(child.url)}"	style="background-image:url(${url});_background-image:none;_filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='${url}', sizingMethod='crop');">
									${child.title}
								</a>
							</#list>
						</span>
					</#if>
				</li>
			</#list>
		</ul>
	</@template.block>
</@template.extend>