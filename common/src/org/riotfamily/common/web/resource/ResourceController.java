package org.riotfamily.common.web.resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.SocketException;
import java.util.Iterator;
import java.util.List;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.util.FormatUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.LastModified;

/**
 * Controller that serves an internal resource.
 * <p>
 * Note: This will only work when a prefix mapping is used for the 
 * DispatcherServlet (like <tt>/riot/*</tt>) since 
 * <code>request.getPathInfo()</code> is used.
 * </p>
 */
public class ResourceController extends WebApplicationObjectSupport
		implements Controller, LastModified {

	private static final String HEADER_EXPIRES = "Expires";
	
	private Log log = LogFactory.getLog(ResourceController.class);
	
	private FileTypeMap fileTypeMap = FileTypeMap.getDefaultFileTypeMap();
	
    private List mappings;
    
    private List filters;
    
	private long expiresAfter = 1000 * 60 * 60 * 24;
	
	private long lastModified = System.currentTimeMillis();
	
	private String pathAttribute;
	
	public void setExpiresAfter(String s) {
		this.expiresAfter = FormatUtils.parseMillis(s);
	}
	        
	/**
	 * @param pathAttribute The pathAttribute to set.
	 */
	public void setPathAttribute(String pathAttribute) {
		this.pathAttribute = pathAttribute;
	}
	
    public void setFileTypeMap(FileTypeMap fileTypeMap) {
		this.fileTypeMap = fileTypeMap;
	}

	public void setMappings(List resourceMappings) {
		this.mappings = resourceMappings;
	}

	public void setFilters(List filters) {
		this.filters = filters;
	}

	public long getLastModified(HttpServletRequest request) {
		return lastModified;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
            HttpServletResponse response) throws IOException {
        
    	String path;
    	if (pathAttribute != null) {
    		path = "/" + request.getAttribute(pathAttribute); 
    	}
    	else {
    		path = request.getPathInfo();
    	}
    	log.debug("Looking up resource " + path);
    	Iterator it = mappings.iterator();
    	while (it.hasNext()) {
    		ResourceMapping mapping = (ResourceMapping) it.next();
			Resource res = mapping.getResource(path);
			if (res != null) {
				response.addDateHeader(HEADER_EXPIRES, 
						System.currentTimeMillis() + expiresAfter);

				String contentType = fileTypeMap.getContentType(
						res.getFilename());
				
				response.setContentType(contentType);
				if (contentType.startsWith("text/")) {
					serveText(path, res, request, response);
				}
				else {
					try {
						FileCopyUtils.copy(res.getInputStream(), 
								response.getOutputStream());
					}
					catch (IOException e) {
						if (!SocketException.class.isInstance(e.getCause())) {
							throw e;
						}
					}
				}
				return null;
			}
		}
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
    	return null;
	}
	
	protected void serveText(String path, Resource res,
			HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		
		log.debug("Serving text resource: " + path);
		Reader in = new InputStreamReader(res.getInputStream());
		Writer out = response.getWriter();
		
		if (filters != null) {
			Iterator it = filters.iterator();
			while (it.hasNext()) {
				ResourceFilter filter = (ResourceFilter) it.next();
				if (filter.matches(path)) {
					log.debug("Filter " + filter + " matches.");
					in = filter.createFilterReader(in, request);
					break;
				}
				log.debug("Filter " + filter + " does not match.");
			}
		}
		FileCopyUtils.copy(in, out);
	}

}
