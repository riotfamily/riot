<div id="${element.id}">
<#if file??>
	<div class="filePreview">
		<a class="download" href="${element.downloadUrl}" title="Download"></a>
		<table class="fileInfo">
			<tbody>
				<#if file.fileName??><tr><td class="label">Name</td><td class="value">${file.fileName}</td></tr></#if>
				<#if file.formatedSize??><tr><td class="label">Size</td><td class="value">${file.formatedSize}</td></tr></#if>
				<#if file.contentType??><tr><td class="label">Type</td><td class="value">${file.contentType}</td></tr></#if>
			</tbody>
		</table>
	</div>
</#if>
</div>