<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
	<title>About - Data to DDI</title>
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
							CED<sup>2</sup>AR Data to DDI
						</h1>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-12">&nbsp;</div>
			</div>

		<div class="lb">
			<h2>About the CED<sup>2</sup>AR Data to DDI Project</h2>
			<p>
				The Comprehensive Extensible Data Documentation and Access Repository (CED<sup>2</sup>AR)
				is funded by the National Science Foundation (NSF), under grant <a href="http://www.nsf.gov/awardsearch/showAward?AWD_ID=1131848" target="_blank">#1131848</a>
				and developed by the <a href="https://www.ncrn.cornell.edu">Cornell Node of the NSF Census Research Network (NCRN)</a>.
				The Cornell NCRN branch includes researchers and developers from the Cornell Institute 
				for Social and Economic Research  <a href="http://ciser.cornell.edu/" target="_blank">(CISER)</a> 
				and the Cornell <a href="http://www.ilr.cornell.edu/ldi/" target="_blank">Labor Dynamics Institute</a>.	
			</p>
		</div>

		<div class="lb">
			<h3>Current Version</h3>
			<p>
				CED<sup>2</sup>AR Data to DDI version ${mainVer}
			</p>
			<c:if test="${initParam.buildTimeStamp} ne '${maven.build.timestamp}'}">
				<%-- When running tomcat from eclipse, these build values may not be available --%>
				<c:if test="${not empty initParam.buildTimeStamp}">
					<p>
					This instance was built on ${initParam.buildTimeStamp}
					</p>
				</c:if>	
			</c:if>	
		</div>

		<div class="lb" id="legal">
			<h3>Copyright Information</h3>
			<p class="lb2">
				Copyright 2012-2018 Cornell University. All rights reserved.
				CED<sup>2</sup>AR is licensed under the Creative Commons 
				Attribution-NonCommercial-ShareAlike 4.0 International License. 
				(See <a href="http://creativecommons.org/licenses/by-nc-sa/4.0/legalcode.txt">http://creativecommons.org/licenses/by-nc-sa/4.0/legalcode.txt</a>)
				Permission to copy, modify, and distribute all or any part of CED<sup>2</sup>AR, officially docketed at Cornell as D-6801, 
				titled "The Comprehensive Extensible Data Documentation and Access Repository" ("WORK") 
				and its associated copyrights for educational, research and non-profit purposes, without fee, 
				and without a written agreement is hereby granted, provided that the above copyright notice 
				and the four paragraphs of this document appear in all copies.
			</p>
			<p class="lb2">
				Those desiring to incorporate WORK into commercial products or use WORK 
				and its associated copyrights for commercial purposes 
				should contact the Cornell Center for Technology Enterprise 
				and Commercialization at 395 Pine Tree Road, Suite 310, Ithaca, NY 14850; 
				Email:cctecconnect@cornell.edu; Tel: 607-254-4698; Fax: 607-254-5454 
				for a commercial license.
			</p>
			<p class="lb2">
				In no event shall Cornell University be liable to any party for direct, 
				indirect, special, incidental, or consequential damages, 
				including lost profits, arising out of the use of work and its associated copyrights, 
				even if Cornell University may have been advised of the possibility of such damage.
			</p>
			<p class="lb2">
				The work is provided on an "AS IS" basis, and Cornell University has no obligation 
				to provide maintenance, support, updates, enhancements, or modifications. 
				of merchantability or fitness for a particular purpose, or that the use of work 
				and its associated copyrights will not infringe any patent, trademark or other rights.
			</p>
		</div>

		</div>
	</body>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
	<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</html>