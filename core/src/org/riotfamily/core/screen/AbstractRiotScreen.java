package org.riotfamily.core.screen;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

	public List<Screenlet> getScreenlets() {
		return screenlets;
	}

	public void setScreenlets(List<Screenlet> screenlets) {
		this.screenlets = screenlets;
	}
	
}
