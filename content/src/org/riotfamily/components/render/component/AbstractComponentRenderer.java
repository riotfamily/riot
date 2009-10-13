/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.components.render.component;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.TagWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.riotfamily.components.model.Component;

/**
 * Abstract base class for component implementations.
 */
public abstract class AbstractComponentRenderer implements ComponentRenderer {

	public static final String COMPONENT_ID = "componentId";

	public static final String THIS = "this";
	
	public static final String PARENT = "parent";

	public static final String POSITION = "position";
	
	public static final String LIST_SIZE = "listSize";

	protected Logger log = LoggerFactory.getLogger(AbstractComponentRenderer.class);

	public final void render(Component component, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		try {
			renderInternal(component, request, response);
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
	}

	protected abstract void renderInternal(Component component,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception;
	
}
