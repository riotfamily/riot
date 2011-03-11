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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.riotfamily.common.util.FormatUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.support.RequestContextUtils;


public abstract class AbstractRiotScreen implements RiotScreen, BeanNameAware, 
		MessageSourceAware {

	private String id;
	
	private String icon;
	
	private String vanityTitle;
	
	private RiotScreen parentScreen;
	
	private MessageSource messageSource;
	
	private List<Screenlet> screenlets;
	
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

	public String getVanityTitle() {
		return vanityTitle;
	}

	public void setVanityTitle(String vanityTitle) {
		this.vanityTitle = vanityTitle;
	}

	public RiotScreen getParentScreen() {
		return parentScreen;
	}

	public void setParentScreen(RiotScreen parentScreen) {
		if (this.parentScreen == null) {
			this.parentScreen = parentScreen;
		}
	}
	
	public Collection<RiotScreen> getChildScreens() {
		return Collections.emptySet();
	}
	
	public String getTitle(ScreenContext context) {
		String code = "screen." + getId();
		String defaultTitle = vanityTitle != null ? vanityTitle : getId();
		defaultTitle = FormatUtils.xmlToTitleCase(defaultTitle);
		Locale locale = RequestContextUtils.getLocale(context.getRequest());
		return messageSource.getMessage(code, null, defaultTitle, locale);
	}

	public List<Screenlet> getScreenlets() {
		return screenlets;
	}

	public void setScreenlets(List<Screenlet> screenlets) {
		this.screenlets = screenlets;
	}
	
	@Override
	public String toString() {
		return String.format("%s[id=%s]", ClassUtils.getShortName(getClass()), getId());
	}
	
}
