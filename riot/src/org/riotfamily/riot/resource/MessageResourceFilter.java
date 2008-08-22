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
package org.riotfamily.riot.resource;

import java.io.FilterReader;
import java.io.Reader;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.io.MessageFilterReader;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContextUtils;

public class MessageResourceFilter extends AbstractPathMatchingResourceFilter 
		implements MessageSourceAware {
	
	private MessageSource messageSource;
	
	private String prefix;
	
	private boolean escapeJsStrings;
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setEscapeJsStrings(boolean escapeJsStrings) {
		this.escapeJsStrings = escapeJsStrings;
	}
	
	public FilterReader createFilterReader(Reader in, 
			HttpServletRequest request) {
		
		Locale locale = RequestContextUtils.getLocale(request);
		return new MessageFilterReader(in, messageSource, locale, prefix, escapeJsStrings);
	}

}
