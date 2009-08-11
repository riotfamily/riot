package org.riotfamily.pages.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.view.MacroHelperFactory;
import org.riotfamily.pages.mapping.PageResolver;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class CurrentPageMacroHelperFactory implements MacroHelperFactory {

	private PageResolver pageResolver;
	
	public CurrentPageMacroHelperFactory(PageResolver pageResolver) {
		this.pageResolver = pageResolver;
	}

	public Object createMacroHelper(HttpServletRequest request,
			HttpServletResponse response, Map<String, ?> model) {

		return pageResolver.getPage(request);
	}
}
