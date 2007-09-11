/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
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
    
	private long expiresAfter = FormatUtils.parseMillis("10Y");
	
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
