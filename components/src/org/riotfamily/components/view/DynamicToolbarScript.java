package org.riotfamily.components.view;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public interface DynamicToolbarScript {

	public String generateJavaScript(HttpServletRequest request);

}
