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
package org.riotfamily.forms.element;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.ErrorUtils;
import org.riotfamily.forms.MessageUtils;
import org.riotfamily.forms.TemplateUtils;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.resource.StylesheetResource;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.util.StringUtils;

/**
 * A DHTML calendar widget.
 * http://www.dynarch.com/projects/calendar/
 */
public class Calendar extends AbstractTextElement implements ResourceElement,
		DHTMLElement {

	/**
	 * Rules to translate {@link SimpleDateFormat SimpleDateFormat patterns}
	 * to the syntax used by the JavaScript calendar widget.
	 * */
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
            "(?<!%)ss?", "%S",
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

	private String formatPattern = "yyyy-MM-dd";

	private String formatKey;

	private String defaultValue;

	private String jsFormatPattern;

	private DateFormat dateFormat;

	private ScriptResource resource;


	public Calendar() {
		setStyleClass("text calendar-input");
	}

	public String getFormatPattern() {
		return formatPattern;
	}

	/**
	 * Sets the format pattern to use.
	 * @see SimpleDateFormat
	 */
	public void setFormatPattern(String formatPattern) {
		this.formatPattern = formatPattern;
	}

	public String getJsFormatPattern() {
		return jsFormatPattern;
	}

	/**
	 * Sets a message-key that is used to look-up the actual format pattern.
	 */
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
		dateFormat = new SimpleDateFormat(formatPattern);
		setPropertyEditor(new CustomDateEditor(dateFormat, false));
	}

	public boolean isShowTime() {
		return formatPattern != null && (formatPattern.indexOf('H') != -1 ||
				formatPattern.indexOf('h') != -1);
	}

	public void setValue(Object value) {
		if (value == null && defaultValue != null) {
			value = getDefaultDate();
		}
		super.setValue(value);
	}

	public void validate() {
		super.validate();
		if (StringUtils.hasText(getText())) {
			try {
				dateFormat.parse(getText());
			}
			catch (ParseException e) {
				ErrorUtils.reject(this, "error.calendar.invalidDateFormat");
			}
		}
	}

	protected void afterFormContextSet() {
		String lang = getFormContext().getLocale().getLanguage().toLowerCase();
		URL languageScript = getClass().getResource(
				"/org/riotfamily/resources/jscalendar/lang/calendar-"
				+ lang + ".js");

		if (languageScript == null) {
			lang = "en";
		}

		resource = new ScriptResource("jscalendar/calendar-setup.js", "Calendar.setup", new FormResource[] {
			new ScriptResource("jscalendar/lang/calendar-" + lang + ".js", "Calendar._DN",
				new ScriptResource("jscalendar/calendar.js", "Calendar")
			),
			new StylesheetResource("jscalendar/calendar.css")
		});
	}

	public FormResource getResource() {
		return resource;
	}

	public String getInitScript() {
		return TemplateUtils.getInitScript(this);
	}

	protected Date getDefaultDate() {
		Date date = FormatUtils.parseDate(defaultValue);
		if (date == null) {
			try {
				date = dateFormat.parse(defaultValue);
			}
			catch (ParseException e) {
			}
		}
		return date;
	}
}
