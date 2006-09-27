package org.riotfamily.pages.i18n;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.LocaleResolver;

/**
 * Resolves the locale using another LocaleResolver and exports it as request
 * attribute for later retrieval by a {@link 
 * org.riotfamily.pages.i18n.RequestLocaleResolver RequestLocaleResolver}. 
 */
public class LocaleExporter implements LocaleResolver {

	public static final String LOCALE_ATTRIBUTE = 
			LocaleExporter.class.getName() + ".locale";
	
	public static final String LOCALE_RESOLVER_ATTRIBUTE = 
			LocaleExporter.class.getName() +  ".localeResolver";
	
	public static final String LANGUAGE_ATTRIBUTE = "language";
	
	private LocaleResolver localeResolver;

	public LocaleExporter(LocaleResolver resolver) {
		this.localeResolver = resolver;
	}

	public Locale resolveLocale(HttpServletRequest request) {
		Locale locale = getLocale(request);
		if (locale == null) {
			locale = localeResolver.resolveLocale(request);
			request.setAttribute(LOCALE_ATTRIBUTE, locale);
			request.setAttribute(LANGUAGE_ATTRIBUTE, locale.getLanguage());
			request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, localeResolver);
		}
		return locale;
	}

	public void setLocale(HttpServletRequest request, 
			HttpServletResponse response, Locale locale) {
		
		request.setAttribute(LOCALE_ATTRIBUTE, locale);
		localeResolver.setLocale(request, response, locale);
	}
	
	public static Locale getLocale(HttpServletRequest request) {
		return (Locale) request.getAttribute(LOCALE_ATTRIBUTE);
	}
	
	public static LocaleResolver getLocaleResolver(HttpServletRequest request) {
		return (LocaleResolver) request.getAttribute(LOCALE_RESOLVER_ATTRIBUTE);
	}

}
