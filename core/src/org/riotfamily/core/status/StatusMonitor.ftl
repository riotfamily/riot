<#if status??>
	<div class="box">
		<div class="status" style="${riot.iconStyle(status.icon)}">
			<@c.link href=status.link transform=c.url>${status.message}</@c.link>
		</div>
	</div>
</#if>