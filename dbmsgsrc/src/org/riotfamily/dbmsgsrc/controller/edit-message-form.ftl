<html>
<head>
<style type="text/css">
body {
    background: #fff;
}
form {
    margin: 20px 10px;
    text-align: right;
}
textarea {
    display: block;
    width: 100%;
    margin-bottom: 10px;
    border: 1px solid #e0e0e0;
}
</style>
</head>
<body>
	<form action="${request.requestURI}" method="POST">
		<textarea name="message-text" rows="5">${message!}</textarea>
		<input type="submit" value="Save" />
	</form>
</body>
</html>