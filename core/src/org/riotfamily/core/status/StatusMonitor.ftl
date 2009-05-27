<#if status??>
	<div class="box">
		<@c.link href=status.link transform=c.url>
			<span class="label" style="${riot.iconStyle(status.icon)}">${status.message}</span>
		</@c.link>
	</div>
</#if>