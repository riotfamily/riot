package org.riotfamily.pages.page.meta;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.page.Page;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContextUtils;

public class MessageSourceMetaDataProvider extends InheritingMetaDataProvider 
		implements MessageSourceAware {

	private MessageSource messageSource;
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	protected MetaData createMetaData(Page page, HttpServletRequest request) {
		Locale locale = RequestContextUtils.getLocale(request);
		String key = "page" + page.getPath().replace('/', '.');
		
		MetaData metaData = new MetaData();
		metaData.setTitle(getMessage(key + ".title", locale));
		metaData.setDescription(getMessage(key + ".description", locale));
		metaData.setKeywords(getMessage(key + ".keywords", locale));
		
		return metaData;
	}
	
	private String getMessage(String key, Locale locale) {
		return messageSource.getMessage(key, null, null, locale);
	}

}
