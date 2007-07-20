<div id="${element.id}">
<#if file.present>
	<div class="filePreview">
		<#if file.present>
			<a class="download" href="${element.downloadUrl}" title="Download"></a>
			<table class="fileInfo">
				<tbody>
					<#if file.fileName?exists><tr><td class="label">Name</td><td class="value">${file.fileName}</td></tr></#if>
					<#if file.formatedSize?exists><tr><td class="label">Size</td><td class="value">${file.formatedSize}</td></tr></#if>
					<#if file.contentType?exists><tr><td class="label">Type</td><td class="value">${file.contentType}</td></tr></#if>
				</tbody>
			</table>
		</#if>
	</div>
</#if>
</div>