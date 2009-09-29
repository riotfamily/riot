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
package org.riotfamily.common.web.mvc.mapping;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheContext;
import org.riotfamily.common.io.IOUtils;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.web.support.ServletUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.LastModified;
import org.springframework.web.util.WebUtils;

/**
 * HandlerMapping that serves static resources from the application directory.
 * <p>
 * For security reasons no resources under WEB-INF or outside of the 
 * application directory will be served.
 * </p> 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class ResourceHandlerMapping extends AbstractHandlerMapping {

	public static final Pattern FORBIDDEN = Pattern.compile(
			"(WEB-INF|META-INF|\\.\\.)", Pattern.CASE_INSENSITIVE);
	
	private FileTypeMap fileTypeMap;
	
	public void setFileTypeMap(FileTypeMap fileTypeMap) {
		this.fileTypeMap = fileTypeMap;
	}

	protected Object getHandlerInternal(HttpServletRequest request)
			throws Exception {
		
		String path = ServletUtils.getPathWithinApplication(request);
		if (StringUtils.hasLength(FormatUtils.getExtension(path))) {
			path = StringUtils.cleanPath(path);
			Resource res = new ServletContextResource(getServletContext(), path);
			if (res.exists()) {
				if (!FORBIDDEN.matcher(path).find()) {
					String contentType = null;
					if (fileTypeMap != null) {
						contentType = fileTypeMap.getContentType(path);
					}
					return new ServeResourceController(res, contentType);
				}
			}
		}
		return null;
	}
	
	private static class ServeResourceController 
			implements Controller, LastModified {
		
		private static final long STARTUP_TIME = System.currentTimeMillis();
		
		private Resource res;
		
		private File file;
		
		private String contentType;
		
		public ServeResourceController(Resource res, String contentType) {
			this.res = res;
			this.contentType = contentType;
			try {
				file = res.getFile();
			}
			catch (IOException ex) {
			}
		}

		public long getLastModified(HttpServletRequest request) {
			if (file != null) {
				return file.lastModified();
			}
			return STARTUP_TIME;
		}
		
		public ModelAndView handleRequest(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			
			if (contentType != null) {
				response.setContentType(contentType);
			}
			if (file != null && WebUtils.isIncludeRequest(request)) {
				CacheContext.addFile(file);
			}
			IOUtils.serve(res.getInputStream(), response.getOutputStream());
			return null;
		}
		
	}
}
