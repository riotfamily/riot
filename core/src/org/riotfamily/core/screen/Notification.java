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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Carsten Woelk [cwoelk at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.core.screen;

import org.riotfamily.common.i18n.MessageResolver;
import org.springframework.util.Assert;

public class Notification {
	
	private MessageResolver messageResolver;
	
	private String keyPrefix;
	
	private Object[] args;
	
	private String titleKey;
	
	private String messageKey;
	
	private String defaultTitle;
	
	private String defaultMessage;
	
	private String title;
	
	private String message;
	
	private String icon;
		
	public Notification(MessageResolver messageResolver) {
		this.messageResolver = messageResolver;
	}

	public String getTitle() {
		if (title == null && messageResolver != null) {
			title = messageResolver.getMessage(getTitleKey(), args, defaultTitle);
		}
		return title;
	}
	
	public String getMessage() {
		if (message == null && messageResolver != null) {
			message = messageResolver.getMessage(getMessageKey(), args, defaultMessage);
		}
		return message;
	}
	
	public String getIcon() {
		return icon;
	}

	public Notification setIcon(String icon) {
		this.icon = icon;
		return this;
	}
	
	public void setKeyPrefix(String keyPrefix) {
		Assert.notNull(messageResolver, "A MessageResolver must be set first");
		this.keyPrefix = keyPrefix;
	}
	
	public Notification setTitleKey(String titleKey) {
		Assert.notNull(messageResolver, "A MessageResolver must be set first");
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
		Assert.notNull(messageResolver, "A MessageResolver must be set first");
		this.messageKey = messageKey;
		return this;
	}
	
	private String getMessageKey() {
		if (messageKey == null && keyPrefix != null) {
			messageKey = keyPrefix + ".message";
		}
		return titleKey;
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
	
}
