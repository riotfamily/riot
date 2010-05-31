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
package org.riotfamily.dbmsgsrc;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

import org.riotfamily.common.web.cache.tags.CacheTagUtils;
import org.riotfamily.dbmsgsrc.model.Message;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

public class DbMessageSource extends AbstractMessageSource {

	public static final String DEFAULT_BUNDLE = "default";
	
	private String bundle = DEFAULT_BUNDLE;
	
	private boolean fallbackToDefaultCountry = true;
	
	private boolean escapeSingleQuotes = true;

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}
	
	/**
	 * Whether the to use <i>&lt;lang&gt;_&lt;LANG&gt;</i> as first fallback 
	 * in case no message for <i>&lt;lang&gt;_&lt;COUNTRY&gt;</i> exists.
	 * If set to <code>false</code>, <i>&lt;lang&gt;</i> will be used. Default 
	 * is <code>true</code>.   
	 */
	public void setFallbackToDefaultCountry(boolean fallbackToDefaultCountry) {
		this.fallbackToDefaultCountry = fallbackToDefaultCountry;
	}

	/**
	 * Whether single quotes should be escaped before texts are passed to the
	 * {@link MessageFormat}. Default is <code>true</code>.
	 */
	public void setEscapeSingleQuotes(boolean escapeSingleQuotes) {
		this.escapeSingleQuotes = escapeSingleQuotes;
	}

	@Override
	@Transactional
	public String getMessage(MessageSourceResolvable resolvable, Locale locale)
				throws NoSuchMessageException {

		return super.getMessage(resolvable, locale);
	}

	@Override
	@Transactional
	public String getMessage(String code, Object[] args, Locale locale)
				throws NoSuchMessageException {

		return super.getMessage(code, args, locale);
	}

	@Override
	@Transactional
	public String getMessage(String code, Object[] args, String defaultMessage,
				Locale locale) {

		return super.getMessage(code, args, defaultMessage, locale);
	}

	MessageBundleEntry getEntry(final String code, final String defaultMessage) {
		MessageBundleEntry result = MessageBundleEntry.loadByBundleAndCode(bundle, code);
		if (result == null) {
			result = new MessageBundleEntry(bundle, code, defaultMessage);
			result.save();
		}
		return result;
	}
	
	@Override
	protected MessageFormat resolveCode(String code, Locale locale, String defaultMessage) {
		CacheTagUtils.tag(Message.class);
		MessageBundleEntry entry = getEntry(code, defaultMessage);
		Message message = getMessage(entry, locale);
		if (message != null) {
			return message.getMessageFormat(escapeSingleQuotes);
		}
		return null;
	}
	
	@Override
	protected String resolveCodeWithoutArguments(String code, Locale locale, String defaultMessage) {
		CacheTagUtils.tag(Message.class);
		MessageBundleEntry entry = getEntry(code, defaultMessage);
		Message message = getMessage(entry, locale);
		if (message != null) {
			return message.getText();
		}
		return null;
	}
	
	protected Message getMessage(MessageBundleEntry entry, Locale locale) {
		Map<Locale, Message> messages = entry.getMessages();
		if (messages == null) {
			return null;
		}
		Message message = null;
		while (message == null && locale != null) {
			message = messages.get(locale);
			locale = getFallbackLocale(locale);
		}
		return message;
	}
	
	/**
	 * Returns the fallback for the given Locale.
	 */
	protected Locale getFallbackLocale(Locale locale) {
		String country = locale.getCountry();
		String lang = locale.getLanguage();
		if (StringUtils.hasLength(locale.getVariant())) {
			return new Locale(lang, country);
		}
		if (StringUtils.hasLength(country)) {
			if (fallbackToDefaultCountry && !lang.equals(country.toLowerCase())) {
				return new Locale(lang, lang.toUpperCase());	
			}
			return new Locale(lang);
		}
		return null;
	}
	
	@Override
	protected String getMessageFromParent(String code, Object[] args, Locale locale) {
		String result = super.getMessageFromParent(code, args, locale);
		if (result == null) {
			MessageBundleEntry entry = MessageBundleEntry.loadByBundleAndCode(bundle, code);
			Message message = entry.getDefaultMessage();
			if (message != null) {
				result = message.format(args, escapeSingleQuotes);
			}
		}
		return result;
	}
	
}
