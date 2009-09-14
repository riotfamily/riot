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
package org.riotfamily.core.dao;

import java.io.Serializable;

import org.springframework.beans.support.SortDefinition;

public class Order implements Serializable, SortDefinition {

	String property;

	boolean ascending;
	
	boolean caseSensitive;

	
	public Order(String property, boolean ascending, boolean caseSensitive) {
		this.property = property;
		this.ascending = ascending;
		this.caseSensitive = caseSensitive;
	}

	public String getProperty() {
		return property;
	}

	public boolean isAscending() {
		return ascending;
	}	
	
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public boolean isIgnoreCase() {
		return !caseSensitive;
	}
	
	public boolean isProperty(String property) {
		return this.property.equals(property);
	}
	
	public void toggleDirection() {
		ascending = !ascending;
	}
	
}