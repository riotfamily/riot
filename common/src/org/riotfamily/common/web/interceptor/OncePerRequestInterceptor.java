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
package org.riotfamily.common.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public abstract class OncePerRequestInterceptor extends PathMatchingInterceptor {

	private String counterAttribute = getClass().getName() + ".interceptions";

	protected final boolean doPreHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

		int interceptions = 0;
		Integer counter = (Integer) request.getAttribute(counterAttribute);
		if (counter != null) {
			interceptions = counter.intValue();
		}
		interceptions++;
		request.setAttribute(counterAttribute, new Integer(interceptions));

		if (interceptions == 1) {
			return preHandleOnce(request, response, handler);
		}
		
		return true;
	}

	protected boolean preHandleOnce(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		return true;
	}
	
	public final void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception exception)
			throws Exception {
		
		Integer counter = (Integer) request.getAttribute(counterAttribute);
		if (counter != null) {
			int interceptions = counter.intValue() - 1;
			request.setAttribute(counterAttribute, new Integer(interceptions));
			if (interceptions == 0) {
				afterLastCompletion(request, response, handler, exception);
			}
		}
	}

	protected void afterLastCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception exception)
			throws Exception {
	}

}
