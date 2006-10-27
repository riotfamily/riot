/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.i18n;

import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
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
	
	public static String getHint(Form form, Class beanClass) {
		FormContext context = form.getFormContext();
		return context.getMessageResolver().getPropertyHint(
				form.getId(), beanClass, null);
	}
}
