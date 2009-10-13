/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
