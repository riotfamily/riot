new Cropper.UI('${element.id}', '${element.imageUrl?if_exists}', <#if element.cropUrl?exists>'${element.cropUrl}'<#else>null</#if>, {
	<#if element.undoUrl?exists>undoUrl: '${element.undoUrl}',</#if>
	<#if element.cropUrl?exists>
	minWidth: ${element.minWidth?c}, maxWidth: ${element.maxWidth?c}, 
	minHeight: ${element.minHeight?c}, maxHeight: ${element.maxHeight?c},
	<#else>
	minWidthLabel: ${element.minWidth?c}, maxWidthLabel: ${element.maxWidth?c}, 
	minHeightLabel: ${element.minHeight?c}, maxHeightLabel: ${element.maxHeight?c},
	</#if>
	previewWidth: ${element.previewWidth?c}, 
	previewHeight: ${element.previewHeight?c},
	<#if element.widths?has_content>widths: [<#list element.widths as width>${width?c}<#if width_has_next>,</#if></#list>],</#if>
	<#if element.heights?has_content>heights: [<#list element.heights as height>${height?c}<#if height_has_next>,</#if></#list>],</#if>
	scale: ${element.scale?c},
	cropLabel: '${element.formContext.messageResolver.getMessage('label.imageUpload.crop', 'Crop')?js_string}', 
	undoLabel: '${element.formContext.messageResolver.getMessage('label.imageUpload.undo', 'Undo')?js_string}',
	onCrop: function(cropper) {
		setValid($('${element.parentId}'), true);
		var errors = $('${element.parentId}-error');
		if (errors) {
			cropper.errors = errors.innerHTML;
			errors.update();
		}
	},
	onUndo: function(cropper) {
		if (cropper.errors) {
			$('${element.parentId}-error').update(cropper.errors);
		}
	}
});