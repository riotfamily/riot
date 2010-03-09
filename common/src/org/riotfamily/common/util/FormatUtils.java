/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Utility class that provides some simple text formatting methods.
 */
public final class FormatUtils {

	private static NumberFormat numberFormat = new DecimalFormat("0.#");

	private static final String OP_ADDITION = "+";

	private static final String OP_SUBTRACTION = "-";

	private static final String ENTITY_LT = "&lt;";

	private static final String ENTITY_GT = "&gt;";

	private static final String ENTITY_AMP = "&amp;";

	private static final String ENTITY_QUOT = "&quot;";

	private static final Pattern TAG_PATTERN = Pattern.compile("</?[^>]+>");
	
	private static final Pattern DANGLING_AMP_PATTERN = Pattern.compile("&(?!#?\\w+;)");
			
	private static final Pattern PARENT_DIR_PATTERN = Pattern.compile("\\.\\./");
	
	private static final Pattern DATE_DELIMITER_PATTERN = Pattern.compile("^[M|Y|D]*([^MYD])[M|Y|D]*([^MYD])[M|Y|D]*([^MYD])?$");
	
	private FormatUtils() {
	}
	
	private static Logger getLog() {
		return LoggerFactory.getLogger(FormatUtils.class);
	}

	public static String formatNumber(Number number, String pattern, Locale locale) {
		 NumberFormat f = NumberFormat.getInstance(locale);
		 if (f instanceof DecimalFormat) {
		     ((DecimalFormat) f).applyPattern(pattern);
		 }
		 return f.format(number);
	}
	
