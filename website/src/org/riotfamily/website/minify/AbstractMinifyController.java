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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.minify;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.spring.AbstractCacheableController;
import org.riotfamily.cachius.spring.CacheableController;
import org.riotfamily.cachius.spring.Compressible;
import org.riotfamily.common.web.compressor.Compressor;
import org.riotfamily.common.web.util.CapturingResponseWrapper;
import org.riotfamily.common.web.util.ServletUtils;
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
	
	private boolean developmentMode;

	private long startUpTime = System.currentTimeMillis();
	
	/**
	 * If set to <code>true</code>, the output will not be cached, 
	 * not compressed or obfuscated and no expires header will be sent. 
	 */
	public void setDevelopmentMode(boolean developmentMode) {
		this.developmentMode = developmentMode;
	}
	
	/**
	 * Returns the server start-up time, or the current time if running in
	 * {@link #setDevelopmentMode(boolean) development mode}.
	 */
	public long getLastModified(HttpServletRequest request) {
		return developmentMode ? System.currentTimeMillis() : startUpTime;
	}

	/**
	 * The cache is bypassed in {@link #setDevelopmentMode(boolean) development mode}.
	 */
	protected boolean bypassCache(HttpServletRequest request) {
		return developmentMode;
	}

	/**
	 * Adds the query-string to the cache-key. 
	 */
	protected void appendCacheKey(StringBuffer key, HttpServletRequest request) {
		key.append(request.getQueryString());
	}
	
	/**
	 * Returns {@link CacheableController#CACHE_ETERNALLY CACHE_ETERNALLY} to
	 * request eternal caching.
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
				CapturingResponseWrapper wrapper = new CapturingResponseWrapper(response, buffer);
				request.getRequestDispatcher(path).include(request, wrapper);
				wrapper.flush();
				buffer.write('\n');		
			}
			
			String contentType = getContentType();
			if (contentType != null) {
				response.setContentType(contentType);
			}
			
			if (!developmentMode) {
				ServletUtils.setFarFutureExpiresHeader(response);
				Compressor compressor = getCompressor();
				if (compressor != null) {
					Reader in = new InputStreamReader(new ByteArrayInputStream(
							buffer.toByteArray()), response.getCharacterEncoding());
					
					compressor.compress(in, response.getWriter());
					return null;
				}
			}
			FileCopyUtils.copy(buffer.toByteArray(), response.getOutputStream());
		}
		return null;
	}

	protected abstract String getContentType();
	
	protected abstract Compressor getCompressor();

}
