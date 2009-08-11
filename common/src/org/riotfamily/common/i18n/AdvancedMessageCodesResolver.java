package org.riotfamily.common.i18n;

import org.springframework.validation.MessageCodesResolver;

/**
 * Extension of the Spring {@link MessageCodesResolver} interface that provides
 * methods to resolve codes for labels and hints.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface AdvancedMessageCodesResolver extends MessageCodesResolver {

	String[] resolveLabel(String objectName, Class<?> objectClass);
	
	String[] resolveLabel(String objectName, Class<?> objectClass, String field);
	
	String[] resolveHint(String objectName, Class<?> objectClass, String field);
	
}
