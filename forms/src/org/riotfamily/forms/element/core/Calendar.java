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
package org.riotfamily.forms.element.core;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.forms.element.DHTMLElement;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.resource.ScriptSequence;
import org.riotfamily.forms.resource.StylesheetResource;
import org.riotfamily.forms.support.MessageUtils;
import org.riotfamily.forms.support.TemplateUtils;

/**
 * A DHTML calendar widget.
 */
public class Calendar extends TextField implements ResourceElement,
		DHTMLElement {	
	
	/** Format conversion patterns */
    private static String[] conversions = new String[] {
            "'(.)", "$1",
            "%", "%%",
            "(?<!%)MMMM", "%B",
            "(?<!%)MMM", "%b",
            "(?<!%)MM?", "%m",
            "(?<!%)dd", "%d",
            "(?<!%)d", "%e",
            "(?<!%)yyyy", "%Y",
            "(?<!%)yy", "%y",
            "(?<!%)hh", "%I",
            "(?<!%)h", "%i",
            "(?<!%)HH", "%H",
            "(?<!%)H", "%h",
            "(?<!%)mm?", "%M",
            "(?<!%)s", "%S",
            "(?<!%)EE?", "%a",
            "(?<!%)w", "%W",
            "(?<!%)a", "%P"};
    
    /** List of compiled conversion patterns */
    private static Pattern[] patterns;
    
    static {
        patterns = new Pattern[conversions.length / 2];
        for (int i = 0; i < patterns.length; i++) {
            patterns[i] = Pattern.compile(conversions[i * 2]);
        }
    }
    
	private ArrayList resources;
	
	private String formatPattern = "yyyy-MM-dd";
	
	private String jsFormatPattern;
	
	private String formatKey;
	
	private String defaultValue;
	
	
	public Calendar() {
		setStyleClass("text calendar-input");
	}
	
	public String getFormatPattern() {
		return formatPattern;
	}
	
	public void setFormatPattern(String formatPattern) {
		this.formatPattern = formatPattern;
	}
	
	public String getJsFormatPattern() {
		return jsFormatPattern;
	}
	
	public void setFormatKey(String formatKey) {
		this.formatKey = formatKey;
	}	 
	
	public void setDefaultValue(String defaultValue) {		
		this.defaultValue = defaultValue;
	}

	protected void afterFormSet() {
		if (formatKey != null) {
			formatPattern = MessageUtils.getMessage(this, formatKey);
		}
		jsFormatPattern = formatPattern;
		for (int i = 0; i < patterns.length; i++) {
            Matcher matcher = patterns[i].matcher(jsFormatPattern);
            jsFormatPattern = matcher.replaceAll(conversions[i * 2 + 1]);
        }
		
	}
	
	public void setValue(Object value) {
		if (value == null && defaultValue != null) {
			super.setValue(getDefaultDate());
		}
		super.setValue(value);
	}

	protected void afterFormContextSet() {
		String lang = getFormContext().getLocale().getLanguage().toLowerCase();
		URL languageScript = getClass().getResource(
				"/org/riotfamily/resources/jscalendar/lang/calendar-" 
				+ lang + ".js");
		
		if (languageScript == null) {
			lang = "en";
		}
		
		resources = new ArrayList();
		resources.add(new StylesheetResource("jscalendar/calendar.css"));
		resources.add(new ScriptSequence(new ScriptResource[] {
			new ScriptResource("jscalendar/calendar.js", "Calendar"),
			new ScriptResource("jscalendar/lang/calendar-" + lang + ".js", "Calendar._DN"),
			new ScriptResource("jscalendar/calendar-setup.js", "Calendar.setup")
		}));
	}
	
	public Collection getResources() {
		return resources;
	}	

	public String getInitScript() {
		return TemplateUtils.getInitScript(this);
	}
	
	public String getPrecondition() {
		return "Calendar.setup";
	}
	
	protected Date getDefaultDate() {
		Date date = FormatUtils.parseDate(defaultValue);
		if (date == null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(formatPattern);
				date = sdf.parse(defaultValue);
			}
			catch (ParseException e) {
			}
		}
		return date;
	}	
}
