package org.riotfamily.core.resource;

import java.io.FilterReader;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface that allows to filter resources that are served by a 
 * ResourceController.
 * 
 * @see org.riotfamily.core.resource.ResourceController#setFilters
 */
public interface ResourceFilter {

	/**
	 * Returns whether the filter should be applied to the resource denoted
	 * by the given path.
	 */
	public boolean matches(String path);
	
	/**
	 * Returns a FilterReader that does the actual filtering.
	 */
	public FilterReader createFilterReader(Reader in, HttpServletRequest request);

}
