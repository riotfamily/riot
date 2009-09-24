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
package org.riotfamily.components.riot.ui;

import java.io.PrintWriter;

import org.riotfamily.common.ui.ObjectRenderer;
import org.riotfamily.common.ui.RenderContext;
import org.riotfamily.components.model.ContentContainerOwner;
import org.riotfamily.core.screen.list.ListRenderContext;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PublishStatusRenderer implements ObjectRenderer {

	public void render(Object obj, RenderContext context, PrintWriter writer) {
		writer.print("<div class=\"publish-status publish-status-");
		writer.print(getStyleClass((ContentContainerOwner) obj, (ListRenderContext) context));
		writer.print("\"></div>");
	}
	
	protected String getStyleClass(ContentContainerOwner owner, ListRenderContext context) {
		if (!owner.isPublished()) {
			return "new";
		}
		if (owner.getContentContainer().getPreviewVersion().isDirty()) {
			return "dirty";
		}
		return "published";
	}

}
