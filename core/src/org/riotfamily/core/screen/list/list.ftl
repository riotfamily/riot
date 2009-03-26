<@template.set stylesheets=[
	"style/list.css", 
	"style/list-custom.css",
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
					<div id="list"></div>
				</div>
			</div>
			<div id="extras" class="extra">
				<#if filterForm??>
					<div id="filter" class="box">
						<div class="box-title">
							<span class="label"><@c.message "label.list.filter">Filter</@c.message></span>
						</div>
						<div id="filterForm">
						</div>
					</div>
				</#if>
				<div class="box command-box">
					<div class="box-title">
						<span class="label"><@c.message "label.itemCommands">Item Commands</@c.message></span>
					</div>
					<div id="itemCommands" class="commands">
					</div>
				</div>
				<div class="box command-box">
					<div class="box-title">
						<span class="label"><@c.message "label.listCommands">List Commands</@c.message></span>
					</div>
					<div id="listCommands" class="commands">
					</div>
				</div>
			</div>
		</div>		
		
		<script type="text/javascript" language="JavaScript">
			var list = new RiotList('${listState.key}');
			list.render('list', 'listCommands', 'itemCommands', <#if expand??>'${expand}'<#else>null</#if><#if listState. filterForm??>, 'filterForm'</#if>);
		</script>
		
	</@template.block>
</@template.extend>