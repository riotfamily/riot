package org.riotfamily.pages.mvc.hibernate;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

/**
 * ParameterResolver that returns the current date. Instead you may also use
 * Hibernate's <code>current_date()</code> HQL function.
 */
public class CurrentDateResolver extends AbstractParameterResolver {

	protected Object getValueInternal(HttpServletRequest request) {
		return new Date();
	}
	
	public final boolean includeInCacheKey() {
		return false;
	}

}
