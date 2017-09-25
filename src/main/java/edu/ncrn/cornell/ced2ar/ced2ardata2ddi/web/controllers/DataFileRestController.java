package edu.ncrn.cornell.ced2ar.ced2ardata2ddi.web.controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
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
	private static final Logger logger = Logger.getLogger(DataFileRestController.class);
	
	@Autowired
	private ServletContext context;
	
	private File tempDirectoryFile = null;
	private static final String FILE_PREFIX = "data_";
	private static final String FILE_SUFFIX = ".tmp";
	private static final String JAVA_TMPDIR = System.getProperty("java.io.tmpdir");

	/**
	 * June 2017 - Converting this over to the new protype Lars wants to create codebooks from datasets and/or a SWORD2 interface.
	 * 
	 * 	Pulled the original source code from: https://forge.cornell.edu/svn/repos/ncrn-cornell/branches/ced2ar/data/Tools/ced2ardata2ddi
	 * 	Removed all the /src/main/java/org/swordapp/* files.  (There were differences between these files and the released versions.)
	 *  Rebuilding the pom.xml file.  Taking out all the swordapp dependencies to get it to work...
	 *  Making changes to get it to work on tomcat.
	 *  Adding in error messaging...
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
		 * Objective: Get the Stata (.dta) file to DDI xml working for the prototype.
		 * 
		 */

		/*
		 * Found a problem during testing.  There are two main parts to the problem:
		 * 
		 *   1) Files uploaded (via http POST message) to the web server (tomcat) are treated differently depending on size.  
		 *		a) Smaller files are loaded into server memory.  The smaller “in memory” ones are failing. 
		 *		b) Larger files are stored on disk (as tmp files).  
		 *
		 *   2) The existing classes, in ced2arddigenerator-1.1.1.jar (StataCsvGenerator, StataReaderFactory, Dta115Reader), 
		 *   	require physical file locations. 
		 *   	The FileNotFoundException is thrown by the DtaReader.class.  (This is several calls below the DataFileRestController.java file.)
		 *
		 * The solution is to:
		 * 
		 * 	Use CommonsMultipartFile to find the StorageDescription.
		 * 		For LARGER files, use the location of the tmp file created by tomcat (listed in the StorageDescription) 
		 * 			for the downstream calls to classes in ced2arddigenerator-1.1.1.jar.
		 * 		For SMALLER "in memory" files, create a unique temp file.  Use the location of this unique "in memory" temp file 
		 * 			for the downstream calls.  Delete this temp file when done.
		 * 	Use an existing temp directory on the server.  Primary: javax.servlet.context.tempdir.  Alternate: java.io.tmpdir 
		 * 	Use getOriginalFilename() to do the file extension check.
		 */

		/*
		 * Find and set a temporary directory on the web server for the "in memory" files we need to create.
		 */		
		if(tempDirectoryFile == null) {
			setTempDirectoryFile();
		}
		
		 // FYI: fileLocation replaces depositLocation.
		String fileLocation = file.getOriginalFilename();
		logger.info("file.getOriginalFilename(): " + file.getOriginalFilename());

		VariableCsv variablesCSV;

		/*
		 * getStorageDescription can return these formats/values:
		 * 
		 *   1) "at ["<tempFilePathName."]" - used by (tomcat) servlet for large files
		 *      IF a tempFilePathName, use this.  Strip out the characters added by getStorageDescription ("at [" and "]")
		 *      
		 *   2) "in memory" - used by (tomcat) servlet for small files.  (Controlled by MaxInMemorySize.)
		 *      IF this value, Move the content from memory to a file so the existing classes, in ced2arddigenerator-1.1.1.jar 
		 *         (StataCsvGenerator, StataReaderFactory, Dta115Reader), can process it.
		 *         
		 *   3) "on disk" - ?
		 */
		// TODO: May need to add checking for return values of "on disk"
		CommonsMultipartFile cmf = (CommonsMultipartFile) file;
		String fileStorageDesc = cmf.getStorageDescription();
		logger.info("fileStorageDesc: " + fileStorageDesc);

		File inMemoryFile = null;
		
		try{
			if (fileStorageDesc.equalsIgnoreCase("in memory")) {
				// Move the content from in memory to a temp file.
				inMemoryFile = File.createTempFile(FILE_PREFIX, FILE_SUFFIX, tempDirectoryFile);
				logger.debug(" transferTo starting...");
				file.transferTo(inMemoryFile);
				logger.debug(" transferTo complete.");
				logger.debug("inMemoryFile.getAbsolutePath: " + inMemoryFile.getAbsolutePath());
				inMemoryFile.deleteOnExit();				
				fileLocation = inMemoryFile.getAbsolutePath();
				//fileLocation = inMemoryFile.getCanonicalPath();
			} else if (fileStorageDesc.startsWith("at [")) {
				fileLocation = fileStorageDesc.substring(4, fileStorageDesc.length() - 1);
			}
			
			logger.info("fileLocation: " + fileLocation);
			
			if (file.getOriginalFilename().toLowerCase().endsWith(".dta")) {
				StataCsvGenerator gen = new StataCsvGenerator();
				variablesCSV = gen.generateVariablesCsv(fileLocation,
						summaryStats, recordLimit);
			} else if (file.getOriginalFilename().toLowerCase().endsWith(".sav")) {
				SpssCsvGenerator gen = new SpssCsvGenerator();
				variablesCSV = gen.generateVariablesCsv(fileLocation,
						summaryStats, recordLimit);
			} else {
				String message = "NOT a .dta or .sav file.  Cannot convert file: "  + file.getOriginalFilename();
				logger.info("Returning 417.  message: " + message);
				response.addHeader("message", message);
				response.setStatus(417);
				return "";
			}
		}
		catch(Exception ex){
			String message = "Unhandled Exception: "  + ex;
			//logger.warn(message + "    Message: " + ex.getMessage());
			logger.warn("Returning 500.  message: " + message);
			response.addHeader("message", message);
			response.setStatus(500);
			return "";
		}
		finally{
			// If we created an in memory temp file, then delete it.
			if (inMemoryFile != null) {
				try {
					if (inMemoryFile.delete()) {
						logger.debug("file deleted: " + inMemoryFile.getCanonicalPath());
					}
				}
				catch(SecurityException ex) {
					logger.info("file NOT deleted due to SecurityException: " + ex.getMessage(), ex);
				}
				catch(IOException ex) {
					logger.info("file NOT deleted due to IOException: " + ex.getMessage(), ex);
				}
			}
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
		logger.info("Returning ddi2String (normal)");
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

	
	/**
	 * Find and set a temporary directory on the web server.  
	 *  
	 * Try to use the servlet's temporary directory first.  Make sure the directory exists and log path.
	 *   If not found, use the "java.io.tmpdir" one.
	 *   If neither are found, error out.
	 */
	private void setTempDirectoryFile() throws IOException{
		logger.debug("Starting setTempDirectoryFile...");
		
		File dir = (File) context.getAttribute("javax.servlet.context.tempdir");
		if(dir.exists()) {
			tempDirectoryFile = dir;
			logger.debug("dir.getAbsolutePath: " + dir.getAbsolutePath());
			//logger.debug("dir.getCanonicalPath: " + dir.getCanonicalPath());
		} else {
			logger.info("servletContext tempdir does NOT exist. Checking for java.io.tmpdir ...");
			logger.debug("JAVA_TMPDIR: " + JAVA_TMPDIR);

			File tmpDir = new File(JAVA_TMPDIR);
			if(tmpDir.exists()) {
				tempDirectoryFile = tmpDir;
				logger.debug("tmpDir.getAbsolutePath: " + tmpDir.getAbsolutePath());
				//logger.debug("tmpDir.getCanonicalPath: " + tmpDir.getCanonicalPath());
			} else {
				logger.warn("Error java.io.tmpdir does NOT exist either. throwing RuntimeException.");
				throw new RuntimeException("Could NOT setTempDirectory on server.");
			}
		}
		logger.info("tempDirectoryFile path: " + tempDirectoryFile.getAbsolutePath());
	}


}