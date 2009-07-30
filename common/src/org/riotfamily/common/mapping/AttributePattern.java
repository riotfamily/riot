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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.beans.property.MapWrapper;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.springframework.beans.PropertyAccessor;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class AttributePattern {
	
	/**
	 * Name of the request attribute under which a Map of matched wildcards is exposed.
	 */
	public static final String EXPOSED_ATTRIBUTES = 
			AttributePattern.class.getName() + ".exposedAttributes";

	/**
	 * Pattern to match wildcards of the form <code>@{name:type}</code>.
	 */
	private static final Pattern ATTRIBUTE_NAME_PATTERN =
			Pattern.compile("@\\{(.+?)((\\*)|\\:(.*?))?\\}");

	/**
	 * Pattern to match a single star.
	 */
	private static final Pattern STAR_PATTERN =
			Pattern.compile("\\\\\\*");

	/**
	 * Pattern to match two stars.
	 */
	private static final Pattern DOUBLE_STAR_PATTERN =
			Pattern.compile("/?\\\\\\*\\\\\\*");
	
	/**
	 * Pattern that matches named and unanmed wildcards (stars). This pattern is used
	 * to determine how precise a match is.
	 */
	private static final Pattern WILDCARD_PATTERN = Pattern.compile(
			"(" + STAR_PATTERN + "|" + ATTRIBUTE_NAME_PATTERN + ")");
	
	/**
	 * The string that was used to construct the pattern.
	 */
	private String attributePattern;
	
	/**
	 * A regular expression built from the string pattern.
	 */
	private Pattern pattern;

	/**
	 * List of attribute names contained in the pattern.
	 */
	private ArrayList<String> attributeNames;
	
	/**
	 * List of types.
	 */
	private ArrayList<String> attributeTypes;
	
	/**
	 * Precision of the pattern. The more non-wildcard characters a pattern contains,
	 * the more precise it is.
	 */
	private int precision;

	public AttributePattern(String attributePattern) {
		this.attributePattern = attributePattern;
		attributeNames = new ArrayList<String>();
		attributeTypes = new ArrayList<String>();
		Matcher m = ATTRIBUTE_NAME_PATTERN.matcher(attributePattern);
		while (m.find()) {
			attributeNames.add(m.group(1));
			attributeTypes.add(m.group(4));
		}
		pattern = Pattern.compile(convertAttributePatternToRegex(attributePattern));
		precision = WILDCARD_PATTERN.matcher(attributePattern).replaceAll("").length();
	}

	public List<String> getAttributeNames() {
		return attributeNames;
	}

	// Example pattern: @{sitePrefix*}/resources/*/@{resource*}
	private String convertAttributePatternToRegex(final String antPattern) {
		String regex = FormatUtils.escapeChars(antPattern, "()", '\\'); // ... just in case
		regex = ATTRIBUTE_NAME_PATTERN.matcher(antPattern).replaceAll("(*$3)"); // (**)/resources/*/(**)
		regex = "^" + FormatUtils.escapeChars(regex, ".+*?{^$", '\\') + "$"; // ^(\*\*)/resources/\*/(\*\*)$
		regex = DOUBLE_STAR_PATTERN.matcher(regex).replaceAll(".*?"); // ^(.*?)/resources/\*/(.*?)$
		regex = STAR_PATTERN.matcher(regex).replaceAll("[^/]*"); // ^(.*?)/resources/[^/]*/(.*?)$
		return regex;
	}

	public void expose(String urlPath, HttpServletRequest request) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		Matcher m = pattern.matcher(urlPath);
		Assert.isTrue(m.matches());
		for (int i = 0; i < attributeNames.size(); i++) {
			String name = attributeNames.get(i);
			Object value = null;
			String s = m.group(i + 1);
			if (s.length() > 0) {
				String type = attributeTypes.get(i);
				value = convert(s, type);
			}
			request.setAttribute(name, value);
			attributes.put(name, value);
		}
		request.setAttribute(EXPOSED_ATTRIBUTES, attributes);
	}

	private Object convert(String s, String type) {
		if (type == null || type.equalsIgnoreCase("String")) {
			return s;
		}
		if (type.equalsIgnoreCase("Integer")) {
			return Integer.valueOf(s);
		}
		else if (type.equalsIgnoreCase("Long")) {
			return Long.valueOf(s);
		}
		else if (type.equalsIgnoreCase("Short")) {
			return Short.valueOf(s);
		}
		else if (type.equalsIgnoreCase("Double")) {
			return Double.valueOf(s);
		}
		else if (type.equalsIgnoreCase("Float")) {
			return Float.valueOf(s);
		}
		else if (type.equalsIgnoreCase("Boolean")) {
			return Boolean.valueOf(s);
		}
		else if (type.equalsIgnoreCase("Character")) {
			return new Character(s.charAt(0));
		}
		else {
			throw new IllegalArgumentException("Unsupported type: " + type 
					+ " - must be Integer, Long, Short, Double, Float," 
					+ " Boolean or Character");
		}
	}
	
	public boolean canFillIn(Map<String, ?> required, Map<String, ?> optional, int anonymous) {
		if (required == null) {
			required = Collections.emptyMap();
		}
		
		if (optional == null) {
			optional = Collections.emptyMap();
		}

		Set<String> requiredNames = Generics.newHashSet();
		for (Entry<String, ?> entry : required.entrySet()) {
			if (entry.getValue() != null) {
				requiredNames.add(entry.getKey());
			}
		}
		
		Set<String> optionalNames = Generics.newHashSet();
		for (Entry<String, ?> entry : optional.entrySet()) {
			if (entry.getValue() != null) {
				optionalNames.add(entry.getKey());
			}
		}
		
		return canFillIn(requiredNames, optionalNames, anonymous);
	}
	
	public boolean canFillIn(Set<String> required, Set<String> optional, int anonymous) {
		if (required == null) {
			required = Collections.emptySet();
		}
		if (optional == null) {
			optional = Collections.emptySet();
		}
		if (CollectionUtils.isEmpty(attributeNames)) {
			return required.isEmpty() && anonymous == 0;
		}
		if (!attributeNames.containsAll(required)) {
			return false;
		}
		int unmatched = 0;
		for (String name : attributeNames) { 
			if (!(required.contains(name) || optional.contains(name))) {
				if (++unmatched > anonymous) {
					return false;
				}
			}
		}
		return unmatched == anonymous;
	}

	public boolean matches(String path) {
		return pattern.matcher(path).matches();
	}
	
	public boolean startsWith(String prefix) {
		return attributePattern.startsWith(prefix);
	}
	
	public boolean isMoreSpecific(AttributePattern other) {
		return other == null || precision > other.precision;
	}
	
	public String fillInAttributes(PropertyAccessor attributes) {
		return fillInAttributes(attributes, null);
	}
	
	public String fillInAttributes(Map<String, ?> attributes, Map<String, ?> defaults) {
		return fillInAttributes(new MapWrapper(attributes), defaults);
	}
	
	public String fillInAttributes(PropertyAccessor attributes, Map<String, ?> defaults) {
		StringBuffer url = new StringBuffer();
		Matcher m = ATTRIBUTE_NAME_PATTERN.matcher(attributePattern);
		while (m.find()) {
			String name = m.group(1);
			Object value = null;
			if (attributes != null && attributes.isReadableProperty(name)) {
				value = attributes.getPropertyValue(name);
			}
			if (value == null && defaults != null) {
				value = defaults.get(name);
			}
			String replacement = value != null
					? StringUtils.replace(value.toString(), "$", "\\$")
					: null;
					
			if ("*".equals(m.group(2))) {
				if (replacement != null) {
					m.appendReplacement(url, FormatUtils.uriEscapePath(replacement));
				}
			} 
			else {
				if (replacement != null) {
					m.appendReplacement(url, FormatUtils.uriEscape(replacement));
				}
			}
		}
		m.appendTail(url);
		return url.toString();
	}
	
	public String fillInAttribute(Object value) {
		return fillInAttribute(value, null);
	}
	
	public String fillInAttribute(Object value, Map<String, ?> defaults) {
		Matcher m = ATTRIBUTE_NAME_PATTERN.matcher(attributePattern);
		String unmatched = null;
		while (m.find()) {
			String name = m.group(1);
			if (defaults == null || !defaults.containsKey(name)) {
				if (unmatched != null) {
					throw new IllegalStateException("Pattern has more than one " 
							+ "unmatched wildcard.");
				}
				unmatched = name;
			}
		}
		
		if (value != null) {
			Map<String, Object> map;
			if (unmatched == null) {
				if (attributeNames.size() != 1) {
					throw new IllegalStateException("No unmatched wildcard, "
							+ "don't know which default should be overwritten.");
				}
				String name = attributeNames.get(0);
				map = Collections.singletonMap(name, value);
			}
			else {
				map = Collections.singletonMap(unmatched, value);
			}
			return fillInAttributes(map, defaults);
		}
		else {
			return fillInAttributes((PropertyAccessor) null, defaults);
		}
	}
	
	public String fillInAttributes(Object[] values) {
		Matcher m = ATTRIBUTE_NAME_PATTERN.matcher(attributePattern);
		Map<String, Object> map = Generics.newHashMap();
		int i = 0;
		while (m.find()) {
			if (i >= values.length) {
				throw new IllegalStateException("Pattern contains more " +
						"wildcards than elements in the given array.");
			}
			String name = m.group(1);
			map.put(name, values[i++]);
		}
		return fillInAttributes(map, null);
	}
	
	public String toString() {
		return attributePattern;
	}

}