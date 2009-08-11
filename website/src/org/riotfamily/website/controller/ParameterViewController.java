package org.riotfamily.website.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.servlet.ServletUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * {@link ParameterizableViewController} that uses the request parameters as
 * model. Don't use this controller if you need to access multi-valued 
 * parameters since in this case only the first value is exposed to the model.
 */
public class ParameterViewController extends ParameterizableViewController {

	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Map<String, String> params = ServletUtils.getSingularParameterMap(request);
		return new ModelAndView(getViewName(), params);
	}
}
