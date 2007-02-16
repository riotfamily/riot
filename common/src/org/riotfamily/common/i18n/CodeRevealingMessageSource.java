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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.i18n;

import java.util.Locale;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.util.StringUtils;

/**
 * MessageSource that reveals the code(s) used to look-up a message.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class CodeRevealingMessageSource extends DelegatingMessageSource {
	
	private boolean revealCodes = true;

	public boolean isRevealCodes() {
		return this.revealCodes;
	}

	public void setRevealCodes(boolean revealAllCodes) {
		this.revealCodes = revealAllCodes;
	}

	protected String revealCode(String message, String code) {
		StringBuffer sb = new StringBuffer();
		if (message != null) {
			sb.append(message);
		}
		sb.append("<span class=\"messageCode\" title=\"")
			.append(code).append("\"></span>");
		
		return sb.toString();
	}
	
	public String getMessage(String code, Object[] args, String defaultMessage,
			Locale locale) {
		
		if (revealCodes) {
			return revealCode(super.getMessage(code, args, 
					defaultMessage, locale), code);
		}
		return super.getMessage(code, args, defaultMessage, locale);
	}

	public String getMessage(String code, Object[] args, Locale locale) {
		return getMessage(code, args, null, locale);
	}

	public String getMessage(MessageSourceResolvable resolvable, Locale locale) {
		if (revealCodes) {
			return revealCode(super.getMessage(resolvable, locale),
					StringUtils.arrayToDelimitedString(
					resolvable.getCodes(), " | "));
		}
		return super.getMessage(resolvable, locale);
	}
}
