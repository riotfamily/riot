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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheService;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * A {@link Filter} that defers cache invalidation to the end of the filter
 * chain.
 * 
 * @author Alf Werder [alf dot werder at artundweise dot de]
 * @since 8.0.1
 */
public class DeferredInvalidationFilter extends OncePerRequestFilter {
	private CacheService cacheService;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		
		try {
			cacheService.beginLocallyDeferredInvalidation();
			filterChain.doFilter(request, response);
		} finally {
			cacheService.commitLocallyDeferredInvalidation();
		}
	}
	
	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}
}
