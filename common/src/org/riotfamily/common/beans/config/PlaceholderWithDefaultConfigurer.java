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
package org.riotfamily.common.beans.config;

import java.util.HashSet;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.StringValueResolver;

/**
 * PropertyPlaceholderConfigurer that allows to define inline default values.
 * <p>
 * Example:
 * <pre>
 * &lt;bean class="org.riotfamily.example.HelloWorld"&gt;
 *     &lt;property name="message" value="${hello.message=Hello World}" /&gt;
 * &lt;/bean&gt;
 * </pre>
 * Since Riot 7.0 you can specify <code>null</code> as default value:
 * <pre>
 * &lt;bean class="org.riotfamily.example.HelloWorld"&gt;
 *     &lt;property name="message" value="${hello.message=}" /&gt;
 * &lt;/bean&gt;
 * </pre>
 * Please note the the trailing equals sign. If omitted, the behavior will
 * depend on the setting of the {@link #setIgnoreUnresolvablePlaceholders(boolean) 
 * ignoreUnresolvablePlaceholders} flag. 
 */
public class PlaceholderWithDefaultConfigurer 
		extends PropertiesPlaceholderConfigurer {

	public static final String DEFAULT_VALUE_SEPARATOR = "=";
	
	private static final String NULL_DEFAULT = 
			PlaceholderWithDefaultConfigurer.class.getName() + ".NULL_DEFAULT";
	
	private String valueSeparator = DEFAULT_VALUE_SEPARATOR;
	
	private String beanName;

	private BeanFactory beanFactory;
	
	public void setValueSeparator(String valueSeparator) {
		this.valueSeparator = valueSeparator;
	}
	
	public void setBeanName(String beanName) {
		this.beanName = beanName;
		super.setBeanName(beanName);
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		super.setBeanFactory(beanFactory);
	}
	
	protected void processProperties(
			ConfigurableListableBeanFactory beanFactoryToProcess, 
			Properties props) throws BeansException {
	
		StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(props);
		BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);
		
		String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
		for (int i = 0; i < beanNames.length; i++) {
			// Check that we're not parsing our own bean definition,
			// to avoid failing on unresolvable placeholders in properties file locations.
			if (!(beanNames[i].equals(this.beanName) && beanFactoryToProcess.equals(this.beanFactory))) {
				BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(beanNames[i]);
				try {
					visitor.visitBeanDefinition(bd);
				}
				catch (BeanDefinitionStoreException ex) {
					throw new BeanDefinitionStoreException(bd.getResourceDescription(), beanNames[i], ex.getMessage());
				}
			}
		}
		
		// New in Spring 2.5: resolve placeholders in alias target names and aliases as well.
		beanFactoryToProcess.resolveAliases(valueResolver);
	}
	
	protected String resolvePlaceholder(String placeholder, Properties props, 
			int systemPropertiesMode) {
		
		String defaultValue = null;
		int i = placeholder.indexOf(valueSeparator);
		if (i != -1) {
			if (i + 1 < placeholder.length()) {
				defaultValue = placeholder.substring(i + 1);
			}
			else {
				defaultValue = NULL_DEFAULT;
			}
			placeholder = placeholder.substring(0, i); 
		}
		String value = super.resolvePlaceholder(placeholder, props, 
				systemPropertiesMode);
		
		if (value == null) {
			value = defaultValue;
		}
		
		return value;
	}
	
	/**
	 * BeanDefinitionVisitor that resolves placeholders in String values,
	 * delegating to the <code>parseStringValue</code> method of the
	 * containing class.
	 */
	private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

		private final Properties props;

		public PlaceholderResolvingStringValueResolver(Properties props) {
			this.props = props;
		}

		public String resolveStringValue(String strVal) throws BeansException {
			String value = parseStringValue(strVal, this.props, new HashSet());
			if (NULL_DEFAULT.equals(value)) {
				return null;
			}
			return value;
		}
	}

}
