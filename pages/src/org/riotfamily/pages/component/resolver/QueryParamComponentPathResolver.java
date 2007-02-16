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
package org.riotfamily.pages.component.resolver;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class QueryParamComponentPathResolver extends UrlComponentPathResolver {

	private List params;

	private String componentPathPrefix;

	public String getComponentPath(HttpServletRequest request) {

		StringBuffer path;
		if (componentPathPrefix != null) {
			path = new StringBuffer(componentPathPrefix);
		}
		else {
			path = new StringBuffer(super.getComponentPath(request));
			path.append('#');
		}

		Iterator it = params.iterator();
		while (it.hasNext()) {
			String paramValue = request.getParameter((String) it.next());
			path.append(':').append(paramValue);
			if (it.hasNext()) {
				path.append(',');
			}
		}

		return path.toString();
	}

	public String getParentPath(String path) {
		if (componentPathPrefix == null) {
			return super.getParentPath(path);
		}
		return null;
	}

	public List getParams() {
		return params;
	}

	public void setParams(List params) {
		this.params = params;
	}

	public void setComponentPathPrefix(String componentPathPrefix) {
		this.componentPathPrefix = componentPathPrefix;
	}

}
