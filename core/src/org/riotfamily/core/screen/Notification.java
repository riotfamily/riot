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
package org.riotfamily.core.screen;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.core.runtime.RiotRuntime;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.Assert;


public class Notification {
	
	private MessageSource messageSource;
	
	private String resourcePath;
	
	private String keyPrefix;
	
	private Object[] args;
	
	private String titleKey;
	
	private String messageKey;
	
	private String defaultTitle;
	
	private String defaultMessage;
	
	private String title;
	
	private String message;
	
	private String icon;

	public Notification(RiotRuntime runtime) {
		this(runtime.getMessageSource(), runtime.getResourcePath());
	}
	
	public Notification(MessageSource messageSource, String resourcePath) {
		this.messageSource = messageSource;
		this.resourcePath = resourcePath;
	}

	public String getTitle() {
		if (title == null) {
			title = resolve(getTitleKey(), defaultTitle);
		}
		return title;
	}
	
	public String getMessage() {
		if (message == null) {
			message = resolve(getMessageKey(), defaultMessage);
		}
		return message;
	}
	
	protected String resolve(String key, String defaultText) {
		if (messageSource != null && key != null) {
			return messageSource.getMessage(key, args, defaultText, LocaleContextHolder.getLocale());
		}
		return null;
	}
	
	public String getIcon() {
		return icon;
	}

	public Notification setIcon(String icon) {
		this.icon = String.format("%s/style/images/icons/%s.png", resourcePath, icon);
		return this;
	}
	
	public Notification setKeyPrefix(String keyPrefix) {
		Assert.notNull(messageSource, "A MessageSource must be set first");
		this.keyPrefix = keyPrefix;
		return this;
	}
	
	public Notification setTitleKey(String titleKey) {
		Assert.notNull(messageSource, "A MessageSource must be set first");
		this.titleKey = titleKey;
		return this;
	}
	
	private String getTitleKey() {
		if (titleKey == null && keyPrefix != null) {
			titleKey = keyPrefix + ".title";
		}
		return titleKey;
	}
	
	public Notification setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public Notification setDefaultTitle(String defaultTitle) {
		this.defaultTitle = defaultTitle;
		return this;
	}
	
	public Notification setMessageKey(String messageKey) {
		Assert.notNull(messageSource, "A MessageSource must be set first");
		this.messageKey = messageKey;
		return this;
	}
	
	private String getMessageKey() {
		if (messageKey == null && keyPrefix != null) {
			messageKey = keyPrefix + ".message";
		}
		return messageKey;
	}
	
	public Notification setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public Notification setDefaultMessage(String defaultMessage) {
		this.defaultMessage = defaultMessage;
		return this;
	}

	public Notification setArgs(Object... args) {
		this.args = args;
		return this;
	}
	
	public String toScript() {
		return String.format("riot.showNotification(%s);", FormatUtils.toJSON(this));
	}
	
}
