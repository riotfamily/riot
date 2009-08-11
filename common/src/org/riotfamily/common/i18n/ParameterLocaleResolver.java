package org.riotfamily.common.i18n;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ParameterLocaleResolver extends ChainedLocaleResolver {

	private String localeParameter = "locale";
	
	public void setLocaleParameter(String localeParameter) {
		this.localeParameter = localeParameter;
	}

	protected Locale resolveLocaleInternal(HttpServletRequest request) {
		String localeString = request.getParameter(localeParameter);
		if (localeString != null) {
			return StringUtils.parseLocaleString(localeString);
		}
		return null;
	}
	
}
