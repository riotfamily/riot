package org.riotfamily.forms.i18n;

import org.riotfamily.forms.Element;
import org.riotfamily.forms.FormContext;
import org.riotfamily.forms.bind.EditorBinding;

public class MessageUtils {
		
	public static String getMessage(Element element, String key) {
		return getMessage(element, key, null, key);
	}
	
	public static String getMessage(Element element, String key, 
			Object[] args, String defaultMessage) {
		
		FormContext context = element.getForm().getFormContext();
		return context.getMessageResolver().getMessage(
				key, args, defaultMessage);
	}
	
	public static String getLabel(Element element, EditorBinding binding) {
		FormContext context = element.getForm().getFormContext();
		return context.getMessageResolver().getPropertyLabel(
				element.getForm().getId(), binding.getBeanClass(), 
				binding.getProperty());
	}
	
	public static String getHint(Element element, EditorBinding binding) {
		FormContext context = element.getForm().getFormContext();
		return context.getMessageResolver().getPropertyHint(
				element.getForm().getId(), binding.getBeanClass(), 
				binding.getProperty());
	}
}
