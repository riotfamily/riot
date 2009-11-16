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
package org.riotfamily.common.beans.config;

import java.util.Properties;

import org.springframework.beans.factory.InitializingBean;

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
public class PlaceholderWithDefaultConfigurer extends PropertiesPlaceholderConfigurer
		implements InitializingBean {

	public static final String DEFAULT_VALUE_SEPARATOR = "=";
	
	private static final String NULL_DEFAULT = 
			PlaceholderWithDefaultConfigurer.class.getName() + ".NULL_DEFAULT";
	
	private String valueSeparator = DEFAULT_VALUE_SEPARATOR;

	private String nullValue;
	
	
	public void setValueSeparator(String valueSeparator) {
		this.valueSeparator = valueSeparator;
	}
	
	@Override
	public void setNullValue(String nullValue) {
		super.setNullValue(nullValue);
		this.nullValue = nullValue;
	}
	
	public void afterPropertiesSet() throws Exception {
		if (nullValue == null) {
			setNullValue(NULL_DEFAULT);
		}
	}
	
	@Override
	protected String resolvePlaceholder(String placeholder, Properties props, 
			int systemPropertiesMode) {
		
		String defaultValue = null;
		int i = placeholder.indexOf(valueSeparator);
		if (i != -1) {
			if (i + 1 < placeholder.length()) {
				defaultValue = placeholder.substring(i + 1);
			}
			else {
				defaultValue = nullValue;
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
	
}
