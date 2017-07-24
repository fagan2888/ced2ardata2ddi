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
<head>
	<body>
		<div class="container-fluid">
			<div class="row">
				<div class="col-sm-12" style="background-color: #B40404;">
					<div class="row">
						<h1 style="color: #FFFFFF">
							CED<sup>2</sup>AR DDI Generator
						</h1>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-12">&nbsp;</div>
			</div>

			<form method="POST" action="generateDDI" enctype="multipart/form-data">
				<div class="row">
					<div class="col-sm-6">
						<table class="table table-responsive">
							<tr>
								<td>Please select a SPSS/STATA Data File (Under 100 MB)</td>
								<td><input type="file" name="file"></td>
							</tr>
							<tr>
								<td>Exclude Summary Statistics</td>
								<td><input type="checkbox" name="summaryStats" value="false"></td>
							</tr>
							<tr>
								<td>Observation Limit</td>
								<td><input type="text" name="recordLimit"></td>
							</tr>
						</table>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-6">
						<button type="submit" class="btn btn-default">Generate DDI</button>
					</div>
				</div>
			</form>
		</div>
	</body>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
	<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</html>