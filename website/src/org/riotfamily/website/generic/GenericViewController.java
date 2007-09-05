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
 *   Carsten Woelk [cwoelk at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.generic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.spring.AbstractCacheableController;
import org.riotfamily.cachius.spring.CacheableController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Class similar to Spring's {@link org.springframework.web.servlet.mvc.ParameterizableViewController}, 
 * but with the following differences:
 * <ul>
 *   <li>
 *     Setting a viewName is not required. If omitted Spring's 
 *     {@link org.springframework.web.servlet.RequestToViewNameTranslator} 
 *     will be used.
 *   </li>
 *   <li>
 *     A Content-Type may be set.
 *   </li>
 *   <li>
 *     Implements the {@link CacheableController} interface. The output will
 *     be cached eternally unless caching is {@link #setCache(boolean) disabled}.
 *   </li>
 * </ul> 
 * 
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 6.5
 */
public class GenericViewController extends AbstractCacheableController {

	private String viewName;

	private String contentType;

	private boolean cache = true;

	/**
	 * Sets the name of the view to delegate to.
	 */
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	/**
	 * Returns the name of the view to delegate to.
	 */
	public String getViewName() {
		return this.viewName;
	}

	/**
	 * Returns the content type to be set in the header.
	 */
	public String getContentType() {
		return this.contentType;
	}

	/**
	 * Sets the content type to be set in the header.
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	/**
	 * Sets whether the output should be cached. If <code>true</code> (default)
	 * the output is cached eternally.
	 */
	public void setCache(boolean cache) {
		this.cache = cache;
	}

	public long getTimeToLive() {
		return CACHE_ETERNALLY;
	}
	
	protected boolean bypassCache(HttpServletRequest request) {
		return !cache;
	}
	
	/**
	 * If configured sets the content on the request and returns a {@link ModelAndView}
	 * object with the specified view name if configured. If none has been set
	 * Spring will use its configured viewNameTranslator to resolve the view name.
	 */
	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		if (contentType != null) {
			response.setContentType(contentType);
		}

		return new ModelAndView(getViewName());
	}

}
