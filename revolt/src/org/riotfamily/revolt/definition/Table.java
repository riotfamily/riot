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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.revolt.definition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class Table extends Identifier {

	private List columns = new ArrayList();

	private List primaryKeys = new ArrayList();
	
	private List foreignKeys = new ArrayList();
	
	private List uniqueConstraints = new ArrayList();
	
	private List indices = new ArrayList();

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
		this.columns = new ArrayList(columns.size());
		primaryKeys.clear();
		if (columns != null) {
			Iterator it = columns.iterator();
			while (it.hasNext()) {
				Column column = (Column) it.next();
				Column copy = column.copy();
				this.columns.add(copy);
				if (column.isPrimaryKey()) {
					primaryKeys.add(copy);
				}
			}
		}
	}

	public void addColumn(Column column) {
		columns.remove(column);
		primaryKeys.remove(column);
		Column copy = column.copy();
		columns.add(copy);
		if (column.isPrimaryKey()) {
			primaryKeys.add(copy);
		}
	}

	public void removeColumn(String name) {
		columns.remove(new Identifier(name));
	}
	
	public Column getColumn(String name) {
		return (Column) columns.get(columns.indexOf(new Identifier(name)));
	}
	
	public List getPrimaryKeys() {
		return this.primaryKeys;
	}
	
	public void addIndex(Index index) {
		indices.remove(index);
		indices.add(index);
	}
	
	public void removeIndex(String name) {
		indices.remove(new Identifier(name));
	}
	
	public void addForeignKey(ForeignKey fk) {
		foreignKeys.remove(fk);
		foreignKeys.add(fk);
	}
	
	public void removeForeignKey(String name) {
		foreignKeys.remove(new Identifier(name));
	}
	
	public void addUniqueConstraint(UniqueConstraint uc) {
		uniqueConstraints.remove(uc);
		uniqueConstraints.add(uc);
	}
	
	public void removeUniqueConstraint(String uc) {
		uniqueConstraints.remove(new Identifier(uc));
	}
	
}
