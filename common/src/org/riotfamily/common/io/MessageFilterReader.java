package org.riotfamily.common.io;

import java.io.Reader;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.web.util.JavaScriptUtils;

public class MessageFilterReader extends AbstractTokenFilterReader {

	private MessageSource messageSource;
	
	private String prefix;
	
	private Locale locale;
	
	private boolean escapeJsStrings;
	
	public MessageFilterReader(Reader in, MessageSource messageSource, 
			Locale locale) {
		
		this (in, messageSource, locale, null, false);
	}
	
	public MessageFilterReader(Reader in, MessageSource messageSource, 
			Locale locale, String prefix, boolean escapeJsStrings) {
		
		super(in);
		this.messageSource = messageSource;
		this.locale = locale;
		this.prefix = prefix;
		this.escapeJsStrings = escapeJsStrings;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	protected String getReplacement(String key) {
		if (prefix != null) {
			key = prefix + key;
		}
		String message = messageSource.getMessage(key, null, key, locale);
		if (escapeJsStrings) {
			message = JavaScriptUtils.javaScriptEscape(message);
		}
		return message;
	}
}
