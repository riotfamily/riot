package org.riotfamily.common.i18n;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public abstract class ChainedLocaleResolver implements LocaleResolver {

	private LocaleResolver fallbackResolver = new AcceptHeaderLocaleResolver();
	
	public void setFallbackResolver(LocaleResolver fallbackResolver) {
		Assert.notNull(fallbackResolver, "Fallback LocaleResolver must not be null");
		this.fallbackResolver = fallbackResolver;
	}

	public final Locale resolveLocale(HttpServletRequest request) {
		Locale locale = resolveLocaleInternal(request);
		if (locale == null) {
			locale = fallbackResolver.resolveLocale(request);
		}
		return locale;
	}
	
	protected abstract Locale resolveLocaleInternal(HttpServletRequest request);
	
	public final void setLocale(HttpServletRequest request, 
			HttpServletResponse response, Locale locale) {
		
		if (!setLocaleInternal(request, response, locale)) {
			fallbackResolver.setLocale(request, response, locale);
		}
	}
	
	protected boolean setLocaleInternal(HttpServletRequest request, 
			HttpServletResponse response, Locale locale) {
	
		return false;
	}
}
