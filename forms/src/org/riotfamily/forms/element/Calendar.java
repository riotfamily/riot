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

	public Object getDefaultValue() {
		String defaultText = getDefaultText();
		if (defaultText != null) {
			Date date = FormatUtils.parseDate(defaultText);
			if (date == null) {
				try {
					date = dateFormat.parse(defaultText);
				}
				catch (ParseException e) {
				}
			}
			return date;
		}
		return null;
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

		resource = new ScriptResource("jscalendar/calendar-setup.js", "Calendar.setup", 
			new ScriptResource("jscalendar/lang/calendar-" + lang + ".js", "Calendar._DN",
				new ScriptResource("jscalendar/calendar.js", "Calendar")
			),
			new StylesheetResource("jscalendar/calendar.css")
		);
	}

	public FormResource getResource() {
		return isEnabled() ? resource : null;
	}

	public String getInitScript() {
		return isEnabled() ? TemplateUtils.getInitScript(this) : null;
	}

}
