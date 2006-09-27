package org.riotfamily.common.web.resource;

import java.io.FilterReader;
import java.io.Reader;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.io.MessageFilterReader;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContextUtils;

public class MessageResourceFilter extends AbstractPathMatchingResourceFilter 
		implements MessageSourceAware {
	
	private MessageSource messageSource;
	
	private String prefix;
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public FilterReader createFilterReader(Reader in, 
			HttpServletRequest request) {
		
		Locale locale = RequestContextUtils.getLocale(request);
		return new MessageFilterReader(in, messageSource, locale, prefix);
	}

}
