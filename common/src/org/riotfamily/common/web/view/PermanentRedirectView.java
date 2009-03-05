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
package org.riotfamily.common.web.view;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.web.servlet.view.RedirectView;

/**
 * View that sends a redirect to the originating request URI.
 *  
 * @see ServletUtils#getOriginatingRequestUri(HttpServletRequest)
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 6.6
 */
public class PermanentRedirectView extends RedirectView {

	public PermanentRedirectView(String url) {
		super(url);
	}
	
	public PermanentRedirectView(String url, boolean contextRelative) {
		super(url, contextRelative);
	}

	public PermanentRedirectView(String url, boolean contextRelative, boolean exposeModelAttributes) {
		super(url, contextRelative);
		super.setExposeModelAttributes(exposeModelAttributes);
	}

	protected void sendRedirect(HttpServletRequest request,
			HttpServletResponse response, String targetUrl,
			boolean http10Compatible) throws IOException {

		if (isGetOrHeadRequest(request)) {
			// send status code 301 for GET or HEAD requests only
			response.setStatus(301);
			response.setHeader("Location", response.encodeRedirectURL(targetUrl));
		}
		else {
			super.sendRedirect(request, response, targetUrl, http10Compatible);
		}
	}
	
	private boolean isGetOrHeadRequest(HttpServletRequest request) {
		String method = request.getMethod();
		if ("GET".equals(method) || "HEAD".equals(method)) {
			return true;
		}
		return false;
	}

}
