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
package org.riotfamily.common.ui;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.riotfamily.common.util.FormatUtils;

public class DateRenderer implements ObjectRenderer {

	private static final String SHORT = "short";
	
	private static final String MEDIUM = "medium";
	
	private static final String LONG = "long";
	
	private int style = DateFormat.MEDIUM;
	
	private String pattern = null;
	
	public void setStyle(String style) {
		if (SHORT.equals(style)) {
			this.style = DateFormat.SHORT;
		}
		else if (MEDIUM.equals(style)) {
			this.style = DateFormat.MEDIUM;
		} 
		else if (LONG.equals(style)) {
			this.style = DateFormat.LONG;
		}
		else {
			throw new IllegalArgumentException("Invalid date style: " + style);
		}
	}
	
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	public void render(Object obj, RenderContext context, PrintWriter writer) {
		Date date = (Date) obj;
		if (date != null) {
			Locale locale = context.getMessageResolver().getLocale();
			String s;
			if (pattern != null) {
				s = FormatUtils.formatDate(date, pattern, locale);
			}
			else {
				DateFormat format = SimpleDateFormat.getDateInstance(style, locale);
				s = format.format(date);
			}
			writer.print(s);
		}
	}
}
