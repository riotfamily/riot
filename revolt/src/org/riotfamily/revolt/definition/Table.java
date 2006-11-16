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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.revolt.definition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Felix Gnass <fgnass@neteye.de>
 * 
 */
public class Table extends AbstractDataDefinition {

	private List columns;

	private List primaryKeys;
	
	private List foreignKeys;
	
	private List uniqueConstraints;
	
	private List indices;

	public Table() {
	}

	public Table(String name) {
		super(name);
	}
	
	public Table(String name, List columns) {
		super(name);
		setColumns(columns);
	}

	public List getColumns() {
		return this.columns;
	}

	public void setColumns(List columns) {
		this.columns = columns;
		primaryKeys = null;
		if (columns != null) {
			Iterator it = columns.iterator();
			while (it.hasNext()) {
				Column column = (Column) it.next();
				if (column.isPrimaryKey()) {
					if (primaryKeys == null) {
						primaryKeys = new ArrayList();
					}
					primaryKeys.add(column.getName());
				}
			}
		}
	}

	public void addColumn(Column column) {
		columns = DefinitionUtils.addDefinition(columns, column);
		if (column.isPrimaryKey()) {
			if (primaryKeys == null) {
				primaryKeys = new ArrayList();
			}
			primaryKeys.add(column.getName());
		}
	}

	public void removeColumn(String name) {
		DefinitionUtils.removeDefinition(columns, name);
	}
	
	public Column getColumn(String name) {
		return (Column) DefinitionUtils.findDefinition(columns, name);
	}
	
	public List getPrimaryKeys() {
		return this.primaryKeys;
	}
	
	public void addIndex(Index index) {
		indices = DefinitionUtils.addDefinition(indices, index);
	}
	
	public void removeIndex(String index) {
		DefinitionUtils.removeDefinition(indices, index);
	}
	
	public void addForeignKey(ForeignKey fk) {
		foreignKeys = DefinitionUtils.addDefinition(foreignKeys, fk);
	}
	
	public void removeForeignKey(String fk) {
		DefinitionUtils.removeDefinition(foreignKeys, fk);
	}
	
	public void addUniqueConstraint(UniqueConstraint uc) {
		uniqueConstraints = DefinitionUtils.addDefinition(uniqueConstraints, uc);
	}
	
	public void removeUniqueConstraint(String uc) {
		DefinitionUtils.removeDefinition(uniqueConstraints, uc);
	}

}
