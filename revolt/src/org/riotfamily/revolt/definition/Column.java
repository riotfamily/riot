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

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class Column extends Identifier implements Cloneable {

	private String type;

	private int length;

	private boolean lengthSet;

	private String defaultValue;
	
	private boolean defaultValueSet;

	private boolean notNull;
	
	private boolean notNullSet;

	private boolean primaryKey;
	
	private boolean primaryKeySet;

	private boolean autoIncrement;
	
	private boolean autoIncrementSet;

	public Column() {
	}
	
	public Column(String name) {
		super(name);
	}

	public Column(String name, String type) {
		super(name);
		this.type = type;
	}

	public Column(String name, String type, int length) {
		this(name, type);
		setLength(length);
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
		autoIncrementSet = true;
	}
	
	public boolean isAutoIncrementSet() {
		return this.autoIncrementSet;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		defaultValueSet = true;
	}

	public boolean isDefaultValueSet() {
		return this.defaultValueSet;
	}

	public int getLength() {
		return this.length;
	}

	public void setLength(int length) {
		this.length = length;
		lengthSet = true;
	}

	public boolean isLengthSet() {
		return this.lengthSet;
	}

	public boolean isNotNullSet() {
		return notNullSet;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
		notNullSet = true;
	}

	public boolean isPrimaryKeySet() {
		return primaryKeySet;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
		primaryKeySet = true;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public Column copy() {
		try {
			return (Column) clone();
		} 
		catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void merge(Column column) {
		if (column.isDefaultValueSet()) {
			setDefaultValue(column.getDefaultValue());
		}
		if (column.isNotNullSet()) {
			setNotNull(column.isNotNull());
		}
		if (column.getType() != null) {
			setType(column.getType());
		}
		if (column.isLengthSet()) {
			setLength(column.getLength());
		}
		if (column.isAutoIncrementSet()) {
			setAutoIncrement(column.isAutoIncrement());
		}
	}	
}
