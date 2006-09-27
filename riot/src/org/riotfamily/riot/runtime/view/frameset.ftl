<#import "/spring.ftl" as spring />
<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" 
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">	
		
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title><@spring.messageText "title.riot", "Riot V6" /></title>
		<link rel="icon" href="${request.contextPath}${resourcePath}/style/images/favicon.ico" type="image/x-icon" />
    	<link rel="shortcut icon" href="${request.contextPath}${resourcePath}/style/images/favicon.ico" type="image/x-icon" /> 
		<script type="text/javascript" language="JavaScript" src="${request.contextPath}${resourcePath}/frameset.js"></script>
		<script type="text/javascript" language="JavaScript">
			var frameset = new RowFrameset('rows');
		</script>
		<style type="text/css">
			frame {
				background-color: #fff;
			}
		</style>
	</head>
	<frameset id="rows" rows="126,*,32" border="0">
		<frame name="path" src="${url(servletPrefix + '/path')}" />
		<frame name="editor" src="${url(servletPrefix + '/group')}" />
		<frame name="statusbar" src="${url(servletPrefix + '/statusbar')}" />
	</frameset>
</html>
