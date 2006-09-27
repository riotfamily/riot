<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de" xml:lang="de">
<head>
	<title>RiotFamily.org</title>
	<meta name="robots" CONTENT="noindex,nofollow" />
	<script src="${request.contextPath}/style/1px-corner.js" langauage="Javascript" type="text/javascript"></script>
	<link type="text/css" href="${request.contextPath}/style/screen.css" rel="stylesheet" media="screen" />
</head>
<body>
<div id="page-frame">
<div id="shadow-left">
<div id="shadow-right">
<div id="page">

<div id="header">
<a id="logo" href="#"></a>
</div>

<div id="page-body">

<@riot.include menu />

<div id="content">
	<@riot.include content />
</div>

<div id="extra">
	<@riot.include extra />
</div>

</div><!-- #page-body -->

<div id="footer">
	<div id="footer-content">
		&copy;2006 Riotfamily.org
	</div>
	<a id="spring-button" href="http://www.springframework.org" target="_blank"></a>
</div>

</div><!-- #page -->
</div><!-- #shadow-right -->
</div><!-- #shadow-left -->
</div><!-- #page-frame -->
<@riot.toolbar stylesheet="/style/riot.css" />
</body>
</html>