	/**
	 * Returns a formatted string using an appropriate unit (Bytes, KB or MB).
	 */
	public static String formatByteSize(long bytes) {
		if (bytes < 1024) {
			return numberFormat.format(bytes) + " Bytes";
		}
		float kb = (float) bytes / 1024;
		if (kb < 1024) {
			return numberFormat.format(kb) + " KB";
		}
		float mb = kb / 1024;
		return numberFormat.format(mb) + " MB";
	}
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	    	data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	    			+ Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}

	/**
	 * <pre>
	 * camelCase -> Camel Case
	 * CamelCASE -> Camel CASE
	 * Cam31Case -> Cam 31 Case
	 * </pre>
	 */
	public static String camelToTitleCase(String s) {
		if (s == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		char last = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isUpperCase(c) && last > 0 && !Character.isUpperCase(last)) {
				sb.append(' ');
			}
			else if (Character.isDigit(c) && last > 0 && !Character.isDigit(last)) {
				sb.append(' ');
			}
			if (i == 0) {
				c = Character.toUpperCase(c);
			}
			sb.append(c);
			last = c;
		}
		return sb.toString();
	}

	/**
	 * <pre>
	 * foo-bar -> fooBar
	 * Foo-bAR -> FooBAR
	 * </pre>
	 */
	public static String xmlToCamelCase(String s) {
		if (s == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder(s);
		int offset = 0;
		int i;
		while ((i = sb.indexOf("-", offset)) >= 0) {
			sb.deleteCharAt(i);
			if (sb.length() > i) {
				sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
				offset = i;
			}
		}
		return sb.toString();
	}

	/**
	 * <pre>
	 * foo-bar -> Foo Bar
	 * fooBar  -> Foo Bar
	 * </pre>
	 */
	public static String xmlToTitleCase(String s) {
		return camelToTitleCase(xmlToCamelCase(s));
	}

	/**
	 * <pre>
	 * foo.bar    -> Foo Bar
	 * foo.barBar -> Foo Bar Bar
	 * </pre>
	 */
	public static String propertyToTitleCase(String s) {
		if (s == null) {
			return null;
		}
		return xmlToTitleCase(s.replace('.', '-'));
	}

	/**
	 * <pre>
	 * foo.bar     -> Foo Bar
	 * foo-foo_bar -> Foo Foo Bar
	 * foo.barBar  -> Foo Bar Bar
	 * </pre>
	 */
	public static String fileNameToTitleCase(String s) {
		if (s == null) {
			return null;
		}
		return propertyToTitleCase(s.replace('_', '-'));
	}

	/**
	 * <pre>
	 * CamelCase -> camel-case
	 * camelCASE -> camel-case
	 * </pre>
	 */
	public static String camelToXmlCase(String s) {
		if (s == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		boolean lastWasLower = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isUpperCase(c)) {
				if (lastWasLower) {
					sb.append('-');
				}
				c = Character.toLowerCase(c);
			}
			else {
				lastWasLower = true;
			}
			sb.append(c);
		}
		return sb.toString();
	}

	public static String join(String delimiter, String... parts) {
		StringBuilder sb = new StringBuilder();
		for (String s : parts) {
			if (s != null) {
				if (sb.length() > 0) {
					sb.append(delimiter);
				}
				sb.append(s);
			}
		}
		return sb.toString();
	}
	
	public static List<String> tokenize(String s, String delimiter) {
		return Arrays.asList(StringUtils.delimitedListToStringArray(s, delimiter));
	}
	
	public static List<String> tokenizeCommaDelimitedList(String s) {
		return tokenize(s, ",");
	}
	
	/**
	 * "a", "b", "c" -> "a b c a-b a-b-c"
	 * "a", "b", null -> "a b a-b"
	 */
	public static String combine(String... s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length; i++) {
			if (s[i] != null) {
				sb.append(s[i]);
				if (i < s.length - 1 && s[i + 1] != null) {
					sb.append(' ');
				}
			}
		}
		for (int j = 1; j < s.length; j++) {
			if (s[j] != null) {
				sb.append(' ');
				for (int i = 0; i <= j; i++) {
					sb.append(s[i]);
					if (i < j && s[i + 1] != null) {
						sb.append('-');
					}
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Truncates the given String if its length exceeds the specified value.
	 * @since 6.4
	 */
	public static String truncate(String s, int length) {
		if (s == null || s.length() <= length) {
			return s;
		}
		return s.substring(0, length);
	}

	/**
	 * Converts the given String into a valid CSS class name.
	 */
	public static String toCssClass(String s) {
		if (s == null) {
			return null;
		}
		s = s.replaceAll("[.\\s/]", "-");
		s = s.replaceAll("[^\\w-_]", "");
		return s;
	}

	/**
	 * Parses a formatted String and returns the value in milliseconds. You can
	 * use one of the following suffixes:
	 *
	 * <pre>
	 *     s - seconds
	 *     m - minutes
	 *     h - hours
	 *     D - days
	 *     W - weeks
	 *     M - months
	 *     Y - years
	 * </pre>
	 */
	public static long parseMillis(String s) {
		if (s == null) {
			return 0;
		}
		long millis = 0;
		int i = 0;
		int length = s.length();
		while (i < length) {
			long delta = 0;
			char ch = 0;
			for (; i < length; i++) {
				ch = s.charAt(i);
				if (!Character.isDigit(ch)) {
					i++;
					break;
				}
				delta *= 10;
				delta += Character.getNumericValue(ch);
			}
			switch (ch) {
			case 's':
			case 'S':
			default:
				millis += 1000 * delta;
				break;

			case 'm':
				millis += 60 * 1000 * delta;
				break;

			case 'h':
			case 'H':
				millis += 60L * 60 * 1000 * delta;
				break;

			case 'd':
			case 'D':
				millis += 24L * 60 * 60 * 1000 * delta;
				break;

			case 'w':
			case 'W':
				millis += 7L * 24 * 60 * 60 * 1000 * delta;
				break;

			case 'M':
				millis += 30L * 24 * 60 * 60 * 1000 * delta;
				break;

			case 'y':
			case 'Y':
				millis += 365L * 24 * 60 * 60 * 1000 * delta;
				break;
			}
		}
		return millis;
	}

	/**
	 * Returns a formatted string using the pattern hh:mm:ss. The hours are
	 * omitted if they are zero, the minutes are padded with a '0' character
	 * if they are less than 10.
	 */
	public static String formatMillis(long millis) {
		int hours = (int) (millis / (1000 * 60 * 60));
		int minutes = (int) (millis / (1000 * 60)) % 60;
		int seconds = (int) (millis / 1000) % 60;
		StringBuilder sb = new StringBuilder();
		if (hours > 0) {
			sb.append(hours);
			sb.append(':');
		}
		if (minutes < 10 && hours > 0) {
			sb.append(0);
		}
		sb.append(minutes);
		sb.append(':');
		if (seconds < 10) {
			sb.append(0);
		}
		sb.append(seconds);
		return sb.toString();
	}
	
	/**
	 * Strips directory names and the query-string from a path.
	 * <p>
  	 * <b>Example:</b>
     * <code>"/hello/world.html?foo=bar"</code> -&gt; <code>"world.html"</code>
	 */
	public static String baseName(String path) {
		int begin = path.lastIndexOf('/') + 1;
		int end = path.indexOf(';');
		if (end == -1) {
			end = path.indexOf('?');
			if (end == -1) {
				end = path.length();
			}
		}
		return path.substring(begin, end);
	}

	/**
	 * Returns the extension of the given filename. Examples:
	 *
	 * <pre>
	 *   &quot;foo.bar&quot; - &quot;bar&quot;
	 *   &quot;/some/file.name.foo&quot; - &quot;foo&quot;
	 * </pre>
	 *
	 * The following examples will return an empty String:
	 *
	 * <pre>
	 *   &quot;foo&quot;
	 *   &quot;foo.&quot;
	 *   &quot;/dir.with.dots/file&quot;
	 *   &quot;.bar&quot;
	 *   &quot;/foo/.bar&quot;
	 * </pre>
	 */
	public static String getExtension(String filename) {
		if (filename == null) {
			return "";
		}
		int i = filename.lastIndexOf('.');
		if (i <= 0 || i == filename.length() - 1
				|| filename.indexOf('/', i) != -1
				|| filename.indexOf('\\', i) != -1) {

			return "";
		}
		return filename.substring(i + 1);
	}

	/**
	 * Returns the the filename without it's extension.
	 */
	public static String stripExtension(String filename) {
		String extension = getExtension(filename);
		if (extension.length() == 0) {
			return filename;
		}
		return filename.substring(0, filename.length() - extension.length() - 1);
	}

	/**
	 * Returns the the path without a leading slash.
	 * @since 7.0
	 */
	public static String stripLeadingSlash(String path) {
		if (path != null && path.startsWith("/")) {
			path = path.substring(1);
		}
		return path;
	}

	/**
	 * Returns the the path without a trailing slash.
	 * @since 7.0
	 */
	public static String stripTrailingSlash(String path) {
		if (path != null && path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}
	
	/**
	 * Parses a formatted String and returns the date. The date to parse starts
	 * with today. You can use one of the following sufixes:
	 *
	 * <pre>
	 *
	 *     D - days
	 *     W - weeks
	 *     M - months
	 *     Y - years
	 * </pre>
	 *
	 * Days is option, any number without a suffix is treated as a number of
	 * days
	 */
	public static Date parseDate(String s) {
		String op = null;
		int days = 0;
		int months = 0;
		int years = 0;
		
		if (s.startsWith("today")) {
			s = s.substring(5);
		}
		int i = 0;
		int length = s.length();
		long delta = 0;
		while (i < length) {

			char ch = 0;
			for (; i < length; i++) {
				ch = s.charAt(i);
				if (!Character.isDigit(ch)) {
					i++;
					break;
				}
				delta *= 10;
				delta += Character.getNumericValue(ch);
			}
			switch (ch) {
			case '+':
				op = OP_ADDITION;
				break;
			case '-':
				op = OP_SUBTRACTION;
				break;
			case 'd':
			case 'D':
				if (OP_ADDITION.equals(op)) {
					days += delta;
				}
				else if (OP_SUBTRACTION.equals(op)) {
					days -= delta;
				}
				op = null;
				delta = 0;
				break;

			case 'w':
			case 'W':
				if (OP_ADDITION.equals(op)) {
					days += 7 * delta;
				}
				else if (OP_SUBTRACTION.equals(op)) {
					days -= 7 * delta;
				}
				op = null;
				delta = 0;
				break;

			case 'M':
				if (OP_ADDITION.equals(op)) {
					months += delta;
				}
				else if (OP_SUBTRACTION.equals(op)) {
					months -= delta;
				}
				op = null;
				delta = 0;
				break;

			case 'y':
			case 'Y':
				if (OP_ADDITION.equals(op)) {
					years += delta;
				}
				else if (OP_SUBTRACTION.equals(op)) {
					years -= delta;
				}
				op = null;
				delta = 0;
				break;
			}
			if (delta > 0) {
				if (OP_ADDITION.equals(op)) {
					days += delta;
				}
				else if (OP_SUBTRACTION.equals(op)) {
					days -= delta;
				}
			}

		}
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, days);
		c.add(Calendar.MONTH, months);
		c.add(Calendar.YEAR, years);
		return c.getTime();
	}

	public static String formatDate(Date date, String pattern, Locale locale) {
		return new SimpleDateFormat(pattern, locale).format(date);
	}
	
	public static String formatMediumDate(Date date, Locale locale) {
		return SimpleDateFormat.getDateInstance(
				SimpleDateFormat.MEDIUM, locale).format(date);
	}
	
	public static String formatIsoDate(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
	}
	
	/**
	 * Returns the date format for the given locale.
	 * Examples:
	 *
	 * <pre>
	 *  Locale.ENGLISH - &quot;MM/DD/YYYY&quot;
	 *  Locale.GERMAN - &quot;DD.MM.YYYY&quot;
	 * </pre>
	 * 	 
	 */
	public static String getDateFormat(Locale locale) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, 24);
		c.set(Calendar.MONTH, Calendar.DECEMBER);
		c.set(Calendar.YEAR, 1970);
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		return df.format(c.getTime())
				.replaceAll("\\s+", "")
				.replace("12", "MM")
				.replace("1970", "YYYY")				
				.replace("24", "DD")
				.replace("70", "YYYY");
	}
	
	/**
	 * Returns the date delimiter for the given date format
	 * Examples:
	 *
	 * <pre>
	 *   &quot;MM/DD/YYYY&quot; - &quot;/&quot;
	 *   &quot;DD.MM.YYYY&quot; - &quot;.&quot;
	 * </pre>
	 * 	 
	 */
	public static String getDateDelimiter(String dateFormat) {		
		Matcher m = DATE_DELIMITER_PATTERN.matcher(dateFormat);
		if (m.matches()) {				
			return m.group(1);
		}
		return null;
	}

	/**
	 * Invokes toLowerCase(), converts all whitespaces to underscores and
	 * removes all characters other than a-z, 0-9, dot, underscore or minus.
	 */
	public static String toFilename(String s) {
		s = s.toLowerCase();
		s = s.replaceAll("\\s+", "_");
		s = s.replaceAll("[^a-z_0-9.-]", "");
		return s;
	}

	/**
	 * Turn special characters into escaped characters conforming to XML.
	 *
	 * @param input the input string
	 * @return the escaped string
	 */
	public static String xmlEscape(String input) {
		if (input == null) {
			return input;
		}
		StringBuffer filtered = new StringBuffer(input.length() + 42);
		char c;
		for (int i = 0; i < input.length(); i++) {
			c = input.charAt(i);
			if (c == '<') {
				filtered.append(ENTITY_LT);
			}
			else if (c == '>') {
				filtered.append(ENTITY_GT);
			}
			else if (c == '&') {
				filtered.append(ENTITY_AMP);
			}
			else if (c == '"') {
				filtered.append(ENTITY_QUOT);
			}
			else {
				filtered.append(c);
			}
		}
		return filtered.toString();
	}

	public static String escapeChars(String s, String chars, char escape) {
		StringBuffer sb = new StringBuffer(s);
		for (int i = 0; i < sb.length(); i++) {
			if (chars.indexOf(sb.charAt(i)) != -1) {
				sb.insert(i, escape);
				i++;
			}
		}
		return sb.toString();
	}

	/**
	 * Translates the given string into application/x-www-form-urlencoded
	 * format using UTF-8 as encoding scheme.
	 */
	public static String uriEscape(String input) {
		if (input == null) {
			return null;
		}
		try {
			return URLEncoder.encode(input, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e.getMessage());
		}
	}

	/**
	 * Translates the given path into application/x-www-form-urlencoded
	 * format using UTF-8 as encoding scheme. This method differs from
	 * {@link #uriEscape(String)}} that path component separators (/) are
	 * not encoded.
	 * 
	 * @see #uriEscape(String)
	 */
	public static String uriEscapePath(String input) {
		StringBuffer result = new StringBuffer();
		
		int p = 0;
		int q = input.indexOf('/');
		
		while (q >= 0) {
			result.append(uriEscape(input.substring(p, q)));
			p = q + 1;
			result.append('/');
			
			q = input.indexOf('/', p);
		}
		
		result.append(uriEscape(input.substring(p, input.length())));
		return result.toString();
	}

	/**
	 * Decodes the given application/x-www-form-urlencoded string using
	 * UTF-8 as encoding scheme.
	 */
	public static String uriUnescape(String input) {
		try {
			return URLDecoder.decode(input, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e.getMessage());
		}
	}

	/**
	 * Escapes all XML special characters in the given array. Dates and
	 * primitive-wrappers are left as-is, all other objects are converted to
	 * their String representation and escaped using
	 * {@link #xmlEscape(String)}.
	 * @since 6.4
	 */
	public static Object[] htmlEscapeArgs(Object[] args) {
		Object[] escapedArgs = new Object[args.length];
		escapedArgs = new Object[args.length];
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			if (arg instanceof String) {
				escapedArgs[i] = xmlEscape((String) arg);
			}
			else if (ClassUtils.isPrimitiveWrapper(arg.getClass())
					|| arg instanceof Date) {

				escapedArgs[i] = arg;
			}
			else {
				escapedArgs[i] = xmlEscape(arg.toString());
			}
		}
		return escapedArgs;
	}

	/**
	 * Extracts an integer from a String using the first capture group of the
	 * given regular expression.
	 * @since 6.4
	 */
	public static int extractInt(String s, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(s);
		if (matcher.find()) {
			String group = matcher.group(1);
			try {
				return Integer.parseInt(group);
			}
			catch (NumberFormatException e) {
				getLog().error("Not a valid number: " + group);
			}
		}
		return -1;
	}

	/**
	 * Strips whitespaces without preserving line breaks.
	 * @see #stripWhitespaces(String, boolean) 
	 */
	public static String stripWhitespaces(String s) {
		return stripWhitespaces(s, false);
	}

	/**
	 * Replaces consecutive whitespaces by a single space character.
	 * @param s The String to tidy up
	 * @param preserveBreaks Whether line breaks should be preserved 
	 */
	public static String stripWhitespaces(String s, boolean preserveBreaks) {
		if (s == null) {
			return null;
		}
		StringReader in = new StringReader(s);
		StringBuilder sb = new StringBuilder();
		try {
			boolean lineBreak = false;
			boolean charsWritten = false;
			int count = 0;
			int i;
			while ((i = in.read()) != -1) {
				char c = (char) i;
				if (Character.isWhitespace(c)) {
					if (charsWritten) {
						count++;
						if (preserveBreaks && c == '\n') {
							lineBreak = true;
						}
					}
				}
				else {
					if (count > 0) {
						sb.append(lineBreak ? '\n' : ' ');
						count = 0;
						lineBreak = false;
					}
					sb.append(c);
					charsWritten = true;
				}
			}
		}
		catch (IOException e) {
			// Should never happen since we are using a StringReader
		}
		finally {
			in.close();
		}
		return sb.toString();
	}
	
	/**
	 * XML-escapes dangling ampersands that are no entities.  
	 * @since 8.0
	 */
	public static String xmlEscapeDanglingAmps(String s) {
		if (s == null) {
			return null;
		}
		return DANGLING_AMP_PATTERN.matcher(s).replaceAll("&amp;");
	}
	
	/**
	 * Removes all markup from the given String using the pattern
	 * <code>"&lt;/?[^&gt;]+&gt;"</code>.
	 * @since 7.0
	 */
	public static String stripTags(String s) {
		if (s == null) {
			return null;
		}
		return TAG_PATTERN.matcher(s).replaceAll("");
	}
	
	/**
	 * Convenience method that strips tags and whitespaces.
	 * @see #stripTags(String)
	 * @see #stripWhitespaces(String)
	 */
	public static String stripTagsAndSpaces(String s) {
		return stripWhitespaces(stripTags(s));
	}
	
	/**
	 * Calls {@link StringUtils#cleanPath(String)} and removes all occurrences
	 * of "../".
	 * @since 7.0
	 */
	public static String sanitizePath(String s) {
		if (s == null) {
			return null;
		}
		return PARENT_DIR_PATTERN.matcher(StringUtils.cleanPath(s)).replaceAll("");
	}
	
	/**
	 * Repeats a String the given number of times.
	 * @since 8.0 
	 */
	public static String repeat(String s, int times) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < times; i++) {
			sb.append(s);
		}
		return sb.toString();
	}

	/**
	 * Returns the given object as JSON String.
	 */
	public static String toJSON(Object obj) {
		return JSONObject.fromObject(obj).toString();
	}
}
