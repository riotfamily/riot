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

import org.riotfamily.common.ui.Renderer;


/**
 *
 */
public class ColumnConfig {
	
	private String property;

	private boolean sortable = true;

	private boolean ascending = true;
	
	private boolean caseSensitive = true;

	private Renderer renderer;

	private String cssClass;

	public ColumnConfig() {
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
	
	public void setDefaultDirection(String dir) {
		ascending = !"desc".equalsIgnoreCase(dir);
	}

	public boolean isAscending() {
		return ascending;
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
	
	public Renderer getRenderer() {
		return renderer;
	}

	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}
}
