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
import java.util.Set;

import org.riotfamily.common.util.Generics;
import org.riotfamily.riot.security.AccessController;
import org.riotfamily.riot.security.auth.RiotUser;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DelegatingMessageSource;

/**
 * MessageSource that reveals the code(s) used to look-up a message.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class CodeRevealingMessageSource extends DelegatingMessageSource {
	
	private Set<String> revealTo = Generics.newHashSet();

	private String contextPath = "";
	
	private Set<String> doNotReveal;
	
	public void setDoNotReveal(Set<String> doNotReveal) {
		this.doNotReveal = doNotReveal;
	}
	
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	
	protected String getContextPath() {
		return contextPath;
	}
	
	public boolean isRevealCodes() {
		RiotUser user = AccessController.getCurrentUser();
		if (user != null) {
			return revealTo.contains(user.getUserId());
		}
		return false;
	}

	public void setRevealCodes(boolean revealCodes) {
		RiotUser user = AccessController.getCurrentUser();
		if (user != null) {
			if (revealCodes) {
				revealTo.add(user.getUserId());
			}
			else {
				revealTo.remove(user.getUserId());
			}
		}
	}

	protected boolean shouldBeRevealed(String code) {
		return isRevealCodes() && (doNotReveal == null || !doNotReveal.contains(code));
	}
	
	protected String revealCodes(String message, String... codes) {
		StringBuffer sb = new StringBuffer();
		if (message != null) {
			sb.append(message);
		}
		sb.append("<span class=\"messageCode\" title=\"");
		int i = 0;
		for (String code : codes) {
			sb.append(code);
			if (++i < codes.length) {
				sb.append(" | ");
			}
		}
		sb.append("\"></span>");
		
		return sb.toString();
	}
	
	public String getMessage(String code, Object[] args, String defaultMessage,
			Locale locale) {
		
		if (shouldBeRevealed(code)) {
			return revealCodes(super.getMessage(code, args, 
					defaultMessage, locale), code);
		}
		return super.getMessage(code, args, defaultMessage, locale);
	}

	public String getMessage(String code, Object[] args, Locale locale) {
		return getMessage(code, args, null, locale);
	}

	public String getMessage(MessageSourceResolvable resolvable, Locale locale) {
		if (shouldBeRevealed(resolvable.getCodes()[0])) {
			return revealCodes(super.getMessage(resolvable, locale), resolvable.getCodes());
		}
		return super.getMessage(resolvable, locale);
	}

}
