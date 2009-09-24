/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import org.riotfamily.common.io.IOUtils;
import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.common.util.RiotLog;
import org.riotfamily.website.cache.AbstractCacheableController;
import org.riotfamily.website.cache.controller.Compressible;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller that serves an internal resource.
 * <p>
 * Note: This will only work when a prefix mapping is used for the 
 * DispatcherServlet (like <tt>/riot/*</tt>) since 
 * <code>request.getPathInfo()</code> is used.
 * </p>
 */
public abstract class AbstractResourceController extends AbstractCacheableController
		implements Compressible {

	private RiotLog log = RiotLog.get(AbstractResourceController.class);
	
	private FileTypeMap fileTypeMap = FileTypeMap.getDefaultFileTypeMap();
	
    private List<ResourceMapping> mappings;
    
    private List<ResourceFilter> filters;
    
	private boolean checkForModifications = false;
		        
	
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
	
	protected abstract String getResourcePath(HttpServletRequest request);
		
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
