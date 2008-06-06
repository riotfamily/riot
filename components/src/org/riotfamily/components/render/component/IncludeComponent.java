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
package org.riotfamily.components.render.component;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.components.model.Component;
import org.springframework.web.util.WebUtils;

/**
 * Component implementation that uses a RequestDispatcher to perform the
 * rendering. The configured url will be included and the properties of
 * the ComponentVersion that is to be rendered will be exposed as request
 * attributes.
 */
public class IncludeComponent extends AbstractComponent {

	private String uri;

	private boolean dynamic = true;

	public void setUri(String uri) {
		this.uri = uri;
	}

	protected void renderInternal(Component component, boolean preview,
			int position, int listSize, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Map<String, Object> snapshot = ServletUtils.takeAttributesSnapshot(request);
		WebUtils.exposeRequestAttributes(request, component.unwrapValues());
		
		request.setAttribute(THIS, component);
		request.setAttribute(POSITION, new Integer(position));
		request.setAttribute(LIST_SIZE, new Integer(listSize));

		request.getRequestDispatcher(uri).include(request, response);
		ServletUtils.restoreAttributes(request, snapshot);
	}

	public boolean isDynamic() {
		return this.dynamic;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

}
