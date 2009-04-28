<@template.set stylesheets=[
	"style/group.css", 
	"style/group-custom.css"
] />
<@template.extend file="screen.ftl">
	<@template.block name="content" cache=false>
		<div class="main group">
			<div class="screens">
				<#list links as link>
					<a class="screen" href="${c.url(link.url)}">
						<#assign url = c.resolve(riot.resource("style/images/icons/"+(link.icon!"brick")+".png")) />
						<div class="label" style="background-image:url(${url});_background-image:none;_filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='${url}', sizingMethod='crop');">${link.title}</div>
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