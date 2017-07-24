package edu.ncrn.cornell.ced2ar.ced2ardata2ddi.web.controllers;

import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
// cs added
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import org.swordapp.client.DepositReceipt;
import org.w3c.dom.Document;

import edu.ncrn.cornell.ced2ar.ced2ardata2ddi.util.SwordDeposit;
import edu.ncrn.cornell.ced2ar.csv.SpssCsvGenerator;
import edu.ncrn.cornell.ced2ar.csv.StataCsvGenerator;
import edu.ncrn.cornell.ced2ar.csv.VariableCsv;
import edu.ncrn.cornell.ced2ar.ddi.CodebookVariable;
import edu.ncrn.cornell.ced2ar.ddi.VariableDDIGenerator;

@Controller
public class DataFileController {
	private static final Logger logger = Logger
			.getLogger(DataFileController.class);

	@RequestMapping(value = "/generateDDI", method = RequestMethod.POST)
	public String generateDDI(
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "summaryStats", required = true, defaultValue = "true") boolean summaryStats,
			@RequestParam(value = "recordLimit", required = true, defaultValue = "-1") long recordLimit,
			Model model) throws Exception {
		String ddi2String = "";
		
/*
 * Commented out existing code.  Need to bypass SWORD calls because it contacts the SWORD server right away.

		if (file != null && !StringUtils.isEmpty(file.getOriginalFilename())) {
			SwordDeposit swordDeposit = new SwordDeposit();
			DepositReceipt receipt = swordDeposit.DepositDataFile(file);
			String depositLocation = receipt.getLocation();
			if (depositLocation.toLowerCase().endsWith(".dta")) {
				StataCsvGenerator gen = new StataCsvGenerator();
				VariableCsv variablesCSV = gen.generateVariablesCsv(
						depositLocation, summaryStats, recordLimit);
				ddi2String = getDDIString(variablesCSV);
			} else if (depositLocation.toLowerCase().endsWith(".sav")) {
				SpssCsvGenerator gen = new SpssCsvGenerator();
				VariableCsv variablesCSV = gen.generateVariablesCsv(
						depositLocation, summaryStats, recordLimit);
				ddi2String = getDDIString(variablesCSV);
			} else {
				ddi2String = "Invalid Data file uploaded";
			}
		} else {
			ddi2String = "Data file is not uploaded.";
		}

		model.addAttribute("ddi2String", ddi2String);
		model.addAttribute("fileName", file.getOriginalFilename() + ".xml");
		return "codebookDDI";
 */
		
		 
		/*
		 * Get the Stata (.dta) file to DDI xml working for the prototype.
		 */		
		if (file != null && !StringUtils.isEmpty(file.getOriginalFilename())) {
			String depositLocation = file.getOriginalFilename();	
			String tempFileLocation = "";			
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
				VariableCsv variablesCSV = gen.generateVariablesCsv(
						tempFileLocation, summaryStats, recordLimit);
				ddi2String = getDDIString(variablesCSV);
			} else if (depositLocation.toLowerCase().endsWith(".sav")) {
				SpssCsvGenerator gen = new SpssCsvGenerator();
				VariableCsv variablesCSV = gen.generateVariablesCsv(
						depositLocation, summaryStats, recordLimit);
				ddi2String = getDDIString(variablesCSV);
			} else {
				ddi2String = "Invalid Data file uploaded";
			}
		} else {
			ddi2String = "No Data file uploaded.";
		}

		model.addAttribute("ddi2String", ddi2String);
		model.addAttribute("fileName", file.getOriginalFilename() + ".xml");
		return "codebookDDI";
	}

	private String getDDIString(VariableCsv variablesCSV) throws Exception {
		VariableDDIGenerator variableDDIGenerator = new VariableDDIGenerator();
		List<CodebookVariable> codebookVariables = variableDDIGenerator
				.getCodebookVariables(variablesCSV);
		Document document = variableDDIGenerator
				.getCodebookDocument(codebookVariables);
		String ddi2String = variableDDIGenerator.DOM2String(document);
		return ddi2String;
	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ MultipartException.class })
	public String exceptionHandler(Throwable ex) {
		String message = "";
		;
		if (ex instanceof MultipartException) {
			message = "Request is may be missing data file";
		}
		message += ex.getMessage();
		return message;
	}

	@RequestMapping(value = "/downloadDDI", method = RequestMethod.POST, produces = "text/plain")
	public void downloadDDI(@RequestParam("codebookDDI") String codebookDDI,
			@RequestParam("codebookName") String codebookName,
			HttpServletResponse response) throws Exception {
		OutputStream os = null;
		try {
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ codebookName + "\"");
			os = response.getOutputStream();
			os.write(codebookDDI.getBytes());
		} finally {
			os.flush();
			os.close();
		}
	}
}