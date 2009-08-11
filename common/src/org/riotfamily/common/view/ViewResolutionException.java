package org.riotfamily.common.view;

import org.springframework.core.NestedRuntimeException;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ViewResolutionException extends NestedRuntimeException {

	public ViewResolutionException(String viewName, Throwable cause) {
		super("Error resolving viewName [" + viewName + ']', cause);
	}

	public ViewResolutionException(String viewName) {
		this(viewName, null);
	}

}
