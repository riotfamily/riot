<@template.set stylesheets=[
	"style/group.css", 
	"style/group-custom.css"
] />
<@template.extend file="screen.ftl">
	<@template.block name="main">
		<ul class="screens">
			<#list links as link>
				<li class="screen">
					<a href="${c.url(link.url)}">
						<span class="icon" style="${riot.iconStyle(link.icon!"brick")}"></span>
						<span class="label">${link.title!}</span>
					</a>
					<#if link.childLinks??>
						<span class="nested">
							<#list link.childLinks as child>
								<a href="${c.url(child.url)}">
									<span class="icon" style="${riot.iconStyle(child.icon!"brick")}"></span>
									<span class="label">${child.title!}</span>
								</a>
							</#list>
						</span>
					</#if>
				</li>
			</#list>
		</ul>
	</@template.block>
</@template.extend>