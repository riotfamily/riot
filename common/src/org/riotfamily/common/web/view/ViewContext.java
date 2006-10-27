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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class ViewContext {

	private static ThreadLocal threadLocal = new ThreadLocal();
	
	private ViewContext() {
	}
		
	public static void execute(HttpServletRequest request, 
			HttpServletResponse response, Callback callback) 
			throws Exception {
		
		RequestAndResponse rar = (RequestAndResponse) threadLocal.get();
		if (rar == null) {
			threadLocal.set(new RequestAndResponse(request, response)); 
		}
		try {
			callback.doInContext();
		}
		finally {
			if (rar == null) {
				threadLocal.set(null);	
			}
		}
	}
	
	private static RequestAndResponse getRequestAndResponse() {
		RequestAndResponse rar = (RequestAndResponse) threadLocal.get();
		return rar;
	}
	
	public static HttpServletRequest getRequest() {
		return getRequestAndResponse().getRequest();
	}
	
	public static HttpServletResponse getResponse() {
		return getRequestAndResponse().getResponse();
	}
	
	public interface Callback {
	
		public void doInContext() throws Exception;
		
	}
	
	private static class RequestAndResponse {
		
		private HttpServletRequest request;
		
		private HttpServletResponse response;
		
		private RequestAndResponse(HttpServletRequest request, 
				HttpServletResponse response) {

			this.request = request;
			this.response = response;
		}

		public HttpServletRequest getRequest() {
			return this.request;
		}

		public HttpServletResponse getResponse() {
			return this.response;
		}
		
	}

}
