package org.riotfamily.common.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * {@link ParameterizableViewController} that uses the request attributes as
 * model. Use this controller for simple views that need to access request
 * attributes but you don't want your ViewResolver to generally expose 
 * attributes. 
 */
public class AttributeViewController extends ParameterizableViewController {

	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Map attributes = ServletUtils.takeAttributesSnapshot(request);
		return new ModelAndView(getViewName(), attributes);
	}
}
