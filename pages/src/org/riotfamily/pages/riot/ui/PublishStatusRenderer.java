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
