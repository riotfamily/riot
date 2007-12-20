<div id="${element.id}">
<#if file??>
	<div class="filePreview">
		<a class="view" href="${element.downloadUrl}" title="Download" target="_blank"></a>
		<table class="fileInfo">
			<tbody>
				<tr><td class="label">Name</td><td class="value">${file.fileName}</td></tr>
				<tr><td class="label">Size</td><td class="value">${file.formatedSize}</td></tr>
				<tr>
					<td class="label">Movie</td>
					<td class="value">Version ${file.version} (${file.width} &times; ${file.height})</td>
				</tr>
			</tbody>
		</table>
	</div>
</#if>
</div>