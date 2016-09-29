<div id="${element.id}" style="padding-top:10px;">
<#if element.svg??>
	<img src="${element.imageUrl}" width="${element.previewWidth?c}" height="${element.previewHeight?c}" />
</#if>
	<div style="font-size:10px;margin-top:10px;">${element.sizeLabel}</div>
</div>