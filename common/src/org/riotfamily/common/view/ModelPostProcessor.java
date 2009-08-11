package org.riotfamily.common.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface to modify the model after it has been populated by the 
 * controller and before it is passed to the view.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public interface ModelPostProcessor {

	public void postProcess(Map<String, Object> model, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception;
	
}
