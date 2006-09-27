package org.riotfamily.common.i18n;

import org.springframework.validation.MessageCodesResolver;

public interface AdvancedMessageCodesResolver extends MessageCodesResolver {

	String[] resolveLabel(String objectName, Class objectClass);
	
	String[] resolveLabel(String objectName, Class objectClass, String field);
	
	String[] resolveHint(String objectName, Class objectClass, String field);
	
}
