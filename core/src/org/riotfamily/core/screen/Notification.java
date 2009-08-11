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
	
	public Notification setKeyPrefix(String keyPrefix) {
		Assert.notNull(messageResolver, "A MessageResolver must be set first");
		this.keyPrefix = keyPrefix;
		return this;
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
