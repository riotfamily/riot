package org.riotfamily.pages.i18n;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.util.ServletMappingHelper;
import org.springframework.util.Assert;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * Abstract base class for {@link org.springframework.web.servlet.LocaleResolver
 * LocaleResolvers} that resolve the locale from the requested path. 
 */
public abstract class AbstractPathLocaleResolver implements LocaleResolver {
	
	private Log log = LogFactory.getLog(AbstractPathLocaleResolver.class);
	
	private LocaleResolver defaultResolver = new AcceptHeaderLocaleResolver();
	
	private ServletMappingHelper servletMappingHelper =
			new ServletMappingHelper(true);

	
	public void setDefaultResolver(LocaleResolver defaultResolver) {
		Assert.notNull(defaultResolver);
		this.defaultResolver = defaultResolver;
	}

	public final Locale resolveLocale(HttpServletRequest request) {
		String path = servletMappingHelper.getLookupPathForRequest(request);
		Locale locale = resolveLocaleForPath(path);
		log.debug("Locale for path [" + path + "] is: " + locale);
		if (locale == null) {
			locale = defaultResolver.resolveLocale(request);
		}
		return locale;
	}
	
	protected Locale resolveLocaleForPath(String path) {
		String language = resolveLanguage(path);
		if (language == null) {
			return null;
		}
		String country = resolveCountry(path);
		if (country == null) {
			return new Locale(language);
		}
		String variant = resolveVariant(path);
		if (variant == null) {
			return new Locale(language, country);
		}
		return new Locale(language, country, variant);
	}
	
	protected abstract String resolveLanguage(String path);

	protected String resolveCountry(String path) {
		return null;
	}
	
	protected String resolveVariant(String path) {
		return null;
	}

	public void setLocale(HttpServletRequest request, 
			HttpServletResponse response, Locale locale) {
		
		defaultResolver.setLocale(request, response, locale);
	}
	

}
