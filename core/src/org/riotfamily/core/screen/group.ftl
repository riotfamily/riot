<@template.set stylesheets=[
	"style/group.css", 
	"style/group-custom.css"
] />
<@template.extend file="screen.ftl">
	<@template.block name="main">
		<ul class="screens">
			<#list links as link>
				<li class="screen">
					<a href="${c.url(link.url)}" class="label" style="${riot.iconStyle(link.icon!"brick")}">${link.title!}</a>
					<#if link.childLinks??>
						<span class="nested">
							<#list link.childLinks as child>
								<a class="nested-screen" href="${c.url(child.url)}"	style="${riot.iconStyle(child.icon!"brick")}">
									${child.title!}
								</a>
							</#list>
						</span>
					</#if>
				</li>
			</#list>
		</ul>
	</@template.block>
</@template.extend>