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
package org.riotfamily.core.screen.list;

import org.riotfamily.common.ui.ObjectRenderer;

/**
 *
 */
public class ColumnConfig {
	
	private String property;

	private int lookupLevel;

	private boolean sortable = true;

	private boolean ascending = true;
	
	private boolean caseSensitive = true;

	private ObjectRenderer renderer;

	private String cssClass;

	public ColumnConfig() {
	}
	
	public ColumnConfig(ObjectRenderer renderer) {
		this.renderer = renderer;
	}

	public int getLookupLevel() {
		return lookupLevel;
	}

	/**
	 * Sets which level of a nested property should be used to construct the
	 * message-key for the column heading.
	 * <p> 
	 * Because this setting is quite difficult to explain, here are two 
	 * examples that illustrate the results of the different settings.
	 * Let's imagine we have a list persons. Each <code>Person</code> entity has
	 * an <code>address</code> property which in turn has a property called 
	 * <code>firstName</code>. We now want to display the person's firstName 
	 * in a column:
	 * </p>
	 * <pre>&lt;column property="address.firstName" /&gt;</pre>
	 * The default message-key (level 0) will be <b>org.example.Person.address</b>
	 * which might not be desirable in this special case, as the column would
	 * be labeled with <em>Address</em> instead of <em>First Name</em>.
	 * <p>
	 * If we set the level to 1, the complete property path would be appended
	 * to the message-key, resulting in <b>org.example.Person.address.firstName</b>.
	 * </p>
	 * <p>
	 * If the Address class is used in several places it would be even nicer
	 * to have <b>org.example.Address.firstName</b> as message-key. This can be 
	 * achieved by setting the lookup-level to 2.
	 * </p>
	 * <dl>
	 * <dt>level 0:</dt><dd>org.example.Person.address</dd>
	 * <dt>level 1:</dt><dd>org.example.Person.address.firstName</dd>
	 * <dt>level 2:</td><dd>org.example.Address.firstName</dd>
	 * </dl>
	 * Let's see what the results would look like if we had a property with an
	 * even greater nesting level, say we have a list of cars and want to 
	 * display the owner's first-name:
	 * <pre>&lt;column property="owner.address.firstName" /&gt;</pre>
	 * <dl>
	 * <dt>level 0:</dt><dd>org.example.Car.owner</dd>
	 * <dt>level 1:</dt><dd>org.example.Car.owner.address.firstName</dd>
	 * <dt>level 2:</dt><dd>org.example.Person.address.firstName</dd>
	 * <dt>level 3:</dt><dd>org.example.Address.firstName</dd>
	 * </dl>
	 */
	public void setLookupLevel(int lookupLevel) {
		this.lookupLevel = lookupLevel;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public boolean isSortable() {
		return sortable && property != null;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}	

	public boolean isCaseSensitive() {		
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {		
		this.caseSensitive = caseSensitive;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public ObjectRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(ObjectRenderer renderer) {
		this.renderer = renderer;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}
}
