package org.riotfamily.website.controller;

import java.util.Locale;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 7.0
 */
public class CountryFlagController implements Controller {
	
	private FileTypeMap fileTypeMap;
	
	private Resource location;
	
	private String defaultFlag;
	
	private String suffix;
	
	public CountryFlagController(FileTypeMap fileTypeMap) {
		this.fileTypeMap = fileTypeMap;
	}

	public void setLocation(Resource location) {
		this.location = location;
	}
	
	public void setDefaultFlag(String defaultFlag) {
		this.defaultFlag = defaultFlag;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	public ModelAndView handleRequest(HttpServletRequest request, 
					HttpServletResponse response) throws Exception {
		
		Resource res = null;
		String localeString = (String)request.getAttribute("locale");
		if (localeString != null) {
			Locale locale = StringUtils.parseLocaleString(localeString);			
			if (locale != null) {			
				String flagName = locale.getCountry();
				if (flagName != null) {
					res = location.createRelative(flagName.toLowerCase() + suffix);
				}
			}
		}
		
		if (res == null || !res.exists()) {
			res = location.createRelative(defaultFlag + suffix);
		}
		 		
		if (res.exists()) {			
			response.setContentType(getContentType(res));
			IOUtils.serve(res.getInputStream(), response.getOutputStream());
		}		
		return null;
	}
	
	protected String getContentType(Resource resource) {
		if (resource == null) {
			return null;
		}
		return fileTypeMap.getContentType(resource.getFilename());
	}

}
