<@template.set stylesheets=[
	"style/list.css", 
	"style/list-custom.css",
	"style/form.css", 
	"style/form-custom.css",
	"style/command.css", 
	"style/command-custom.css"
] />
<@template.extend file="../screen.ftl">
	
	<@template.block name="main">
		<@riot.script src="/engine.js" />
		<@riot.script src="/util.js" />
		<@riot.script src="/interface/ListService.js" />
		<@riot.script src="list.js" />
		<#if chooser??>
			<@template.set bodyClass="chooser" />
		</#if>
		<div id="list"></div>
	</@template.block>

	<@template.block name="extra">	
		<div class="box command-box">
			<div class="box-title">
				<span class="label"><@c.message "label.commands">Commands</@c.message></span>
			</div>
			<div id="commands" class="commands">
			</div>
		</div>

		<#if listState.filterForm??>
			<div id="filter" class="box">
				<div class="box-title">
					<span class="label"><@c.message "label.list.filter">Filter</@c.message></span>
				</div>
				<div id="filterForm">
				</div>
			</div>
		</#if>
				
		<script type="text/javascript" language="JavaScript">
			riot.list.render({
			     key: '${listState.key}',
			     target: '#list', 
			     commandTarget: '#commands', 
			     expandedId: <#if context.objectId??>'${context.objectId}'<#else>null</#if>
			});
		</script>
	</@template.block>

</@template.extend>