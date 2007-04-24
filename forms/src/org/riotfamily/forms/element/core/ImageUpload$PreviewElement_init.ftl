new Cropper.UI('${element.id}', '${element.imageUrl?if_exists}', <#if element.cropUrl?exists>'${element.cropUrl}'<#else>null</#if>, {
	<#if element.undoUrl?exists>undoUrl: '${element.undoUrl}',</#if>
	<#if element.cropUrl?exists>
	minWidth: ${element.minWidth?c}, maxWidth: ${element.maxWidth?c}, 
	minHeight: ${element.minHeight?c}, maxHeight: ${element.maxHeight?c},
	</#if>
	<#if element.widths?has_content>widths: [<#list element.widths as width>${width?c}<#if width_has_next>,</#if></#list>],</#if>
	<#if element.heights?has_content>heights: [<#list element.heights as height>${height?c}<#if height_has_next>,</#if></#list>],</#if>
	cropLabel: '${element.formContext.messageResolver.getMessage('label.imageUpload.crop', 'Crop')?js_string}', 
	undoLabel: '${element.formContext.messageResolver.getMessage('label.imageUpload.undo', 'Undo')?js_string}'
});