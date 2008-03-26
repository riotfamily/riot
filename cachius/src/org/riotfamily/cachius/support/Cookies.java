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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Backport of the new (6.5) cache to the 6.4 branch.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Alf Werder [alf dot werder at artundweise dot de]
 * @since 6.4.4
 */
public class Cookies implements Serializable {

	private ArrayList cookies;
	
	public void add(Cookie cookie) {
		if (cookies == null) {
			cookies = new ArrayList();
		}
		cookies.add(new SerializableCookie(cookie));
	}
	
	public void addToResponse(HttpServletResponse response) {
		if (cookies != null) {
			Iterator it = cookies.iterator();
			while (it.hasNext()) {
				SerializableCookie cookie = (SerializableCookie) it.next();
				response.addCookie(cookie.create());
			}
		}
	}
}
