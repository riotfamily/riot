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
	<frameset rows="*">
		<frame src="${chooserUrl}" />
	</frameset>
</html>