package org.riotfamily.common.i18n;

import java.util.Locale;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.PropertyUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.Assert;

public class MessageResolver {
	
	private static final String EMPTY_MESSAGE = new String();
	
	private AdvancedMessageCodesResolver messageCodesResolver;
	
	private MessageSource source;
	
	private Locale locale;
	
	
	public MessageResolver(MessageSource source, 
			AdvancedMessageCodesResolver resolver, Locale locale) {
		
		Assert.notNull(source, "MessageSource must not be null");
		Assert.notNull(resolver, "MessageCodesResolver must not be null");
		Assert.notNull(locale, "Locale must not be null");
		
		this.source = source;
		this.messageCodesResolver = resolver;
		this.locale = locale;
	}

	public String getObjectError(String objectName, Class clazz,
			String errorCode, Object[] args, String defaultMessage) {
	
		String[] codes = messageCodesResolver.resolveMessageCodes(errorCode, objectName);
		return getMessage(codes, args, defaultMessage);
	}
	
	public String getPropertyError(
			String objectName, Class clazz, String property, String errorCode, 
			Object[] args, String defaultMessage) {
	
		Class fieldType = PropertyUtils.getPropertyType(clazz, property);
		String[] codes = messageCodesResolver.resolveMessageCodes(errorCode, objectName, 
				property, fieldType);
		
		return getMessage(codes, args, defaultMessage);
	}
	
	
	public String getPropertyLabel(String objectName, 
			Class clazz, String property) {
		
		String[] codes = messageCodesResolver.resolveLabel(objectName, clazz, property);
		String defaultMessage = FormatUtils.camelToTitleCase(property);
		return getMessage(codes, null, defaultMessage);
	}
	
	public String getClassLabel(String objectName, Class clazz) {
		String[] codes = messageCodesResolver.resolveLabel(objectName, clazz);
		String defaultMessage = FormatUtils.camelToTitleCase(objectName);
		return getMessage(codes, null, defaultMessage);
	}
		
	public String getPropertyHint(String objectName, 
			Class clazz, String property) {
		
		String[] codes = messageCodesResolver.resolveHint(objectName, clazz, property);
		return getMessage(codes, null, null);
	}
	
	public String getMessage(String code) {
		return getMessage(code, null, null);
	}
	
	public String getMessage(String code, Object[] args) {
		return getMessage(code, args, null);
	}
	
	public String getMessage(String code, Object[] args, String defaultMessage) {
		return source.getMessage(code, args, defaultMessage, locale);
	}
	
	public String getMessage(MessageSourceResolvable resolvable) {
		return getMessage(resolvable.getCodes(), resolvable.getArguments(), 
				resolvable.getDefaultMessage());
	}
	
	public String getMessage(String[] codes, Object[] args, 
			String defaultMessage) {
		
		if (defaultMessage == null) {
			defaultMessage = EMPTY_MESSAGE;
		}
		MessageSourceResolvable resolvable = 
				new DefaultMessageSourceResolvable(codes, args, defaultMessage);
		
		String message = source.getMessage(resolvable, locale);
		if (message == EMPTY_MESSAGE) {
			return null;
		}
		return message;
	}

	public AdvancedMessageCodesResolver getMessageCodesResolver() {
		return this.messageCodesResolver;
	}
	
}
