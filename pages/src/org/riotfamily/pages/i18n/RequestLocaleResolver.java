package org.riotfamily.pages.i18n;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;
import org.springframework.web.servlet.LocaleResolver;

/**
 * Resolves the locale previously exported by an 
 * {@link org.riotfamily.pages.i18n.LocaleExporter LocaleExporter}.
 * 
 * Use this class if you are working with two DispatcherServlets, one for
 * toplevel (public) requests and one for includes.
 */
public class RequestLocaleResolver implements LocaleResolver {
	
	private Locale defaultLocale = Locale.getDefault();
	
	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public Locale resolveLocale(HttpServletRequest request) {
		Locale locale = LocaleExporter.getLocale(request);
		if (locale == null) {
			return defaultLocale;
		}
		return locale;
	}
	
	public void setLocale(HttpServletRequest request, 
			HttpServletResponse response, Locale locale) {
		
		LocaleResolver resolver = LocaleExporter.getLocaleResolver(request);
		Assert.notNull(resolver, "No LocaleResolver found in request. " +
				"Make sure to use a LocaleExporter.");
		
		resolver.setLocale(request, response, locale);
	}
	
}
