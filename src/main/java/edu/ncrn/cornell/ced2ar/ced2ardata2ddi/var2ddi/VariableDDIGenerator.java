package edu.ncrn.cornell.ced2ar.ced2ardata2ddi.var2ddi;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import au.com.bytecode.opencsv.CSVReader;
import edu.ncrn.cornell.ced2ar.ced2ardata2ddi.csv.VariableCsv;

public class VariableDDIGenerator {
	public String namespace = "";

	public static void main(String argc[]) throws Exception {
		if (argc.length < 2) {
			System.out.println("Please enter location of vars.csv and varValues.csv files");
			System.exit(0);
		}
		File varFile = new File(argc[0]);
		File varValuesFile = new File(argc[1]);

		VariableDDIGenerator r = new VariableDDIGenerator();
		List<CodebookVariable> variables = r.getCodebookVariables(argc[0],
				argc[1]);
		Document codebookDoc = r.getCodebookDocument(variables);
		FileWriter writer = null;
		try {
			File ddiFile = new File("ddi.xml");
			writer = new FileWriter(ddiFile.getAbsoluteFile());
			writer.write(r.DOM2String(codebookDoc));
			writer.close();
			System.out.println("DDI is successfully created");
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	/*
	 * <var ID="V5" name="MSC"> <labl>Metropolitan Status Code (provided by
	 * MSG)</labl> <sumStat type="vald">1000</sumStat> <sumStat
	 * type="invd">0</sumStat> <sumStat type="min"/> <sumStat type="max"/>
	 * <sumStat type="mean"/> <sumStat type="stdev"/> <catgry>
	 * <catValu>3</catValu> <labl>Inside a suburban county of the MSA</labl>
	 * </catgry> <catgry> <catValu>2</catValu> <labl>Outside center city of an
	 * MSA but inside county containing
	 * c</labl></catgry><catgry><catValu>1</catValu><labl>In the center city of
	 * an MSA</labl></catgry><catgry><catValu>5</catValu><labl>Not in an
	 * MSA</labl></catgry><catgry><catValu>4</catValu><labl>In an MSA that has
	 * no center city</labl></catgry></var>
	 */

	public Document getCodebookDocument(List<CodebookVariable> variables) {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder domBuilder;
		Document doc;

		try {
			domFactory.setNamespaceAware(true);
			domBuilder = domFactory.newDocumentBuilder();
			doc = domBuilder.newDocument();

			Element codeBook = doc.createElementNS(namespace, "codeBook");
			codeBook.setAttribute("version", "2.0");
			codeBook.setAttribute("ID", getUniqueID());
			doc.appendChild(codeBook);
			doc.getDocumentElement().normalize();

			// docDscr */
			Element docDscr = (Element) codeBook.appendChild(doc
					.createElementNS(namespace, "docDscr"));
			Element stdyDscr = (Element) codeBook.appendChild(doc
					.createElementNS(namespace, "stdyDscr"));
			Element citation = (Element) stdyDscr.appendChild(doc
					.createElementNS(namespace, "citation"));
			Element titlStmt = (Element) citation.appendChild(doc
					.createElementNS(namespace, "titlStmt"));
			Element titl = (Element) titlStmt.appendChild(doc.createElementNS(
					namespace, "titl"));
			Element dataDscr = (Element) codeBook.appendChild(doc
					.createElementNS(namespace, "dataDscr"));

			int varNum = 0;
			for (CodebookVariable codebookVariable : variables) {
				Element varElement = doc.createElementNS(namespace, "var");
				varElement.setAttribute("ID", "V" + (++varNum));
				varElement.setAttribute("name", codebookVariable.getName());

				Element labelElement = doc.createElementNS(namespace, "labl");
				String label = StringEscapeUtils.escapeXml(codebookVariable
						.getLabel());
				labelElement.setTextContent(label);
				varElement.appendChild(labelElement);

				Element sumStatValidElement = doc.createElementNS(namespace,
						"sumStat");
				sumStatValidElement.setAttribute("type", "vald");
				sumStatValidElement.setTextContent(codebookVariable
						.getValidCount());
				varElement.appendChild(sumStatValidElement);

				Element sumStatInValidElement = doc.createElementNS(namespace,
						"sumStat");
				sumStatInValidElement.setAttribute("type", "invd");
				sumStatInValidElement.setTextContent(codebookVariable
						.getInvalidCount());
				varElement.appendChild(sumStatInValidElement);

				Element sumStatMinElement = doc.createElementNS(namespace,
						"sumStat");
				sumStatMinElement.setAttribute("type", "min");
				sumStatMinElement
						.setTextContent(codebookVariable.getMinValue());
				varElement.appendChild(sumStatMinElement);

				Element sumStatMaxElement = doc.createElementNS(namespace,
						"sumStat");
				sumStatMaxElement.setAttribute("type", "max");
				sumStatMaxElement
						.setTextContent(codebookVariable.getMaxValue());
				varElement.appendChild(sumStatMaxElement);

				Element sumStatMeanElement = doc.createElementNS(namespace,
						"sumStat");
				sumStatMeanElement.setAttribute("type", "mean");
				sumStatMeanElement.setTextContent(codebookVariable.getMean());
				varElement.appendChild(sumStatMeanElement);

				Element sumStatStdDevElement = doc.createElementNS(namespace,
						"sumStat");
				sumStatStdDevElement.setAttribute("type", "stdev");
				sumStatStdDevElement.setTextContent(codebookVariable
						.getStdDeviation());
				varElement.appendChild(sumStatStdDevElement);

				List<String> variableCodes = codebookVariable
						.getVariableCodes();
				for (String variableCode : variableCodes) {
					if (variableCode.equalsIgnoreCase(codebookVariable
							.getName()))
						continue;
					Element catgryElement = doc.createElementNS(namespace,
							"catgry");

					String splits[] = variableCode.split("=");
					if (splits.length < 2)
						continue;
					Element catValuElement = doc.createElementNS(namespace,
							"catValu");
					catValuElement.setTextContent(splits[0]);
					catgryElement.appendChild(catValuElement);

					Element lablElement = doc
							.createElementNS(namespace, "labl");
					String valueLabel = StringEscapeUtils.escapeXml(splits[1]);
					lablElement.setTextContent(valueLabel);
					catgryElement.appendChild(lablElement);

					varElement.appendChild(catgryElement);
				}
				dataDscr.appendChild(varElement);
			}
			return doc;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private List<CodebookVariable> getCodebookVariables(String varFile,
			String varValuesFile) throws Exception {
		List<CodebookVariable> codebookVariables = new ArrayList<CodebookVariable>();
		CSVReader csvReader = null;
		CSVReader csvValuesReader = null;
		List<String[]> vars = null;
		List<String[]> varValues = null;

		try {
			// csvReader = new CSVReader(new
			// FileReader("C:\\java\\info\\Data\\bak\\vars.csv"));
			// csvValuesReader = new CSVReader(new
			// FileReader("C:\\java\\info\\Data\\bak\\varValues.csv"));
			csvReader = new CSVReader(new FileReader(varFile));
			// csvReader = new CSVReader(new FileReader(varFile));
			csvValuesReader = new CSVReader(new FileReader(varValuesFile));

			vars = csvReader.readAll();
			varValues = csvValuesReader.readAll();

			for (int i = 0; i < vars.size(); i++) {
				if (i == 0)
					continue;
				if (i == 127) {
					i = i;
				}

				String[] var = vars.get(i);
				String[] varValue = varValues.get(i - 1);
				CodebookVariable cv = new CodebookVariable();
				int g = var.length;

				switch (var.length) {
				case 1:
					cv.setName(var[0]);
					break;
				case 2:
					cv.setName(var[0]);
					cv.setLabel(var[1]);
					break;
				case 3:
					cv.setName(var[0]);
					cv.setLabel(var[1]);
					cv.setValidCount(var[2]);
					break;
				case 4:
					cv.setName(var[0]);
					cv.setLabel(var[1]);
					cv.setValidCount(var[2]);
					cv.setInvalidCount(var[3]);
					break;
				case 5:
					cv.setName(var[0]);
					cv.setLabel(var[1]);
					cv.setValidCount(var[2]);
					cv.setInvalidCount(var[3]);
					cv.setMinValue(var[4]);
					break;
				case 6:
					cv.setName(var[0]);
					cv.setLabel(var[1]);
					cv.setValidCount(var[2]);
					cv.setInvalidCount(var[3]);
					cv.setMinValue(var[4]);
					cv.setMaxValue(var[5]);
					break;
				case 7:
					cv.setName(var[0]);
					cv.setLabel(var[1]);
					cv.setValidCount(var[2]);
					cv.setInvalidCount(var[3]);
					cv.setMinValue(var[4]);
					cv.setMaxValue(var[5]);
					cv.setMean(var[6]);
					break;
				case 8:
					cv.setName(var[0]);
					cv.setLabel(var[1]);
					cv.setValidCount(var[2]);
					cv.setInvalidCount(var[3]);
					cv.setMinValue(var[4]);
					cv.setMaxValue(var[5]);
					cv.setMean(var[6]);
					cv.setStdDeviation(var[7]);
					break;
				default:
					break;

				}
				for (int j = 0; j < varValue.length; j++) {
					cv.getVariableCodes().add(varValue[j]);
				}
				codebookVariables.add(cv);
			}
		} finally {
			csvReader.close();
			csvValuesReader.close();
		}
		return codebookVariables;
	}

	public List<CodebookVariable> getCodebookVariables(VariableCsv variableCSV)
			throws Exception {
		List<CodebookVariable> codebookVariables = new ArrayList<CodebookVariable>();
		List<String[]> vars = new ArrayList<String[]>();
		List<String[]> varValues = new ArrayList<String[]>();
		// String varString = variablesVSC[0];
		String varString = variableCSV.getVariableStatistics();
		String var1[] = varString.split("\n");
		for (int x = 0; x < var1.length; x++) {
			String[] xs = var1[x].split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			vars.add(xs);
		}

		// varString = variablesVSC[1];
		varString = variableCSV.getVariableValueLables();
		String var2[] = varString.split("\n");
		for (int x = 0; x < var2.length; x++) {
			String[] xs = var2[x].split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			varValues.add(xs);
		}

		// String varValueString = variablesVSC[1];
		String varValueString = variableCSV.getVariableValueLables();
		for (int i = 0; i < vars.size(); i++) {
			if (i == 0)
				continue;
			if (i == 127) {
				i = i;
			}
			String[] var = vars.get(i);
			String[] varValue = varValues.get(i - 1);
			CodebookVariable cv = new CodebookVariable();
			int g = var.length;

			switch (var.length) {
			case 1:
				cv.setName(var[0]);
				break;
			case 2:
				cv.setName(var[0]);
				cv.setLabel(var[1]);
				break;
			case 3:
				cv.setName(var[0]);
				cv.setLabel(var[1]);
				cv.setValidCount(var[2]);
				break;
			case 4:
				cv.setName(var[0]);
				cv.setLabel(var[1]);
				cv.setValidCount(var[2]);
				cv.setInvalidCount(var[3]);
				break;
			case 5:
				cv.setName(var[0]);
				cv.setLabel(var[1]);
				cv.setValidCount(var[2]);
				cv.setInvalidCount(var[3]);
				cv.setMinValue(var[4]);
				break;
			case 6:
				cv.setName(var[0]);
				cv.setLabel(var[1]);
				cv.setValidCount(var[2]);
				cv.setInvalidCount(var[3]);
				cv.setMinValue(var[4]);
				cv.setMaxValue(var[5]);
				break;
			case 7:
				cv.setName(var[0]);
				cv.setLabel(var[1]);
				cv.setValidCount(var[2]);
				cv.setInvalidCount(var[3]);
				cv.setMinValue(var[4]);
				cv.setMaxValue(var[5]);
				cv.setMean(var[6]);
				break;
			case 8:
				cv.setName(var[0]);
				cv.setLabel(var[1]);
				cv.setValidCount(var[2]);
				cv.setInvalidCount(var[3]);
				cv.setMinValue(var[4]);
				cv.setMaxValue(var[5]);
				cv.setMean(var[6]);
				cv.setStdDeviation(var[7]);
				break;
			default:
				break;

			}
			for (int j = 0; j < varValue.length; j++) {
				cv.getVariableCodes().add(varValue[j]);
			}
			codebookVariables.add(cv);
		}
		return codebookVariables;
	}

	public String DOM2String(Node node) throws TransformerException,
		TransformerFactoryConfigurationError {
		TransformerFactory transFactory = TransformerFactory.newInstance();
		StringWriter writer = new StringWriter();
		transFactory.newTransformer().transform(new DOMSource(node),
				new StreamResult(writer));
		String result = writer.getBuffer().toString();
		return (result);
	}

	public String getUniqueID() {
		return "ID_" + java.util.UUID.randomUUID().toString();
	}
}