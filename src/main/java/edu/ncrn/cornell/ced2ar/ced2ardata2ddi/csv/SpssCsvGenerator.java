package edu.ncrn.cornell.ced2ar.ced2ardata2ddi.csv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.opendatafoundation.data.FileFormatInfo;
import org.opendatafoundation.data.FileFormatInfo.ASCIIFormat;
import org.opendatafoundation.data.spss.SPSSFile;
import org.opendatafoundation.data.spss.SPSSFileException;
import org.opendatafoundation.data.spss.SPSSNumericVariable;
import org.opendatafoundation.data.spss.SPSSStringVariable;
import org.opendatafoundation.data.spss.SPSSVariable;
import org.opendatafoundation.data.spss.SPSSVariableCategory;

/**
 * @author Cornell University, Copyright 2012-2015
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team
 *
 *
 */
public class SpssCsvGenerator extends CsvGenerator {
	private static final Logger logger = Logger
			.getLogger(SpssCsvGenerator.class);

	/**
	 * generates CSV using a data file upload by the sword server
	 * 
	 * @param dataFileLocation
	 * @return
	 * @throws Exception
	 */
	public VariableCsv generateVariablesCsv(String dataFileLocation)
			throws Exception {
		return generateVariablesCsv(dataFileLocation, true, -1);
	}

	/**
	 * generates CSV using a data file upload by the sword server
	 * 
	 * @param dataFileLocation
	 * @return
	 * @throws Exception
	 */
	public VariableCsv generateVariablesCsv(String dataFileLocation,
			boolean summaryStatistics, long recordLimit) throws Exception {
		SPSSFile spssFile = null;
		File serverFile = new File(dataFileLocation);
		spssFile = new SPSSFile(serverFile);
		VariableCsv variableCSV = getVariablesCsv(spssFile, summaryStatistics,
				recordLimit);
		return variableCSV;
	}

	/**
	 * 
	 * @param spssFile
	 *            spss data file that is to be read
	 * @return Generates two element string array. First element is a csv of
	 *         variable summary statistics Second element is a csv of variable
	 *         values as defined in SPSS Third element is the read error count
	 *         of the individual variable values. Third element is an indicator
	 *         of the validity of the statistics.
	 * @throws SPSSFileException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public VariableCsv getVariablesCsv(SPSSFile spssFile,
			boolean includeSummaryStatistics, long recordLimit)
			throws SPSSFileException, IOException {
		long startTime = System.currentTimeMillis();
		logger.info("Metadata is being loaded from spssFile");
		spssFile.loadMetadata();
		logger.info("Metadata is loaded from spssFile");
		List<Ced2arVariableStat> ced2arVariableStats = new ArrayList<Ced2arVariableStat>();
		int totalVariables = spssFile.getVariableCount();
		logger.info("Variable Count " + totalVariables);
		int startPosition = 0;
		for (int i = 0; i < totalVariables; i++) {
			if (i % 1001 == 1000)
				logger.info("Reading  variable " + i + "of " + totalVariables);
			SPSSVariable spssVariable = spssFile.getVariable(i);
			Ced2arVariableStat variable = new Ced2arVariableStat();
			variable.setName(spssVariable.getName());
			variable.setLabel(spssVariable.getLabel());
			int width = spssVariable.variableRecord.getWriteFormatWidth();
			variable.setStartPosition(startPosition);
			startPosition += width;
			variable.setEndPosition(startPosition);
			variable.setVariableNumber(spssVariable.getVariableNumber());
			if (spssVariable.categoryMap != null) {
				Iterator it = spssVariable.categoryMap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					SPSSVariableCategory cat = (SPSSVariableCategory) pair
							.getValue();
					if (cat.isMissing()) {
						HashMap hm = variable.getMissingValues();
						hm.put(cat.strValue, cat.label);
					} else {
						HashMap hm = variable.getValidValues();
						hm.put(cat.strValue, cat.label);
					}
				}
			}
			if (spssVariable instanceof SPSSNumericVariable) {
				variable.setNumeric(true);
				variable.setDate(spssVariable.isDate());
			} else if (spssVariable instanceof SPSSStringVariable) {
				variable.setNumeric(false);
				variable.setDate(false);
			}
			ced2arVariableStats.add(variable);
		}

		long endTime = System.currentTimeMillis();
		logger.info("Time to process Meta Data "
				+ ((endTime - startTime) / 1000) + " seconds");
		long readErrors = 0;
		if (includeSummaryStatistics) {
			readErrors = setSummaryStatistics(spssFile, ced2arVariableStats,
					recordLimit);
		}

		endTime = System.currentTimeMillis();
		logger.info("Time to process variable statistics  "
				+ ((endTime - startTime) / 1000) + " seconds");

		String variableStatistics = getSummaryStatisticsVaribleCSV(
				ced2arVariableStats, includeSummaryStatistics);
		String variableValueLabels = getVariableValueLabelCSV(ced2arVariableStats);

		VariableCsv variablesCSV = new VariableCsv();
		variablesCSV.setVariableStatistics(variableStatistics);
		variablesCSV.setVariableValueLables(variableValueLabels);
		variablesCSV.setReadErrors(readErrors);

		return variablesCSV;
	}

	/**
	 * Sets Summary Statistics for each of the variable
	 * 
	 * @param spssFile
	 * @param variables
	 *            Variable array that will be updated
	 * @param recordLimit
	 *            indicates the sample size for the summary statistics. -1 for
	 *            all
	 * @throws IOException
	 * @throws SPSSFileException
	 * @returns read error count of the variable values. This happens when this
	 *          program unable to read a value properly happens mostly because
	 *          the data set is converted from SAS or STATA and not in unicode.
	 * 
	 */
	private long setSummaryStatistics(SPSSFile spssFile,
			List<Ced2arVariableStat> variables, long recordLimit)
			throws IOException, SPSSFileException {
		long totalRecords = spssFile.getRecordCount();
		long readErrors = 0;
		logger.info("Total Records " + totalRecords);
		FileFormatInfo fileFormatCSV = new FileFormatInfo();
		fileFormatCSV.asciiFormat = ASCIIFormat.CSV;
		for (int i = 1; i <= totalRecords; i++) {
			if (i % 1000 == 999)
				logger.info("Processing record " + (i + 1) + " of "
						+ totalRecords + " from the data file");
			if (i >= recordLimit && recordLimit != PROCESS_ALL_RECORDS) {
				logger.info("Reached record limit of " + recordLimit
						+ " ending further processing of records");
				break;
			}
			try {
				String record = spssFile
						.getRecordFromDisk(fileFormatCSV, false);
				String[] varValues = record.split(",");
				readErrors = updateVariableStatistics(variables, varValues);
			} catch (Exception ex) {
				logger.error("An error occured in reding observation " + i
						+ ". Skipping this observation " + ex);
			}
		}
		return readErrors;
	}

}
