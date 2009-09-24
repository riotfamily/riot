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
package org.riotfamily.pages.riot.ui;

import org.riotfamily.components.model.ContentContainerOwner;
import org.riotfamily.components.riot.ui.PublishStatusRenderer;
import org.riotfamily.core.screen.list.ListRenderContext;
import org.riotfamily.pages.model.ContentPage;
import org.riotfamily.pages.model.Site;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PagePublishStatusRenderer extends PublishStatusRenderer {

	protected String getStyleClass(ContentContainerOwner owner, ListRenderContext context) {
		if (isTranslated((ContentPage) owner, context)) {
			return super.getStyleClass(owner, context);
		}
		return "translatable";
	}

	private boolean isTranslated(ContentPage page, ListRenderContext context) {
		Site parentSite = getParentSite(context);
		return parentSite == null || parentSite.equals(page.getSite());
	}
	
	private Site getParentSite(ListRenderContext context) {
		Object parent = context.getParent();
		if (parent instanceof ContentPage) {
			return ((ContentPage) parent).getSite();
		}
		else if (parent instanceof Site) {
			return (Site) parent;
		}
		return null;
	}

}
