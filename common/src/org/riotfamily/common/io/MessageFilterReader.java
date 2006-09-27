package org.riotfamily.common.io;

import java.io.Reader;
import java.util.Locale;

import org.springframework.context.MessageSource;

public class MessageFilterReader extends AbstractTokenFilterReader {

	private MessageSource messageSource;
	
	private String prefix;
	
	private Locale locale;
	
	public MessageFilterReader(Reader in, MessageSource messageSource, 
			Locale locale) {
		
		this (in, messageSource, locale, null);
	}
	
	public MessageFilterReader(Reader in, MessageSource messageSource, 
			Locale locale, String prefix) {
		
		super(in);
		this.messageSource = messageSource;
		this.locale = locale;
		this.prefix = prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	protected String getReplacement(String key) {
		if (prefix != null) {
			key = prefix + key;
		}
		return messageSource.getMessage(key, null, key, locale);
	}
}
