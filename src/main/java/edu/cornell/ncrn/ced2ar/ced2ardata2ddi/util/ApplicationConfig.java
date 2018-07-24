package edu.cornell.ncrn.ced2ar.ced2ardata2ddi.util;

/**
 * This class handles updating and reading property=value pairs 
 * Requires ced2ardata2ddi-config.properties file in class path otherwise throws an exception.
 * @author Cornell University, Copyright 2012-2015
 * @author Venky Kambhampaty
 *
 * @author Cornell Institute for Social and Economic Research
 * @author Cornell Labor Dynamics Institute
 * @author NCRN Project Team 
 */

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.core.io.ClassPathResource;

public class ApplicationConfig {
	protected PropertiesConfiguration properties;

	public ApplicationConfig() {
		try {
			ClassPathResource cpr = new ClassPathResource(
					"ced2ardata2ddi-config.properties");
			properties = new PropertiesConfiguration(cpr.getFile());
		} catch (Exception e) {
			throw new RuntimeException(
					"Fatal Error. Unable to read configiration file: ced2ardata2ddi-config.properties",
					e);
		}
	}

}