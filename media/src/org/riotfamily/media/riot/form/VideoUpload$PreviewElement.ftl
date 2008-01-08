<div id="${element.id}">
<#if element.video??>
	<div class="filePreview">
		<a id="${element.id}-view" class="view" href="${element.downloadUrl}" title="Download" target="_blank"></a>
		<table class="fileInfo">
			<tbody>
				<tr><td class="label">Name</td><td class="value">${element.video.fileName}</td></tr>
				<tr><td class="label">Size</td><td class="value">${element.video.width} &times; ${element.video.height} (${element.video.formatedSize})</td></tr>
				<tr><td class="label">Codec</td><td class="value">${element.video.videoCodec} ${element.video.fps} fps</td></tr></tr>
				<#if element.video.audioCodec??>
					<tr>
						<td class="label">Audio</td>
						<td class="value">${element.video.audioCodec} ${element.video.samplingRate} Hz ${element.video.stereo?string('Stereo','Mono')}</td>
					</tr>
				</#if>
			</tbody>
		</table>
		<div id="${element.id}-overlay" style="display:none;width:${element.previewWidth}px;height:${element.previewHeight + 11}px"></div>
	</div>
</#if>
</div>