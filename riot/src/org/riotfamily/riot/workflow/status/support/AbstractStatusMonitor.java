package org.riotfamily.riot.workflow.status.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.riot.workflow.status.StatusMessage;
import org.riotfamily.riot.workflow.status.StatusMonitor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

public abstract class AbstractStatusMonitor implements StatusMonitor, 
		MessageSourceAware {

	private MessageSource messageSource;
	
	private String messageKey;
	
	private String link;

	private long cacheMillis;
	
	private long lastUpdate;
	
	private Object[] args;
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	public void setCache(String period) {
		cacheMillis = FormatUtils.parseMillis(period);
	}

	public Collection getMessages(Locale locale) {
		updateArgs();
		if (isVisible(args)) {
			String message = messageSource.getMessage(messageKey, args, locale);
			return Collections.singleton(new StatusMessage(message, link));
		}
		return null;
	}

	private void updateArgs() {
		if (lastUpdate + cacheMillis < System.currentTimeMillis()) {
			args = getArgs();
		}
	}
	
	protected boolean isVisible(Object[] args) {
		if (args == null || args.length == 0) {
			return false;
		}
		if (args[0] instanceof Number) {
			return ((Number) args[0]).intValue() > 0;
		}
		return true;	
	}
	
	protected abstract Object[] getArgs();
	
}
