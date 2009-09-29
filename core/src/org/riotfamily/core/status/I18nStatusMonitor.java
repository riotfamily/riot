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
package org.riotfamily.core.status;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.web.mvc.mapping.HandlerUrlUtils;
import org.riotfamily.core.screen.ScreenContext;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContextUtils;

public abstract class I18nStatusMonitor extends StatusMonitor 
		implements MessageSourceAware {

	private MessageSource messageSource;
	
	private String messageKey;
	
	private String defaultMessage;
	
	private String icon;
	
	private String linkedScreen;
	
	private long cacheMillis;
	
	private long lastUpdate;
	
	private Object[] args;
	
	private boolean hideZeroStatus;
	
	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public void setDefaultMessage(String defaultMessage) {
		this.defaultMessage = defaultMessage;
	}
	
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public void setLinkedScreen(String linkedScreen) {
		this.linkedScreen = linkedScreen;
	}
	
	public void setHideZeroStatus(boolean hideZeroStatus) {
		this.hideZeroStatus = hideZeroStatus;
	}

	public void setCache(String period) {
		cacheMillis = FormatUtils.parseMillis(period);
	}
	
	private synchronized void updateArgs(ScreenContext context) {
		if (lastUpdate + cacheMillis < System.currentTimeMillis()) {
			this.args = FormatUtils.htmlEscapeArgs(getArgs(context));
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
	
	protected Object[] getArgs(ScreenContext context) {
		Object arg = getArg(context);
		if (arg == null) {
			return null;
		}
		return new Object[] { arg };
	}
	
	protected Object getArg(ScreenContext context) {
		return null;
	}

	@Override
	protected Status getStatus(ScreenContext context) {
		updateArgs(context);
		if (!isVisible(args)) {
			return null;
		}
		HttpServletRequest request = context.getRequest();
		Locale locale = RequestContextUtils.getLocale(request);
		Status status = new Status();
		status.setMessage(messageSource.getMessage(messageKey, args, 
				defaultMessage, locale));
		
		status.setIcon(icon);
		if (linkedScreen != null) {
			status.setLink(HandlerUrlUtils.getContextRelativeUrl(
					request, linkedScreen, context));
		}
		return status;
	}
	
	// -----------------------------------------------------------------------
	// Implementation of the MessageSourceAware interface
	// -----------------------------------------------------------------------
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
