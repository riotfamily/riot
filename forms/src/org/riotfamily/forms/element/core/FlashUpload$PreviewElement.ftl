<div id="${element.id}">
<#if flash.present>
	<div class="filePreview">
		<a class="view" href="${element.downloadUrl}" title="Download" target="_blank"></a>
		<table class="fileInfo">
			<tbody>
				<#if flash.fileName?exists><tr><td class="label">Name</td><td class="value">${flash.fileName}</td></tr></#if>
				<#if flash.formatedSize?exists><tr><td class="label">Size</td><td class="value">${flash.formatedSize}</td></tr></#if>
				<#if flash.validSwf>
					<#if flash.width?exists><tr><td class="label">Movie</td><td class="value">Version ${flash.version} (${flash.width} &times; ${flash.height})</td></tr></#if>
				</#if>
			</tbody>
		</table>
	</div>
</#if>
</div>