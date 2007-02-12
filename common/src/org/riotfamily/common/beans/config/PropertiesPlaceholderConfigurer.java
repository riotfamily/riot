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
package org.riotfamily.common.beans.config;

import java.util.Enumeration;
import java.util.Properties;

import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.web.context.support.ServletContextPropertyPlaceholderConfigurer;

/**
 * PropertyPlaceholderConfigurer that accepts wildcards to populate properties
 * that expect a java.util.Properties value.
 * <p>
 * Example:
 * <pre>
 * &lt;bean class="org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer"&gt;
 *   &lt;property name="freemarkerSettings" ref="${freemarker.*}" /&gt;
 * &lt;/bean&gt;
 * </pre>
 *
 * The configurer will look for all properties start start with 
 * '<code>freemarker.</code>'. So having a properties file like this ...
 * 
 * <pre>
 * freemarker.number_format = 0.######
 * freemarker.locale = en_US
 * </pre>
 * 
 * ... would be equivalent to writing:
 * <pre>
 * &lt;bean class="org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer"&gt;
 *   &lt;property name="freemarkerSettings"&gt;
 *     &lt;value&gt;
 *       number_format = 0.######
 *       locale = en_US
 *     &lt/value&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
 * 
 * Spring's {@link PropertiesEditor} will then take care of converting the 
 * String value into a <code>java.util.Properties</code> object.
 * 
 * @author Felix Gnass <fgnass@neteye.de>
 * @since 6.4
 */
public class PropertiesPlaceholderConfigurer extends 
		ServletContextPropertyPlaceholderConfigurer {
	
	protected String resolvePlaceholder(String placeholder, Properties props) {	
		int i = placeholder.indexOf('*');
		if (i != -1) {
			return resolveAll(props, placeholder.substring(0, i));
		}
		return super.resolvePlaceholder(placeholder, props);
	}
	
	protected String resolveAll(Properties props, String prefix) {
		StringBuffer sb = new StringBuffer();
		Enumeration names = props.propertyNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			if (name.startsWith(prefix) && name.length() > prefix.length()) {
				sb.append(name.substring(prefix.length()));
				sb.append('=');
				sb.append(props.getProperty(name));
				sb.append('\n');
			}
		}
		return sb.toString();
	}
}
