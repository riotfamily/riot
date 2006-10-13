package org.riotfamily.pages.mvc.hibernate;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.riot.security.AccessController;

/**
 * Returns the logged in Riot user.
 * @see AccessController#getPrincipal(HttpServletRequest)
 */
public class RiotPrincipalResolver extends AbstractParameterResolver {

	protected Object getValueInternal(HttpServletRequest request) {
		return AccessController.getPrincipal(request);
	}

}
