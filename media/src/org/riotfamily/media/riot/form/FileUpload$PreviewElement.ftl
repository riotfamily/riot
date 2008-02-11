<div id="${element.id}">
<#if element.file??>
	<div class="filePreview">
		<a class="download" href="${element.downloadUrl}" title="Download"></a>
		<table class="fileInfo">
			<tbody>
				<#if element.file.fileName??><tr><td class="label">Name</td><td class="value">${element.file.fileName}</td></tr></#if>
				<#if element.file.formatedSize??><tr><td class="label">Size</td><td class="value">${element.file.formatedSize}</td></tr></#if>
				<#if element.file.contentType??><tr><td class="label">Type</td><td class="value">${element.file.contentType}</td></tr></#if>
			</tbody>
		</table>
	</div>
</#if>
</div>