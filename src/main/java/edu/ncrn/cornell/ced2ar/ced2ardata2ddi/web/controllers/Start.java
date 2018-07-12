package edu.ncrn.cornell.ced2ar.ced2ardata2ddi.web.controllers;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
//import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class Start {

	@Autowired
	private HttpServletRequest request;

	private static final Logger logger = Logger.getLogger(Start.class);
	
	/**
	 * Method home.
	 * Redirects to the about page
	 * @return String redirects to about page
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(){
		return "redirect:about";
	}

	/**
	 * Method about.
	 * Shows about ced2ardata2ddi page
	 * @param model Model
	 * @return String the info page location
	 */
	@RequestMapping(value = "/about", method = RequestMethod.GET)
    public String about(Model model) {
		String mainVer = "0.0.0";
		java.io.InputStream in = request.getServletContext().getResourceAsStream(
			"META-INF/maven/edu.ncrn.cornell.ced2ar.service/ced2ardata2ddi/pom.properties"
		);
		Properties mProps = new Properties();
		try {
			mProps.load(in);
			mainVer = (String) mProps.get("version");
		} catch(IOException ex) {
			logger.error("Error reading version from pom.properties: " + ex.getMessage());
		}

		model.addAttribute("mainVer", mainVer);
		model.addAttribute("subTitl","About");
		model.addAttribute("metaDesc","Information about the CED2AR Data to DDI project.");
        return "about";
    } 

}