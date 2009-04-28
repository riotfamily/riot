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
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.core.screen;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.riotfamily.common.util.FormatUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContextUtils;


public abstract class AbstractRiotScreen implements RiotScreen, BeanNameAware, 
		MessageSourceAware {

	private String id;
	
	private String icon;
	
	private RiotScreen parentScreen;
	
	private MessageSource messageSource;
	
	public void setBeanName(String beanName) {
		if (id == null) {
			id = beanName;
		}
	}
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public RiotScreen getParentScreen() {
		return parentScreen;
	}

	public void setParentScreen(RiotScreen parentScreen) {
		this.parentScreen = parentScreen;
	}
	
	public Collection<RiotScreen> getChildScreens() {
		return Collections.emptySet();
	}
	
	public String getTitle(ScreenContext context) {
		String code = "screen." + getId();
		String defaultTitle = FormatUtils.xmlToTitleCase(getId());
		Locale locale = RequestContextUtils.getLocale(context.getRequest());
		return messageSource.getMessage(code, null, defaultTitle, locale);
	}

}
