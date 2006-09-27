package org.riotfamily.pages.component.resolver;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.template.TemplateController;

/**
 * ComponentKeyResolver that assumes that the ComponentListController is
 * placed within a template using a {@link TemplateController}.
 */
public class TemplateComponentKeyResolver implements ComponentKeyResolver {

	/**
	 * Returns the slot-path for the given request.
	 * @see TemplateController#getSlotPath(HttpServletRequest)
	 */
	public String getComponentKey(HttpServletRequest request) {
		return TemplateController.getSlotPath(request);
	}

}
