package org.riotfamily.pages.mvc.hibernate;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Returns the current locale.
 * @see RequestContextUtils#getLocale(javax.servlet.http.HttpServletRequest)
 */
public class CurrentLocaleResolver extends AbstractParameterResolver {

	protected Object getValueInternal(HttpServletRequest request) {
		return RequestContextUtils.getLocale(request);
	}

}
