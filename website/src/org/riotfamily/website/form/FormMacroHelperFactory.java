package org.riotfamily.website.form;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.view.MacroHelperFactory;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class FormMacroHelperFactory implements MacroHelperFactory {

	private static final String INSTANCE_ATTRIBUTE = FormMacroHelper.class.getName();

	/**
	 * Creates a {@link FormMacroHelper}. The helper is stored as request 
	 * attribute so that components can access the same instance. 
	 */
	public Object createMacroHelper(HttpServletRequest request, 
			HttpServletResponse response, Map<String, ?> model) {
	
		FormMacroHelper helper = (FormMacroHelper) request.getAttribute(INSTANCE_ATTRIBUTE);
		if (helper == null) {
			helper = new FormMacroHelper();
			request.setAttribute(INSTANCE_ATTRIBUTE, helper);
		}
		return helper;
	}

}
