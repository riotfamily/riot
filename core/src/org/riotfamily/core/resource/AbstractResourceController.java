package org.riotfamily.core.resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.spring.AbstractCacheableController;
import org.riotfamily.cachius.spring.Compressible;
import org.riotfamily.common.io.IOUtils;
import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.common.util.RiotLog;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.LastModified;

/**
 * Controller that serves an internal resource.
 * <p>
 * Note: This will only work when a prefix mapping is used for the 
 * DispatcherServlet (like <tt>/riot/*</tt>) since 
 * <code>request.getPathInfo()</code> is used.
 * </p>
 */
public class AbstractResourceController extends AbstractCacheableController
		implements LastModified, Compressible {

	private RiotLog log = RiotLog.get(AbstractResourceController.class);
	
	private FileTypeMap fileTypeMap = FileTypeMap.getDefaultFileTypeMap();
	
    private List<ResourceMapping> mappings;
    
    private List<ResourceFilter> filters;
    
	private long lastModified = System.currentTimeMillis();
	
	private boolean checkForModifications = false;
	
	private String pathAttribute;
	
	private String pathParameter;
	
		        
	/**
	 * Sets the name of the request attribute that will contain the 
	 * resource path. 
	 */
	public void setPathAttribute(String pathAttribute) {
		this.pathAttribute = pathAttribute;
	}
	
	/**
	 * Sets the name of the request parameter that will contain the 
	 * resource path.
	 */
	public void setPathParameter(String pathParameter) {
		this.pathParameter = pathParameter;
	}
	
    public void setFileTypeMap(FileTypeMap fileTypeMap) {
		this.fileTypeMap = fileTypeMap;
	}

	public void setMappings(List<ResourceMapping> resourceMappings) {
		this.mappings = resourceMappings;
	}

	public void setFilters(List<ResourceFilter> filters) {
		this.filters = filters;
	}

	/**
	 * Sets whether the controller check for file modifications.
	 */
	public void setCheckForModifications(boolean checkForModifications) {
		this.checkForModifications = checkForModifications;
	}
	
	protected Resource lookupResource(String path) throws IOException {
		Iterator<ResourceMapping> it = mappings.iterator();
    	while (it.hasNext()) {
    		ResourceMapping mapping = it.next();
			Resource res = mapping.getResource(path);
			if (res != null) {
				return res;
			}
    	}
    	return null;
	}
	
	protected String getContentType(Resource resource) {
		if (resource == null) {
			return null;
		}
		return fileTypeMap.getContentType(resource.getFilename());
	}
	
	protected String getResourcePath(HttpServletRequest request) {
		if (pathAttribute != null) {
    		return "/" + request.getAttribute(pathAttribute); 
    	}
		else if (pathParameter != null) {
			return "/" + request.getParameter(pathParameter);
		}
   		return request.getPathInfo();
	}
	
	public long getLastModified(HttpServletRequest request) {
		if (checkForModifications) {
			String path = getResourcePath(request);
			long mtime = getLastModified(path);
			return mtime >= 0 ? mtime : lastModified;
		}
		return lastModified;
	}
	
	protected long getLastModified(String path) {
		try {
			Resource res = lookupResource(path);
			if (res != null) {
				return res.getFile().lastModified();
			}
		}
		catch (IOException e) {
		}
		return -1;
	}
	
	public long getTimeToLive() {
		return checkForModifications ? 0 : CACHE_ETERNALLY;
	}
	
	protected String getCacheKeyInternal(HttpServletRequest request) {
		StringBuffer key = new StringBuffer();
		key.append(getResourcePath(request));
		String lang = request.getParameter("lang");
		if (lang != null) {
			key.append(';').append(lang);
		}
		appendCacheKey(key, request);
		return key.toString();
	}
	
	protected boolean contentTypeShouldBeZipped(String contentType) {
		return contentType != null && ( 
				contentType.equals("text/css") || 
				contentType.equals("text/javascript"));
	}

	public boolean gzipResponse(HttpServletRequest request) {
		try {
			Resource resource = lookupResource(getResourcePath(request));
			return contentTypeShouldBeZipped(getContentType(resource));
		}
		catch (IOException e) {
			return false;
		}
	}
	
	public ModelAndView handleRequest(HttpServletRequest request, 
            HttpServletResponse response) throws IOException {
        
    	String path = getResourcePath(request);
   		ServletUtils.setFarFutureExpiresHeader(response);
    	if (!serveResource(path, request, response)) {
    		response.sendError(HttpServletResponse.SC_NOT_FOUND);	
    	}
    	return null;
	}
	
	protected boolean serveResource(String path, HttpServletRequest request, 
			HttpServletResponse response) throws IOException {
		
    	Resource res = lookupResource(path);
		if (res != null) {
			String contentType = getContentType(res);
			response.setContentType(contentType);
			if (contentType.startsWith("text/")) {
				serveText(res, path, contentType, request, response.getWriter());
			}
			else {
				serveBinary(res, contentType, response.getOutputStream());
			}
			return true;
		}
		return false;
	}
	
	protected void serveText(Resource res, String path, String contentType,
			HttpServletRequest request, Writer out)	throws IOException {
		
		log.debug("Serving text resource: " + path);
		
		Reader in = getReader(res, path, contentType, request);
		IOUtils.serve(in, out);
	}

	protected Reader getReader(Resource res, String path, String contentType,
			HttpServletRequest request) throws IOException {
		
		Reader in = new InputStreamReader(res.getInputStream(), "UTF-8");
		if (filters != null) {
			Iterator<ResourceFilter> it = filters.iterator();
			while (it.hasNext()) {
				ResourceFilter filter = it.next();
				if (filter.matches(path)) {
					log.debug("Filter " + filter + " matches.");
					in = filter.createFilterReader(in, request);
					break;
				}
				log.debug("Filter " + filter + " does not match.");
			}
		}
		return in;
	}
	
	protected void serveBinary(Resource res, String contentType, 
			OutputStream out) throws IOException {
		
		IOUtils.serve(res.getInputStream(), out);
	}

}
