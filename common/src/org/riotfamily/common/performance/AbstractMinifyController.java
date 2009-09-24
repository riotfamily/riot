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
package org.riotfamily.common.performance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.cache.AbstractCacheableController;
import org.riotfamily.common.cache.controller.Compressible;
import org.riotfamily.common.servlet.CapturingResponseWrapper;
import org.riotfamily.common.servlet.ServletUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.LastModified;

/**
 * Controller that concatenates resources and optionally compresses or 
 * obfuscates them. The result is cached by Cachius and gzipped when possible. 
 * <p>
 * The controller uses a {@link RequestDispatcher} to request the resources 
 * and captures them using a {@link CapturingResponseWrapper}, which allows us 
 * to include dynamic resources, too.
 * <p>
 * Note: This will not work if an included controller uses 
 * <code>request.getRequestURI()</code> to look up a resource. Please use 
 * {@link ServletUtils#getRequestUri(HttpServletRequest)} instead. 
 *    
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public abstract class AbstractMinifyController extends AbstractCacheableController 
		implements LastModified, Compressible {
	
	private long startUpTime = System.currentTimeMillis();
	
	/**
	 * Returns the server start-up time.
	 */
	public long getLastModified(HttpServletRequest request) {
		return startUpTime;
	}

	/**
	 * Adds the query-string to the cache-key. 
	 */
	protected void appendCacheKey(StringBuffer key, HttpServletRequest request) {
		key.append('?').append(request.getQueryString());
	}
	
	/**
	 * Returns <code>CACHE_ETERNALLY</code>.
	 */
	public long getTimeToLive() {
		return CACHE_ETERNALLY;
	}
	
	/**
	 * Always returns <code>true</code>.
	 */
	public boolean gzipResponse(HttpServletRequest request) {
		return true;
	}
	
	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		String s = request.getParameter("files");
		if (s != null) {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			String[] a = StringUtils.commaDelimitedListToStringArray(s);
			String lastAbsoultePath = null;
			for (int i = 0; i < a.length; i++) {
				String path = StringUtils.cleanPath(a[i]);
				if (path.startsWith("/")) {
					lastAbsoultePath = path;
				}
				else if (lastAbsoultePath != null) {
					path = StringUtils.applyRelativePath(lastAbsoultePath, path);
				}
				if (path.indexOf("/WEB-INF/") != -1) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
					return null;
				}
				capture(path, buffer, request, response);		
			}
			
			String contentType = getContentType();
			if (contentType != null) {
				response.setContentType(contentType);
			}
			
			ServletUtils.setFarFutureExpiresHeader(response);
			Compressor compressor = getCompressor();
			if (compressor != null) {
				Reader in = new InputStreamReader(new ByteArrayInputStream(
						buffer.toByteArray()), response.getCharacterEncoding());
				
				compressor.compress(in, response.getWriter());
				return null;
			}
			FileCopyUtils.copy(buffer.toByteArray(), response.getOutputStream());
		}
		return null;
	}

	protected void capture(String path, ByteArrayOutputStream buffer,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		CapturingResponseWrapper wrapper = new CapturingResponseWrapper(response, buffer);
		request.getRequestDispatcher(path).include(request, wrapper);
		wrapper.flush();
		buffer.write('\n');
	}

	protected abstract String getContentType();
	
	protected abstract Compressor getCompressor();

}
