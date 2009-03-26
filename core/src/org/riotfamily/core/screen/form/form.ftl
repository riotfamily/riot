<@template.set stylesheets=[
	"style/form.css", 
	"style/form-custom.css",
	"style/command.css", 
	"style/command-custom.css"
] />
<@template.extend file="../screen.ftl">
	<@template.block name="content" cache=false>
		<@riot.script src="/engine.js" />
		<@riot.script src="/util.js" />

		<@riot.script src="/interface/ListService.js" />
		<@riot.script src="riot-js/pager.js" />
		<@riot.script src="list.js" />
	
		<div id="body-wrapper">
			<div id="wrapper">
				<div class="main">
					<div id="form">${form}</div>
				</div>
			</div>
			<div id="extras" class="extra">
			</div>
		</div>		
				
	</@template.block>
</@template.extend>