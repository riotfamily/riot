package org.riotfamily.pages.menu;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

public interface MenuBuilder {

	/**
	 * Returns a timestamp indicating when the menu structure for the given
	 * request has changed for the last time. This information is used for
	 * caching purposes.
	 */
	public long getLastModified(HttpServletRequest request);
	
	/**
	 * Returns a list of {@link MenuItem MenuItem MenuItems} for the 
	 * given request.
	 */
	public List buildMenu(HttpServletRequest request);

}
