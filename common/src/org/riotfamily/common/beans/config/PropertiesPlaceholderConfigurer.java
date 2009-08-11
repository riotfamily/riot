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
 * &lt;bean class="org.riotfamily.common.hibernate.RiotSessionFactoryBean"&gt;
 *   &lt;property name="hibernateProperties" value="${riot.(hibernate.*)}" /&gt;
 * &lt;/bean&gt;
 * </pre>
 *
 * The configurer will look for all properties start start with 
 * '<code>riot.hibernate.</code>'. So having a properties file like this ...
 * 
 * <pre>
 * riot.hibernate.cache.use_query_cache = true
 * riot.hibernate.show_sql = false
 * </pre>
 * 
 * ... would be equivalent to writing:
 * <pre>
 * &lt;bean class="org.riotfamily.common.hibernate.RiotSessionFactoryBean"&gt;
 *   &lt;property name="hibernateProperties"&gt;
 *     &lt;value&gt;
 *       hibernate.cache.use_query_cache = true
 *       hibernate.show_sql = false
 *     &lt/value&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
 * 
 * Spring's {@link PropertiesEditor} will then take care of converting the 
 * String value into a <code>java.util.Properties</code> object.
 * 
 * <p>
 * Since Riot 9.0, the placeholder may contain parenthesis to indicate that 
 * a part of the placeholder should be included in the resulting keys. 
 * For example the placeholder <code>${riot.(hibernate.*)}</code> will result 
 * in keys that all start with <code>"hibernate."</code>. If the parenthesis 
 * were omitted, only the part matched by the asterisk would be used as key.
 *   
 * @author Felix Gnass [fgnass at neteye dot de]
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
	
	protected String resolveAll(Properties props, String match) {
		String prefix = match;
		int i = match.indexOf('(');
		if (i != -1) {
			if (i > match.length()-1) {
				prefix = match.substring(i + 1);
			}
			else {
				prefix = "";
			}
			match = match.substring(0, i) + prefix;
		}
		
		StringBuffer sb = new StringBuffer();
		Enumeration<?> names = props.propertyNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			if (name.startsWith(match) && name.length() > match.length()) {
				sb.append(prefix);
				sb.append(name.substring(match.length()));
				sb.append('=');
				sb.append(props.getProperty(name));
				sb.append('\n');
			}
		}
		return sb.toString();
	}
}
