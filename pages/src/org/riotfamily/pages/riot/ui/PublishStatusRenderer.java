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
package org.riotfamily.pages.riot.ui;

import java.io.PrintWriter;

import org.riotfamily.common.ui.ObjectRenderer;
import org.riotfamily.common.ui.RenderContext;
import org.riotfamily.core.screen.list.ListRenderContext;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PublishStatusRenderer implements ObjectRenderer {

	public void render(Object obj, RenderContext context, PrintWriter writer) {
		writer.print("<div class=\"publish-status publish-status-");
		writer.print(getStyleClass((Page) obj, (ListRenderContext) context));
		writer.print("\"></div>");
	}
	
	private String getStyleClass(Page page, ListRenderContext context) {
		if (isTranslated(page, context)) {
			if (!page.isPublished()) {
				return "new";
			}
			if (page.isDirty()) {
				return "dirty";
			}
			return "published";
		}
		return "translatable";
	}

	private boolean isTranslated(Page page, ListRenderContext context) {
		Site parentSite = getParentSite(context);
		return parentSite == null || parentSite.equals(page.getSite());
	}
	
	private Site getParentSite(ListRenderContext context) {
		Object parent = context.getParent();
		if (parent instanceof Page) {
			return ((Page) parent).getSite();
		}
		else if (parent instanceof Site) {
			return (Site) parent;
		}
		return null;
	}

}
