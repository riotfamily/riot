package org.riotfamily.common.web.dwr;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * RequestWrapper that moves characters from the <code>pathInfo</code> to
 * <code>servletPath</code>.
 * <p>
 * DWR uses <code>request.getPathInfo()</code> to extract lookup-paths from
 * the URL, which fails, when the DwrController is used with any other 
 * mapping than <code>'/'</code>.  
 * </p>
 */
public class PathShiftingRequestWrapper extends HttpServletRequestWrapper {

	private String servletPath;
	
	private String pathInfo;
	
	public PathShiftingRequestWrapper(HttpServletRequest request, int offset) {
		super(request);
		String s = request.getPathInfo();
		this.pathInfo = s.substring(offset);
		this.servletPath = request.getServletPath() + s.substring(0, offset);
	}

	public String getServletPath() {
		return this.servletPath;
	}
	
	public String getPathInfo() {
		return this.pathInfo;
	}

}
