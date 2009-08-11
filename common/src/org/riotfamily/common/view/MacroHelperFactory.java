package org.riotfamily.common.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public interface MacroHelperFactory {

	public Object createMacroHelper(HttpServletRequest request, 
			HttpServletResponse response, Map<String, ?> model);
}
