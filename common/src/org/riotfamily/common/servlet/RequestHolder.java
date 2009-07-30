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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public final class RequestHolder {

	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	private int level;

	private static ThreadLocal<RequestHolder> threadLocal = 
			new ThreadLocal<RequestHolder>();
	
	private RequestHolder(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}
	
	public static void set(HttpServletRequest request,
			HttpServletResponse response) {
		
		RequestHolder holder = threadLocal.get();
		if (holder == null) {
			holder = new RequestHolder(request, response);
			threadLocal.set(holder);
		}
		holder.level++;
	}
	
	private static RequestHolder get() {
		RequestHolder holder = threadLocal.get();
		Assert.notNull(holder, "No RequestHolder bound to Thread.");
		return holder;
	}
	
	public static HttpServletRequest getRequest() {
		return get().request;
	}
	
	public static HttpServletResponse getResponse() {
		return get().response;
	}
	
	public static void unset() {
		RequestHolder holder = get();
		if (--holder.level == 0) {
			threadLocal.set(null);
		}
	}
}
