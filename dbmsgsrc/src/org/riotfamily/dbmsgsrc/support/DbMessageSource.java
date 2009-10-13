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
package org.riotfamily.dbmsgsrc.support;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;
import org.riotfamily.common.web.cache.CacheTagUtils;
import org.riotfamily.dbmsgsrc.model.Message;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

public class DbMessageSource extends AbstractMessageSource {

	public static final String DEFAULT_BUNDLE = "default";
	
	private TransactionTemplate transactionTemplate;
	
	private String bundle = DEFAULT_BUNDLE;
	
	private boolean fallbackToDefaultCountry = true;
	
	private boolean escapeSingleQuotes = true;
	
	public DbMessageSource(PlatformTransactionManager tx) {
		this.transactionTemplate = new TransactionTemplate(tx); 
	}
	
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
	
	MessageBundleEntry getEntry(final String code, final String defaultMessage) {
		MessageBundleEntry result = MessageBundleEntry.loadByBundleAndCode(bundle, code);
		if (result == null) {
			try {
				result = transactionTemplate.execute(new TransactionCallback<MessageBundleEntry>() {
					public MessageBundleEntry doInTransaction(TransactionStatus status) {
						MessageBundleEntry entry = new MessageBundleEntry(bundle, code, defaultMessage);
						entry.save();
						return entry;
					}
				});
			}
			catch (ConstraintViolationException e) {
				return result = MessageBundleEntry.loadByBundleAndCode(bundle, code);
			}
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
		Message message = messages.get(locale);
		if (message == null) {
			String country = locale.getCountry();
			String lang = locale.getLanguage();
			if (fallbackToDefaultCountry && (!StringUtils.hasLength(country)
					|| !lang.equals(country.toLowerCase()))) {
				
				message = messages.get(new Locale(lang, lang.toUpperCase()));
			}
			if (message == null) {
				message = messages.get(new Locale(lang));
			}
		}
		return message;
	}
	
	@Override
	protected String getMessageFromParent(String code, Object[] args, Locale locale) {
		String s = super.getMessageFromParent(code, args, locale);
		if (s == null) {
			MessageBundleEntry entry = MessageBundleEntry.loadByBundleAndCode(bundle, code);
			Message message = entry.getDefaultMessage();
			if (message != null) {
				if (args != null) {
					MessageFormat messageFormat = message.getMessageFormat(escapeSingleQuotes);
					if (messageFormat != null) {
						synchronized (messageFormat) {
							return messageFormat.format(args);
						}
					}
				}
				else {
					return message.getText();
				}
			}
		}
		return s;
	}
	
}
