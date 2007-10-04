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
package org.riotfamily.common.web.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.FormatUtils;
import org.springframework.beans.PropertyAccessor;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class AttributePattern {
	
	public static final String EXPOSED_ATTRIBUTES = 
			AttributePattern.class.getName() + ".exposedAttributes";

	private static final Pattern ATTRIBUTE_NAME_PATTERN =
			Pattern.compile("@\\{(.+?)((\\*)|\\:(.*?))?\\}");

	private static final Pattern STAR_PATTERN =
			Pattern.compile("\\\\\\*");

	private static final Pattern DOUBLE_STAR_PATTERN =
			Pattern.compile("\\\\\\*\\\\\\*");

	private String attributePattern;
	
	private Pattern pattern;

	private ArrayList attributeNames;
	
	private ArrayList attributeTypes;

	public AttributePattern(String attributePattern) {
		this.attributePattern = attributePattern;
		attributeNames = new ArrayList();
		attributeTypes = new ArrayList();
		Matcher m = ATTRIBUTE_NAME_PATTERN.matcher(attributePattern);
		while (m.find()) {
			attributeNames.add(m.group(1));
			attributeTypes.add(m.group(4));
		}
		pattern = Pattern.compile(convertAttributePatternToRegex(attributePattern));
	}

	public int getNumberOfWildcards() {
		return attributeNames.size();
	}
	
	// Example pattern: /resources/*/@{resource*}
	private String convertAttributePatternToRegex(final String antPattern) {
		String regex = FormatUtils.escapeChars(antPattern, "()", '\\'); // ... just in case
		regex = ATTRIBUTE_NAME_PATTERN.matcher(antPattern).replaceAll("(*$3)"); // /resources/*/(**)
		regex = "^" + FormatUtils.escapeChars(regex, ".+*?{^$", '\\') + "$"; // ^/resources/\*/(\*\*)$
		regex = DOUBLE_STAR_PATTERN.matcher(regex).replaceAll(".*?"); // ^/resources/\*/(.*?)$
		regex = STAR_PATTERN.matcher(regex).replaceAll("[^/]*"); // ^/resources/[^/]*/.*?$
		return regex;
	}

	public void expose(String urlPath, HttpServletRequest request) {
		Map attributes = new HashMap();
		Matcher m = pattern.matcher(urlPath);
		Assert.isTrue(m.matches());
		for (int i = 0; i < getNumberOfWildcards(); i++) {
			String s = m.group(i + 1);
			String type = (String) attributeTypes.get(i);
			String name = (String) attributeNames.get(i);
			Object value = convert(s, type);
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
	public boolean matches(Map attributes) {
		if (attributes != null) {
			Collection names = attributes.keySet();
			return names.size() == getNumberOfWildcards() &&
				attributeNames.containsAll(names);
		}
		else {
			return attributeNames.isEmpty();
		}
	}

	public boolean startsWith(String prefix) {
		return attributePattern.startsWith(prefix);
	}
	
	public String fillInAttributes(PropertyAccessor attributes) {
		StringBuffer url = new StringBuffer();
		Matcher m = ATTRIBUTE_NAME_PATTERN.matcher(attributePattern);
		while (m.find()) {
			String name = m.group(1);
			Object value = attributes.getPropertyValue(name);
			if (value == null) {
				return null;
			}
			String replacement = StringUtils.replace(value.toString(), "$", "\\$");
			m.appendReplacement(url, replacement);
		}
		m.appendTail(url);
		return url.toString();
	}
	
	public String fillInAttribute(Object value) {
		Assert.state(getNumberOfWildcards() == 1, 
				"Pattern must contain exactly one wildcard.");
		
		String replacement = StringUtils.replace(value.toString(), "$", "\\$");
		Matcher m = ATTRIBUTE_NAME_PATTERN.matcher(attributePattern);
		return m.replaceFirst(replacement);
	}
	
	public String toString() {
		return attributePattern;
	}
	
	public static String convertToAntPattern(String urlPattern) {
		return ATTRIBUTE_NAME_PATTERN.matcher(urlPattern).replaceAll("*$3");
	}

}