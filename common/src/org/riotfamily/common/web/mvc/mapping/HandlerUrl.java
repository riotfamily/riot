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
package org.riotfamily.common.web.mvc.mapping;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.riotfamily.common.util.Generics;
import org.springframework.beans.PropertyAccessor;

public class HandlerUrl implements Comparable<HandlerUrl> {
	
	private static Pattern placeholders = Pattern.compile(
			"(?:(\\*\\*?)|\\{(.+?)(?::.+?)?\\})");
	
	private final String path;
	
	private final Set<String> variables = Generics.newHashSet();
	
	private int numberOfPlaceholders;
	
	public HandlerUrl(String path) {
		this.path = path;
		Matcher m = placeholders.matcher(path);
		while (m.find()) {
			String name = m.group(2);
			if (name != null) {
				variables.add(name);
			}
			else {
				variables.add(m.group(1));
			}
			numberOfPlaceholders++;
		}
	}
	
	public boolean canFillIn(Collection<?> values) {
		int provided = values != null ? values.size() : 0;
		return provided == numberOfPlaceholders;
	}
	
	public boolean canFillIn(PropertyAccessor pa) {
		if (pa == null) {
			return numberOfPlaceholders == 0;
		}
		for (String prop : variables) {
			if (!pa.isReadableProperty(prop) || pa.getPropertyValue(prop) == null) {
				return false;
			}
		}
		return true;
	}
	
	public String fillIn(Collection<?> values) {
		Matcher m = placeholders.matcher(path);
		StringBuffer sb = new StringBuffer();
		Iterator<?> it = values.iterator();
		while (m.find()) {
			m.appendReplacement(sb , String.valueOf(it.next()));
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	public String fillIn(PropertyAccessor pa) {
		Matcher m = placeholders.matcher(path);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String name = m.group(2);
			if (name == null) {
				name = m.group(1);
			}
			Object value = pa.getPropertyValue(name);
			m.appendReplacement(sb, String.valueOf(value));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public int compareTo(HandlerUrl o) {
		if (this.numberOfPlaceholders < o.numberOfPlaceholders) {
			return 1;
		}
		if (this.numberOfPlaceholders == o.numberOfPlaceholders) {
			return 0;
		}
		return -1;
	}
	
	@Override
	public String toString() {
		return String.format("%s[placeholders=%s, path=%s]",
				getClass().getName(), numberOfPlaceholders, path);
	}

}