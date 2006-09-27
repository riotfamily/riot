package org.riotfamily.common.web.view.freemarker;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.view.ViewContext;

public class IncludeMethod extends AbstractSimpleMethod {

	protected Object exec(Object arg) throws Exception {
		String url = (String) arg;
		HttpServletRequest request = ViewContext.getRequest();
		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.include(request, ViewContext.getResponse());
		return "";
	}

}
