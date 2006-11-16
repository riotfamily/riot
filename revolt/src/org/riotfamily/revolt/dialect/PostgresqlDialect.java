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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.revolt.Script;
import org.riotfamily.revolt.definition.Column;
import org.riotfamily.revolt.definition.Index;
import org.riotfamily.revolt.support.TypeMap;

/**
 * @author Felix Gnass <fgnass@neteye.de>
 * 
 */
public class PostgresqlDialect extends Sql92Dialect {

	private static Log log = LogFactory.getLog(PostgresqlDialect.class);
	
	protected void registerTypes() {
		registerType(TypeMap.BIT, "BOOLEAN");
		registerType(TypeMap.BOOLEAN, "BOOLEAN");
		registerType(TypeMap.TINYINT, "SMALLINT");
		registerType(TypeMap.SMALLINT, "SMALLINT");
		registerType(TypeMap.INTEGER, "INTEGER");
		registerType(TypeMap.BIGINT, "BIGINT");
		registerType(TypeMap.FLOAT, "DOUBLE PRECISION");
		registerType(TypeMap.REAL, "REAL");
		registerType(TypeMap.DOUBLE, "DOUBLE PRECISION");
		registerType(TypeMap.NUMERIC, "NUMERIC");
		registerType(TypeMap.DECIMAL, "NUMERIC");
		registerType(TypeMap.CHAR, "CHAR");
		registerType(TypeMap.VARCHAR, "VARCHAR");
		registerType(TypeMap.LONGVARCHAR, "TEXT");
		registerType(TypeMap.DATE, "DATE");
		registerType(TypeMap.TIME, "TIME");
		registerType(TypeMap.TIMESTAMP, "TIMESTAMP");
		registerType(TypeMap.BINARY, "BYTEA");
		registerType(TypeMap.VARBINARY, "BYTEA");
		registerType(TypeMap.LONGVARBINARY, "BYTEA");
		registerType(TypeMap.BLOB, "BYTEA");
		registerType(TypeMap.CLOB, "TEXT");
	}

	public boolean supports(String databaseProductName, 
			int majorVersion, int minorVersion) {

		log.info(databaseProductName + " " + majorVersion + "." + minorVersion);
		return "PostgreSQL".equals(databaseProductName);
	}
		
	public Script renameTable(String name, String renameTo) {

		return alterTable(name).append("RENAME TO").append(getTableName(renameTo));
	}

	public Script createIndex(String table, Index index) {
		// REVISIT Index names are global. Should we add the table name as
		// prefix?
		Script sql = new Script("CREATE INDEX").append(index.getName()).append("ON").append(getTableName(table));

		addColumnNames(sql, index.getColumns());
		return sql;

	}

	public Script dropIndex(String table, String name) {
		return new Script("DROP INDEX").append(name);
	}

	public Script modifyColumn(String table, Column column) {
		Script sql = new Script();
		if (column.isDefaultValueSet()) {
			addAlterColumn(sql, table, column);
			if (column.getDefaultValue() != null) {
				sql.append("SET DEFAULT").append(getValue(column.getDefaultValue()));
			}
			else {
				sql.append("DROP DEFAULT");
			}
			sql.newStatement();
		}
		if (column.isNotNullSet()) {
			addAlterColumn(sql, table, column);
			sql.append(column.isNotNull() ? "SET" : "DROP").append("NOT NULL");

			sql.newStatement();
		}
		if (column.getType() != null) {
			sql.append(modifyColumnType(table, column));
		}
		return sql;
	}

	protected Script modifyColumnType(String table, Column column) {
		throw new OperationNotSupportedException();
	}

	protected void addAlterColumn(Script sql, String table, Column column) {
		sql.append("ALTER TABLE").append(getTableName(table)).append("ALTER COLUMN ").append(
				getColumnName(column.getName()));
	}

	public Script renameColumn(String table, String name, String renameTo) {

		return alterTable(table).append("RENAME COLUMN").append(getColumnName(name)).append("TO").append(
				getColumnName(renameTo));
	}

}
