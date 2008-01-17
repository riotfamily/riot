<#if element.status?exists>
<div id="${element.id}" class="uploadProgress">
	<div class="gauge">
		<div class="bar" style="width:${element.status.getProgressWidth(153,10)}px"></div>
	</div>
	<div class="stats">${element.status.dataTransfered} (${element.status.transferRate})</div>
</div>
<#else>
<input type="file" id="${element.id}" class="file" name="${element.paramName}" 
	<#if element.enabled>onchange="uploadInline(this, '${element.uploadUrl}')"
	<#else>disabled="disabled"
	</#if> />
	<#if element.enabled>
		<noscript>
			<input type="submit" value="Upload" />
		</noscript>
	</#if>	
</#if>
