/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   flx
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.view.freemarker;

import java.util.Locale;
import java.util.Map;

import org.springframework.core.GenericCollectionTypeResolver;
import org.springframework.core.JdkVersion;

import freemarker.core.Environment;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * ObjectWrapper that supports Maps containing {@link Locale} keys. When the
 * map is to be wrapped, the entry for the current locale is returned instead
 * of the map itself.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class LocalizedObjectWrapper extends DefaultObjectWrapper {

	private boolean exact = false;

	private Locale fallbackLocale = null;

	/**
	 * Sets whether the current locale must exactly match the key. If set to
	 * <code>false</code> (which is the default), a second lookup is performed
	 * with the language only in case no entry can be found.
	 */
	public void setExact(boolean exact) {
		this.exact = exact;
	}

	/**
	 * Sets a fallback locale that is used in case no entry is found for the
	 * current locale. Default is <code>null</code>.
	 */
	public void setFallbackLocale(Locale fallbackLocale) {
		this.fallbackLocale = fallbackLocale;
	}

	public TemplateModel wrap(Object obj) throws TemplateModelException {
		if (obj instanceof Map) {
			Map map = (Map) obj;
			if (hasLocaleKey(map)) {
				return wrapLocalizedEntry(map);
			}
		}
		return super.wrap(obj);
	}

	private boolean hasLocaleKey(Map map) {
		if (JdkVersion.isAtLeastJava15()) {
			Class keyType = GenericCollectionTypeResolver.getMapKeyType(
					map.getClass());

			if (keyType != null) {
				return keyType.isAssignableFrom(Locale.class);
			}
		}
		if (!map.isEmpty()) {
			Object firstKey = map.keySet().iterator().next();
			return firstKey instanceof Locale;
		}
		return false;
	}

	private TemplateModel wrapLocalizedEntry(Map map)
			throws TemplateModelException {

		Environment env = Environment.getCurrentEnvironment();
		Locale locale = env.getLocale();
		return super.wrap(getLocalizedEntry(map, locale));
	}

	private Object getLocalizedEntry(Map map, Locale locale) {
		Object value = map.get(locale);
		if (value == null && !exact) {
			Locale lang = new Locale(locale.getLanguage());
			value = map.get(lang);
		}
		if (value == null && fallbackLocale != null
				&& !fallbackLocale.equals(locale)) {

			value = getLocalizedEntry(map, fallbackLocale);
		}
		return value;
	}

}
