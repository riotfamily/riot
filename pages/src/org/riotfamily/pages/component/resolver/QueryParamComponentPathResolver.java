package org.riotfamily.pages.component.resolver;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.util.ServletMappingHelper;

/**
 * TODO Make this a subclass of UrlComponentPathResolver!
 */
public class QueryParamComponentPathResolver implements ComponentPathResolver {

	private List params;

	private String componentPathPrefix;

	private static ServletMappingHelper servletMappingHelper = 
			new ServletMappingHelper(true);

	public String getComponentPath(HttpServletRequest request) {

		StringBuffer path;
		if (componentPathPrefix != null) {
			path = new StringBuffer(componentPathPrefix);
		}
		else {
			path = new StringBuffer(servletMappingHelper
					.getLookupPathForRequest(request));
		}

		Iterator it = params.iterator();
		while (it.hasNext()) {
			String paramValue = request.getParameter((String) it.next());
			path.append(':').append(paramValue);
		}

		return path.toString();
	}

	/**
	 * Returns a substring of the given path starting at zero and ending before
	 * the last slash character. If no slash is found or the only slash is at
	 * the beginning of the path, <code>null</code> is returned.
	 */
	public String getParentPath(String path) {
		int i = path.lastIndexOf('/');
		if (i > 0) {
			return path.substring(0, i);
		}
		return null;
	}

	public List getParams() {
		return params;
	}

	public void setParams(List params) {
		this.params = params;
	}

	public void setComponentPathPrefix(String componentPathPrefix) {
		this.componentPathPrefix = componentPathPrefix;
	}

}
