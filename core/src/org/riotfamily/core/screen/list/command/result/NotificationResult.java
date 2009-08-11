package org.riotfamily.core.screen.list.command.result;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;
import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.core.screen.list.command.Command;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandInfo;
import org.riotfamily.core.screen.list.command.CommandResult;

@DataTransferObject
public class NotificationResult implements CommandResult {
	
	private MessageResolver messageResolver;
	
	private String keyPrefix;
	
	private Object[] args;
	
	private String titleKey;
	
	private String messageKey;
	
	private String defaultTitle;
	
	private String defaultMessage;
	
	private String title;
	
	private String icon;
	
	private String message;
	
	public NotificationResult(CommandContext context) {
		this.messageResolver = context.getMessageResolver();
	}
	
	public NotificationResult(CommandContext context, Command command) {
		CommandInfo info = command.getInfo(context);
		this.messageResolver = context.getMessageResolver();
		this.keyPrefix = "command." + info.getAction() + ".notification";
		this.defaultTitle = info.getLabel();
		this.icon = info.getIcon();
	}
	
	@RemoteProperty
	public String getAction() {
		return "notification";
	}
	
	@RemoteProperty
	public String getTitle() {
		if (title == null && messageResolver != null) {
			title = messageResolver.getMessage(getTitleKey(), args, defaultTitle);
		}
		return title;
	}
	
	@RemoteProperty
	public String getIcon() {
		return icon;
	}
	
	@RemoteProperty
	public String getMessage() {
		if (message == null && messageResolver != null) {
			message = messageResolver.getMessage(getMessageKey(), args, defaultMessage);
		}
		return message;
	}
	
	public NotificationResult setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
		return this;
	}
	
	public NotificationResult setTitleKey(String titleKey) {
		this.titleKey = titleKey;
		return this;
	}
	
	private String getTitleKey() {
		if (titleKey == null) {
			titleKey = keyPrefix + ".title";
		}
		return titleKey;
	}
	
	public NotificationResult setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public NotificationResult setDefaultTitle(String defaultTitle) {
		this.defaultTitle = defaultTitle;
		return this;
	}
	
	public NotificationResult setIcon(String icon) {
		this.icon = icon;
		return this;
	}
	
	public NotificationResult setMessageKey(String messageKey) {
		this.messageKey = messageKey;
		return this;
	}
	
	private String getMessageKey() {
		if (messageKey == null) {
			messageKey = keyPrefix + ".message";
		}
		return titleKey;
	}
	
	public NotificationResult setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public NotificationResult setDefaultMessage(String defaultMessage) {
		this.defaultMessage = defaultMessage;
		return this;
	}

	public NotificationResult setArgs(Object... args) {
		this.args = args;
		return this;
	}
}
