package org.riotfamily.common.web.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

/**
 *
 */
public class ServletMappingHelper extends UrlPathHelper {
	
	boolean useOriginalRequest = false;
		
	public ServletMappingHelper() {
	}
	
	public ServletMappingHelper(boolean useOriginalRequest) {
		this.useOriginalRequest = useOriginalRequest;
	}

	public void setUseOriginalRequest(boolean useOriginalRequest) {
		this.useOriginalRequest = useOriginalRequest;
	}

	/**
	 * Returns the lookup-path for a given request. This is either the path
	 * within the servlet-mapping (in case of a prefix mapping) or the path
	 * within the application without the trailing suffix (in case of a suffix
	 * mapping).
	 */
	public String getLookupPathForRequest(HttpServletRequest request) {
		String path = getPathWithinServletMapping(request);
		if (path.length() == 0) {
			path = getPathWithinApplication(request);
			if (path.equals(getServletPrefix(request))) {
				return "/";
			}
			int dotIndex = path.lastIndexOf('.');
			if (dotIndex >= 0) {
				path = path.substring(0, dotIndex);
			}	
		}
		return path;
	}
		
	/**
	 * Returns the servlet-mapping prefix for the given request or an empty 
	 * String if the servlet is mapped by a suffix.  
	 */
	public String getServletPrefix(HttpServletRequest request) {
		String path = getPathWithinApplication(request);
		String servletPath = getServletPath(request);
		if (path.length() > servletPath.length()
				|| path.lastIndexOf('/') > path.lastIndexOf('.')) {

			return servletPath;
		}
		return "";
	}
	
	/**
	 * Returns the servlet-mapping suffix for the given request or an empty
	 * String if the servlet is mapped by a prefix.
	 */
	public String getServletSuffix(HttpServletRequest request) {
		String path = getPathWithinApplication(request);
		if (path.equals(getServletPath(request))) {
			int dotIndex = path.lastIndexOf('.');
			if (dotIndex >= 0) {
				return path.substring(dotIndex);
			}
		}
		return "";
	}
	
	/**
	 * Returns a String consisting of the context-path and the servlet-prefix
	 * for the given request. The String will always end with a slash. 
	 */
	public String getRootPath(HttpServletRequest request) {
		StringBuffer path = new StringBuffer();
		path.append(getContextPath(request));
		path.append(getServletPrefix(request));
		path.append('/');
		return path.toString();
	}
	
	
	/**
	 * Overrides the super method to take the <code>useOriginalRequest</code>
	 * flag into account. If the flag is set to <code>true</code>, the 
	 * <code>javax.servlet.forward.request_uri</code> request attribute is 
	 * checked. If the attribute is not null, the value is returned, otherwise
	 * the super method is invoked. 
	 */
	public String getServletPath(HttpServletRequest request) {
		if (useOriginalRequest) {
			String servletPath = (String) request.getAttribute(
					WebUtils.FORWARD_SERVLET_PATH_ATTRIBUTE);
			
			if (servletPath == null) {
				servletPath = request.getServletPath();
			}
			return servletPath;
		}
		else {
			return super.getServletPath(request);
		}
	}

	/**
	 * Overrides the super method to take the <code>useOriginalRequest</code>
	 * flag into account. If the flag is set to <code>true</code>, the 
	 * <code>javax.servlet.forward.context_path</code> request attribute is 
	 * checked. If the attribute is not null, the value is returned, otherwise
	 * the super method is invoked. 
	 */
	public String getContextPath(HttpServletRequest request) {
		if (useOriginalRequest) {
			String contextPath = (String) request.getAttribute(
					WebUtils.FORWARD_CONTEXT_PATH_ATTRIBUTE);
			
			if (contextPath == null) {
				contextPath = request.getContextPath();
			}
			return decodeRequestString(request, contextPath);
		}
		else {
			return super.getContextPath(request);
		}
	}

	/**
	 */
	public String getRequestUri(HttpServletRequest request) {
		if (useOriginalRequest) {
			String uri = (String) request.getAttribute(
					WebUtils.FORWARD_REQUEST_URI_ATTRIBUTE);
			
			if (uri == null) {
				uri = request.getRequestURI();
			}
			uri = decodeRequestString(request, uri);
			int i = uri.indexOf(';');
			return i != -1 ? uri.substring(0, i) : uri;
		}
		else {
			return super.getRequestUri(request);
		}
	}
	
	/**
	 * Overrides the super method to take the <code>useOriginalRequest</code>
	 * flag into account. If the flag is set to <code>true</code>, the 
	 * <code>javax.servlet.forward.query_string</code> request attribute is 
	 * checked. If the attribute is not null, the value is returned, otherwise
	 * the super method is invoked. 
	 */
	public String getQueryString(HttpServletRequest request) {
		if (useOriginalRequest) {
			String queryString = (String) request.getAttribute(
					WebUtils.FORWARD_QUERY_STRING_ATTRIBUTE);
			
			if (queryString != null) {
				return queryString;
			}
		}
		return request.getQueryString();
	}

}
