package org.riotfamily.forms.i18n;

import org.riotfamily.forms.Element;

/**
 * View tool to lookup messages. Can be used within Velocity templates by 
 * calling:
 * <pre>  
 *	$messageToo.getMessage($element, 'messageKey', 'Default message')
 * </pre>
 */
public class MessageTool {

	public String getMessage(Element element, String key, 
			String defaultMessage) {
		
		return MessageUtils.getMessage(element,	key, null, defaultMessage);
	}
}
