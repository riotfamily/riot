package org.riotfamily.common.web.view.freemarker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.view.ViewContext;

public abstract class AbstractStringTransformerMethod 
		extends AbstractSimpleMethod {

	protected final Object exec(Object arg) throws Exception {
		HttpServletRequest request = ViewContext.getRequest();
		HttpServletResponse response = ViewContext.getResponse();
		return transform((String) arg, request, response);
	}
	
	protected abstract String transform(String s, HttpServletRequest request, 
			HttpServletResponse response) throws Exception;

}
