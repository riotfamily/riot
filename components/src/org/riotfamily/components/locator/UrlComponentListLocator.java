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
package org.riotfamily.components.locator;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.util.PathCompleter;
import org.riotfamily.common.web.util.ServletUtils;

/**
 * ComponentListLocator that calls
 * {@link ServletUtils#getOriginatingPathWithoutServletMapping(HttpServletRequest)}
 * to determine the lookup-path for a given request.
 * <p>
 * Example: If the DispatcherServlet is mapped to <code>*.html</code> and
 * <code>/myapp</code> is the application's context path, the component-path
 * for a request to <code>/myapp/foo/bar.html</code> will be
 * <code>/foo/bar</code>.
 * </p>
 * @since 6.5
 */
public class UrlComponentListLocator extends AbstractComponentListLocator {

	public static final String TYPE_URL = "url";

	private PathCompleter pathCompleter;

	private String parameter;

	public UrlComponentListLocator(PathCompleter pathCompleter) {
		super(TYPE_URL);
		this.pathCompleter = pathCompleter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	protected String getPath(HttpServletRequest request) {
		String path = ServletUtils.getOriginatingPathWithoutServletMapping(request);
		if (parameter != null) {
			String value = request.getParameter(parameter);
			if (value != null) {
				StringBuffer sb = new StringBuffer(path);
				sb.append('?').append(parameter).append('=').append(value);
				return sb.toString();
			}
		}
		return path;
	}

	/**
	 * Returns a substring of the given path starting at zero and ending before
	 * the last slash character. If no slash is found or the only slash is
	 * at the beginning of the path, <code>null</code> is returned.
	 */
	protected String getParentPath(String path) {
		int i = path.lastIndexOf('/');
		if (i > 0) {
			return path.substring(0, i);
		}
		return null;
	}

	protected String getUrlForPath(String path) {
		return pathCompleter.addServletMapping(path);
	}
}
