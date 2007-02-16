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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.workflow.status.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.riot.workflow.status.StatusMessage;
import org.riotfamily.riot.workflow.status.StatusMonitor;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 * Convenience base class for {@link StatusMonitor} implementations.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public abstract class AbstractStatusMonitor implements StatusMonitor, 
		MessageSourceAware, BeanNameAware {

	private MessageSource messageSource;
	
	private String messageKey;
	
	private String link;

	private long cacheMillis;
	
	private long lastUpdate;
	
	private Object[] args;
	
	private boolean hideZeroStatus;
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public boolean isHideZeroStatus() {
		return this.hideZeroStatus;
	}

	public void setHideZeroStatus(boolean hideZeroStatus) {
		this.hideZeroStatus = hideZeroStatus;
	}

	public void setBeanName(String name) {
		if (messageKey == null) {
			messageKey = name;
		}
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

	private synchronized void updateArgs() {
		if (lastUpdate + cacheMillis < System.currentTimeMillis()) {
			this.args = FormatUtils.htmlEscapeArgs(getArgs());
		}
	}
	
	protected boolean isVisible(Object[] args) {
		if (args == null || args.length == 0) {
			return false;
		}
		if (hideZeroStatus && args[0] instanceof Number) {
			return ((Number) args[0]).intValue() > 0;
		}
		return true;	
	}
	
	/**
	 * Subclasses must return an array of objects which will be passed to
	 * the MessageSource as arguments. All arguments that are neither 
	 * primitive wrappers nor dates will be HTML-escaped automatically to  
	 * prevent XSS attacks.
	 * @see FormatUtils#htmlEscapeArgs(Object[]) 
	 */
	protected abstract Object[] getArgs();
	
}
