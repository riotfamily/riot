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
package org.riotfamily.common.io;

import java.io.Reader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * FilterReader that replaces tokens with values form a Properties instance.
 */
public class PropertyFilterReader extends AbstractTokenFilterReader {

	private Map<String, String> properties;
	
	public PropertyFilterReader(Reader in) {
		super(in);
	}
	
	public PropertyFilterReader(Reader in, Map<String, String> properties) {
		super(in);
		this.properties = properties;
	}
	
	public void setProperties(Properties properties) {
		this.properties = new HashMap<String, String>();
		Enumeration<?> names = properties.propertyNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			this.properties.put(name, properties.getProperty(name));
		}
	}

	public String getReplacement(String key) {
		return properties.get(key);
	}
}
