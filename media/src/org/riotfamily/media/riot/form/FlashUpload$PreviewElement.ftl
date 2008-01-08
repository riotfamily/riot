<div id="${element.id}">
<#if element.swf??>
	<div class="filePreview">
		<a id="${element.id}-view" class="view" href="${element.downloadUrl}" title="Download" target="_blank"></a>
		<table class="fileInfo">
			<tbody>
				<tr><td class="label">Name</td><td class="value">${element.swf.fileName}</td></tr>
				<tr><td class="label">Size</td><td class="value">${element.swf.formatedSize}</td></tr>
				<tr>
					<td class="label">Movie</td>
					<td class="value">Version ${element.swf.version} (${element.swf.width} &times; ${element.swf.height})</td>
				</tr>
			</tbody>
		</table>
		<div id="${element.id}-overlay" style="display:none;width:${previewWidth!0}px;height:${previewHeight!0}px"></div>
	</div>
</#if>
</div>