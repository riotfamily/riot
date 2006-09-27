<div id="${element.id}">
<#if file.present>
	<div class="filePreview">
		<#if element.previewAvailable>
			<div id="${element.id}-preview" class="previewImageFrame"></div>
		<#else>
			<a class="download" href="${element.previewUrl}" title="Download"></a>
		</#if>
		<table class="fileInfo">
			<tbody>
				<#if file.fileName?exists><tr><td class="label">Name</td><td class="value">${file.fileName}</td></tr></#if>
				<#if file.formatedSize?exists><tr><td class="label">Size</td><td class="value">${file.formatedSize}</td></tr></#if>
				<#if file.contentType?exists><tr><td class="label">Type</td><td class="value">${file.contentType}</td></tr></#if>
			</tbody>
		</table>
		<#if file.webImage>
			<div id="${element.id}-scaledImageHint" class="scaledImageHint" title="The image has been scaled. Click here to display the full-sized version."></div>
		</#if>
	</div>
</#if>
</div>