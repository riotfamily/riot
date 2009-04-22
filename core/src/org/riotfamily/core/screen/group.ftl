<@template.set stylesheets=[
	"style/group.css", 
	"style/group-custom.css"
] />
<@template.extend file="screen.ftl">
	<@template.block name="content" cache=false>
		<div class="main group">
			<div class="screens">
				<#list context.screen.childScreens as child>
					<a class="screen" href="${c.urlForHandler(child.id, context)}">
						<div class="label"><span class="icon" style="background-image:url(${c.resolve(riot.resource("style/images/icons/"+(child.icon!"brick")+".png"))})">${child.id}</span></div>
						<div class="description"></div>
					</a>
				</#list>
			</div>
		</div>
		<div id="extra" class="extra">
			<div class="box">
				Welcome <strong>admin</strong>.
				Last login: 4.4.2009 at 14:30 from IP 127.0.0.1
			</div>
		</div>
	</@template.block>
</@template.extend>