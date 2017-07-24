package edu.ncrn.cornell.ced2ar.ced2ardata2ddi.web.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.swordapp.client.DepositReceipt;
import org.w3c.dom.Document;

import edu.ncrn.cornell.ced2ar.ced2ardata2ddi.util.SwordDeposit;
import edu.ncrn.cornell.ced2ar.csv.SpssCsvGenerator;
import edu.ncrn.cornell.ced2ar.csv.StataCsvGenerator;
import edu.ncrn.cornell.ced2ar.csv.VariableCsv;
import edu.ncrn.cornell.ced2ar.ddi.CodebookVariable;
import edu.ncrn.cornell.ced2ar.ddi.VariableDDIGenerator;

@RestController
public class DataFileRestController {
	private static final Logger logger = Logger
			.getLogger(DataFileRestController.class);

	/**
	 * June 2017 - Converting this over to the new protype Lars wants to create codebooks from datasets and/or a SWORD2 interface.
	 * 
	 * 	Pulled the original source code from: https://forge.cornell.edu/svn/repos/ncrn-cornell/branches/ced2ar/data/Tools/ced2ardata2ddi
	 * 	Removed all the /src/main/java/org/swordapp/* files.  (There were differences between these files and the released versions.)
	 *  Rebuilding the pom.xml file.  Taking out all the swordapp dependencies to get it to work...
	 *  Making changes to get it to work on tomcat.
	 *  Adding in error messaging
	 * 
	 * @param request
	 * @param response
	 * @param file
	 * @param summaryStats
	 * @param recordLimit
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/data2ddi", method = RequestMethod.POST, produces = {
			"application/xml", "text/plain", "application/json" })
	public String spss2DDI(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "file", required = true) MultipartFile file,
			@RequestParam(value = "summaryStats", required = true, defaultValue = "true") boolean summaryStats,
			@RequestParam(value = "recordLimit", required = true, defaultValue = "-1") long recordLimit)
			throws Exception {

		/*
		 * Commented out existing code.  Need to bypass SWORD calls because it contacts the SWORD server right away.

		SwordDeposit swordDeposit = new SwordDeposit();
		DepositReceipt receipt = swordDeposit.DepositDataFile(file);
		String depositLocation = receipt.getLocation();
		VariableCsv variablesCSV;
		if (depositLocation.toLowerCase().endsWith(".dta")) {
			StataCsvGenerator gen = new StataCsvGenerator();
			variablesCSV = gen.generateVariablesCsv(depositLocation,
					summaryStats, recordLimit);
		} else if (depositLocation.toLowerCase().endsWith(".sav")) {
			SpssCsvGenerator gen = new SpssCsvGenerator();
			variablesCSV = gen.generateVariablesCsv(depositLocation,
					summaryStats, recordLimit);
		} else {
			response.setStatus(417);
			return "";
		}

		VariableDDIGenerator variableDDIGenerator = new VariableDDIGenerator();
		List<CodebookVariable> codebookVariables = variableDDIGenerator
				.getCodebookVariables(variablesCSV);
		Document document = variableDDIGenerator
				.getCodebookDocument(codebookVariables);
		String ddi2String = variableDDIGenerator.DOM2String(document);

		if (variablesCSV.getReadErrors() > 0) {
			long readErrors = variablesCSV.getReadErrors();
			response.addHeader(
					"message",
					readErrors
							+ ": Unable to read some variable values. Possible invalid summary statistics.");
			response.setStatus(417);
		}
		return ddi2String;
 */
	
		/*
		 * Get the Stata (.dta) file to DDI xml working for the prototype.
		 */

		String depositLocation = file.getOriginalFilename();
		String tempFileLocation = "";

		VariableCsv variablesCSV;

		/*
		 * Fix Error: java.io.FileNotFoundException:
		 * 	- Moving this code to the server side (and possibly removing SWORD classes) caused this error.
		 * 	  We need to access the file on the tomcat server.  (That or modify the other packages that are called.)
		 */
		CommonsMultipartFile cmf = (CommonsMultipartFile) file;
		String tempFilePath = cmf.getStorageDescription();
		logger.info("tempFile Path: " + tempFilePath);
		// Strip out the characters added by getStorageDescription ("at [" and "]")
		// TODO: May need to add checking for return values of :  "in memory"  and "on disk"
		if (tempFilePath.startsWith("at [")) {
			tempFileLocation = tempFilePath.substring(4, tempFilePath.length() - 1);
		}
		logger.info("tempFile Location: " + tempFileLocation);
		logger.info("depositLocation: " + depositLocation);

		if (depositLocation.toLowerCase().endsWith(".dta")) {
			StataCsvGenerator gen = new StataCsvGenerator();
			variablesCSV = gen.generateVariablesCsv(tempFileLocation,
					summaryStats, recordLimit);
		} else if (depositLocation.toLowerCase().endsWith(".sav")) {
			SpssCsvGenerator gen = new SpssCsvGenerator();
			variablesCSV = gen.generateVariablesCsv(tempFileLocation,
					summaryStats, recordLimit);
		} else {
			String message = "NOT a .dta or .sav file.  Cannot convert file: "  + depositLocation;
			logger.info("Returning 417.  message: " + message);
			response.addHeader("message", message);
			response.setStatus(417);
			return "";
		}

		VariableDDIGenerator variableDDIGenerator = new VariableDDIGenerator();
		List<CodebookVariable> codebookVariables = variableDDIGenerator
				.getCodebookVariables(variablesCSV);
		Document document = variableDDIGenerator
				.getCodebookDocument(codebookVariables);
		String ddi2String = variableDDIGenerator.DOM2String(document);

		if (variablesCSV.getReadErrors() > 0) {
			long readErrors = variablesCSV.getReadErrors();
			response.addHeader(
					"message",
					readErrors
							+ ": Unable to read some variable values. Possible invalid summary statistics.");
			response.setStatus(417);
			logger.info("Returning 417.  Unable to read some variable values. Possible invalid summary statistics.");
		}
		return ddi2String;

	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ MultipartException.class })
	public String exceptionHandler(Throwable ex) {
		String message = "";
		;
		if (ex instanceof MultipartException) {
			message = "Request is may be missing data file.";
		}
		message += ex.getMessage();
		logger.info("message: " + message);
		return message;
	}
}