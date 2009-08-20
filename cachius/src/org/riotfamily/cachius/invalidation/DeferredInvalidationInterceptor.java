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
 *   Alf Werder [alf dot werder at artundweise dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.cachius.invalidation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheService;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * A {@link HandlerInterceptor} that defers cache invalidation to the
 * end of the handler execution chain.
 * 
 * @author Alf Werder [alf dot werder at artundweise dot de]
 * @since 8.0.1
 */
public class DeferredInvalidationInterceptor extends HandlerInterceptorAdapter {
	private CacheService cacheService;
	
	@Override
	public boolean preHandle(HttpServletRequest request,
		HttpServletResponse response, Object handler) throws Exception {
		
		cacheService.beginLocallyDeferredInvalidation();
		
		return true;
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request,
		HttpServletResponse response, Object handler, Exception exception)
		throws Exception {
		
		cacheService.commitLocallyDeferredInvalidation();
	}
	
	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}
}
