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

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class UniqueConstraint extends Identifier {

	private Collection<Identifier> columns;

	public UniqueConstraint() {
	}

	public UniqueConstraint(String name) {
		super(name);
	}
	
	public UniqueConstraint(String name, String[] columnNames) {
		super(name);
		setColumnNames(columnNames);
	}

	public Collection<Identifier> getColumns() {
		return this.columns;
	}

	public void setColumnNames(String[] names) {
		columns = new ArrayList<Identifier>();
		for (int i = 0; i < names.length; i++) {
			columns.add(new Identifier(names[i]));
		}
	}

}
