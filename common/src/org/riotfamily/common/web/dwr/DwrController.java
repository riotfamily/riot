package org.riotfamily.common.web.dwr;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

public class DwrController extends org.directwebremoting.spring.DwrController {

	private String mapping;
	
	public void setMapping(String mapping) {
		this.mapping = mapping;
	}
	
	public void setBeanName(String name) {
		super.setBeanName(name);
		if (mapping == null) {
			mapping = name;
		}
	}
		
	protected ModelAndView handleRequestInternal(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		HttpServletRequest shiftedRequest = new PathShiftingRequestWrapper(
				request, getPathOffset(request));
		
		return super.handleRequestInternal(shiftedRequest, response);
	}
	
	protected int getPathOffset(HttpServletRequest request) {
		String pathInfo = request.getPathInfo();
		int i = pathInfo.indexOf(mapping);
		return i + mapping.length();
	}
}
