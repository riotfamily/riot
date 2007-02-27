new Cropper.UI('${element.id}', '${element.imageUrl?if_exists}', <#if element.cropUrl?exists>'${element.cropUrl}'<#else>null</#if>, {
	<#if element.undoUrl?exists>undoUrl: '${element.undoUrl}',</#if>
	minWidth: ${element.minWidth}, maxWidth: ${element.maxWidth}, 
	minHeight: ${element.minHeight}, maxHeight: ${element.maxHeight},
	<#if element.widths?has_content>widths: [<#list element.widths as width>${width}<#if width_has_next>,</#if></#list>],</#if>
	<#if element.heights?has_content>heights: [<#list element.heights as height>${height}<#if height_has_next>,</#if></#list>],</#if>
	cropLabel: '${element.formContext.messageResolver.getMessage('label.imageUpload.crop', 'Crop')}', 
	undoLabel: '${element.formContext.messageResolver.getMessage('label.imageUpload.undo', 'Undo')}'
});