package org.riotfamily.pages.component.resolver;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.util.ServletMappingHelper;

/**
 * ComponentPathResolver that utilizes a 
 * {@link ServletMappingHelper ServletMappingHelper} to determine the   
 * lookup-path for a given request.
 * <p>
 * Example: If the DispatcherServlet is mapped to <code>*.html</code> and 
 * <code>/myapp</code> is the application's context path, the component-path
 * for a request to <code>/myapp/foo/bar.html</code> will be 
 * <code>/foo/bar</code>.
 * </p>
 * @see ServletMappingHelper#getLookupPathForRequest(HttpServletRequest)
 */
public class UrlComponentPathResolver implements ComponentPathResolver {

	private static ServletMappingHelper servletMappingHelper = 
			new ServletMappingHelper(true);
	
	public String getComponentPath(HttpServletRequest request) {
		return servletMappingHelper.getLookupPathForRequest(request);
	}
	
	/**
	 * Returns a substring of the given path starting at zero and ending before
	 * the last slash character. If no slash is found or the only slash is 
	 * at the beginning of the path, <code>null</code> is returned.
	 */
	public String getParentPath(String path) {
		int i = path.lastIndexOf('/');
		if (i > 0) {
			return path.substring(0, i);
		}
		return null;
	}

}
