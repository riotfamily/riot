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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.components.model.Component;

/**
 * Abstract base class for component implementations.
 */
public abstract class AbstractComponent implements ComponentRenderer {

	public static final String CONTAINER = AbstractComponent.class.getName()
			+ ".container";

	public static final String COMPONENT_ID = "componentId";

	public static final String THIS = "this";
	
	public static final String PARENT = "parent";

	public static final String POSITION = "position";
	
	public static final String LIST_SIZE = "listSize";

	protected Log log = LogFactory.getLog(AbstractComponent.class);

	private Map<String, Object> defaults;
	
	public void setDefaults(Map<String, Object> defaults) {
		this.defaults = defaults;
	}
	
	public Map<String, Object> getDefaults() {
		return defaults;
	}
	
	public final void render(Component component, boolean preview,
			int position, int listSize, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		Object outerContainer = request.getAttribute(CONTAINER);
		request.setAttribute(CONTAINER, component);
		try {
			renderInternal(component, preview, position, listSize, 
					request, response);
		}
		catch (Exception e) {
			log.error("Error rendering component", e);

			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			StringWriter strace = new StringWriter();
			e.printStackTrace(new PrintWriter(strace));

			TagWriter pre = new TagWriter(response.getWriter());
			pre.start("pre")
					.attribute("class", "riot-stacktrace")
					.body(strace.toString());

			pre.end();
		}
		request.setAttribute(CONTAINER, outerContainer);
	}

	public boolean isDynamic() {
		return false;
	}

	protected abstract void renderInternal(Component component, boolean preview,
			int position, int listSize, HttpServletRequest request,
			HttpServletResponse response) throws Exception;
	
}
