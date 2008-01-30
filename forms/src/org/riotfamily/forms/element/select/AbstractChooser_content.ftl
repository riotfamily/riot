<html>
	<head>
		<title>${title?if_exists}</title>
		<script>
			function chosen(objectId) {
				if (typeof chooser == 'undefined') {
					alert('No chooser element associated with this window');
				}
				chooser.chosen(objectId);
			}
		</script>
	</head>
	<frameset rows="<#if pathUrl??>32,</#if>*" border="0">
		<#if pathUrl??><frame name="path" src="${pathUrl}" scrolling="no" /></#if>
		<frame name="chooserList" src="${chooserUrl}" />
	</frameset>
</html>