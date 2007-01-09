new Cropper.UI('${element.id}', '${element.imageUrl!}', <#if element.cropUrl?exists>'${element.cropUrl}'<#else>null</#if>, {
	<#if element.undoUrl?exists>undoUrl: '${element.undoUrl}',</#if>
	minWidth: ${element.minWidth}, maxWidth: ${element.maxWidth}, 
	minHeight: ${element.minHeight}, maxHeight: ${element.maxHeight} 
});