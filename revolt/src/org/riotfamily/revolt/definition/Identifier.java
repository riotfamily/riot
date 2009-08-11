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
package org.riotfamily.revolt.definition;

import org.springframework.util.StringUtils;


/**
 * Abstract base for all classes that represent a named component within 
 * a database definition, like tables, columns and constraints.
 * 
 * @see DefinitionUtils
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class Identifier {

	public static final String QUOTED_DELIMITER = "`";

	private String name;
	
	private boolean quoted;
	
	public Identifier() {
	}

	public Identifier(String name) {
		setName(name);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		if (name != null && name.startsWith(QUOTED_DELIMITER) 
				&& name.endsWith(QUOTED_DELIMITER)) {
			
			quoted = true;
			name = StringUtils.delete(name, QUOTED_DELIMITER);
		}
		this.name = name;
	}

	public boolean isQuoted() {
		return this.quoted;
	}

	public void setQuoted(boolean quoted) {
		this.quoted = quoted;
	}
	
	public int hashCode() {
		return getName() == null ? 0 : getName().toUpperCase().hashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (name == null) {
			return false;
		}
		if (!(obj instanceof Identifier)) {
			return false;
		}
		Identifier other = (Identifier) obj;
		return name.equals(other.name) || (quoted == other.quoted 
				&& name.equalsIgnoreCase(other.name));  
	}

	public String toString() {
		return name;
	}
	
}
