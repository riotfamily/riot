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
package org.riotfamily.revolt.dialect;

import java.util.Iterator;
import java.util.List;

import org.riotfamily.revolt.Script;
import org.riotfamily.revolt.definition.Column;
import org.riotfamily.revolt.definition.ForeignKey;
import org.riotfamily.revolt.definition.Table;
import org.riotfamily.revolt.definition.UniqueConstraint;

/**
 * @author Felix Gnass <fgnass@neteye.de>
 * 
 */
public abstract class Sql92Dialect extends AbstractDialect {

	public Sql92Dialect() {
	}

	public Script createTable(Table table) {

		Script sql = new Script("CREATE TABLE").append(getTableName(table.getName())).append("(");

		Iterator it = table.getColumns().iterator();
		while (it.hasNext()) {
			Column column = (Column) it.next();
			addColumnDefinition(sql, column);
			if (it.hasNext()) {
				sql.append(",");
			}
		}
		if (table.getPrimaryKeys() != null) {
			sql.append(", PRIMARY KEY");
			addColumnNames(sql, table.getPrimaryKeys());
		}

		sql.append(")");

		return sql;
	}

	/*
	 * public Script renameTable(String name, String renameTo) {
	 * 
	 * throw new OperationNotSupportedException(); }
	 */

	public Script dropTable(String name) {

		return new Script("DROP TABLE").append(getTableName(name));
	}

	public Script addColumn(String table, Column column) {

		Script sql = alterTable(table).append("ADD COLUMN");
		addColumnDefinition(sql, column);
		return sql;
	}

	/*
	 * public Script renameColumn(String table, String name, String renameTo) {
	 * 
	 * throw new OperationNotSupportedException(); }
	 */

	/*
	 * public Script modifyColumn(String table, Column column) {
	 * 
	 * throw new OperationNotSupportedException(); }
	 */

	public Script dropColumn(String table, String name) {

		return alterTable(table).append("DROP COLUMN").append(getColumnName(name));
	}

	/*
	 * public Script addIndex(String table, Index index) {
	 * 
	 * throw new OperationNotSupportedException(); }
	 */

	/*
	 * public Script dropIndex(String table, String name) {
	 * 
	 * throw new OperationNotSupportedException(); }
	 */

	public Script addUniqueConstraint(String table, UniqueConstraint constraint) {

		Script sql = alterTable(table).append("ADD CONSTRAINT").append(constraint.getName()).append("UNIQUE");

		addColumnNames(sql, constraint.getColumns());
		return sql;
	}

	public Script dropUniqueConstraint(String table, String name) {

		return dropConstraint(table, name);
	}

	public Script addForeignKey(String table, ForeignKey fk) {

		Script sql = alterTable(table).append("ADD CONSTRAINT").append(fk.getName()).append("FOREIGN KEY");

		addColumnNames(sql, fk.getLocalColumns());
		sql.append("REFERENCES").append(fk.getForeignTable());

		addColumnNames(sql, fk.getForeignColumns());

		// TODO ON DELETE / ON UPDATE

		return sql;
	}

	public Script dropForeignKey(String table, String name) {

		return dropConstraint(table, name);
	}

	protected Script alterTable(String name) {
		return new Script("ALTER TABLE").append(getTableName(name));
	}

	protected Script dropConstraint(String table, String name) {
		return alterTable(table).append("DROP CONSTRAINT").append(name);
	}

	protected void addColumnDefinition(Script sql, Column column) {

		sql.append(getColumnName(column.getName())).append(getColumnType(column));

		if (column.isDefaultValueSet()) {
			sql.append("DEFAULT").append(getValue(column.getDefaultValue()));
		}
		if (column.isNotNullSet()) {
			if (column.isNotNull()) {
				sql.append("NOT");
			}
			sql.append("NULL");
		}
	}

	protected void addColumnNames(Script sql, List columns) {
		sql.append("(");
		Iterator it = columns.iterator();
		while (it.hasNext()) {
			sql.append(getColumnName((String) it.next()));
			if (it.hasNext()) {
				sql.append(",");
			}
		}
		sql.append(")");
	}

	protected String getTableName(String name) {
		return name; //TODO Support schemas and delimited identifiers
	}

	protected String getColumnName(String name) {
		return name; //TODO Support delimited identifiers
	}

	protected String getValue(String value) {
		return value; //TODO Quote value
	}

}
