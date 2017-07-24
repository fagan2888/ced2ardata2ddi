<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
	<title>CED2AR DDI Generator</title>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
<%-- 
	<link rel="stylesheet" type="text/css" href="styles/main.css" />
--%>
	<link rel="stylesheet" type="text/css" href="/styles/main.css" />
	<link rel="stylesheet" type="text/css" href="//netdna.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css" />
	<link rel="stylesheet" type="text/css" href="//netdna.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css" />
	<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
</head>
<body>
	<div class="row">
		<div class="col-sm-12" style="background-color: #B40404;">
			<div class="row">
				<h1 style="color: #FFFFFF">CED<sup>2</sup>AR DDI Generator</h1>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-sm-12">&nbsp;</div>
	</div>
	<form method="POST" action="downloadDDI">
		<div class="form-group">
			<input type='hidden' name="codebookName" value="${fileName}" /> <label
				for="comment">Generated DDI</label>
			<textarea class="form-control" rows="20" id="codebookDDI"
				name='codebookDDI'>${ddi2String}</textarea>
		</div>
		<button type="submit" class="btn btn-default">Download DDI</button>
	</form>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
	<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</body>
</html>