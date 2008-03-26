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
package org.riotfamily.cachius.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * HttpServletRequestWrapper that prevents the creation of new HTTP sessions.
 * An IllegalStateException is thrown when 
 * {@link #getSession()} or {@link #getSession(boolean) getSession(true)} is
 * invoked and no session existed at the time the wrapper was created.
 * <p>
 * <strong>Rationale:</strong> Cachius supports the caching of snippets that
 * use URL rewriting for session tracking. It therefore stores two versions
 * (one with rewritten URLs and one without) under different cache keys.
 * Hence Cachius must know whether URL rewriting is used <em>before</em> a
 * CacheItem is retrieved or created. This implies that cachable snippets must
 * not create new sessions.
 * 
 * Backport of the new (6.5+) cache to the 6.4 branch.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Alf Werder [alf dot werder at artundweise dot de]
 * @since 6.4.4
 */
public class SessionCreationPreventingRequestWrapper 
		extends HttpServletRequestWrapper {
	
	private boolean sessionExists;
	
	public SessionCreationPreventingRequestWrapper(HttpServletRequest request) {
		super(request);
		sessionExists = request.getSession(false) != null;
	}
	
	public HttpSession getSession() {
		return getSession(true);
	}
	
	public HttpSession getSession(boolean create) {
		if (create && !sessionExists) {
			throw new IllegalStateException("CacheableControllers must not " +
					"create new HTTP sessions. Make sure that the session " +
					"exists before you invoke the Controller.");
		}
		return super.getSession(create);
	}

}
