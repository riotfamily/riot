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
package org.riotfamily.revolt.dialect;

import org.riotfamily.revolt.Script;
import org.riotfamily.revolt.definition.Column;
import org.riotfamily.revolt.definition.Index;
import org.riotfamily.revolt.support.TypeMap;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class PostgresqlDialect extends Sql92Dialect {

	protected void registerTypes() {
		registerType(TypeMap.BIT, "BOOLEAN");
		registerType(TypeMap.TINYINT, "SMALLINT");
		registerType(TypeMap.SMALLINT, "SMALLINT");
		registerType(TypeMap.INTEGER, "INTEGER");
		registerType(TypeMap.BIGINT, "BIGINT");
		registerType(TypeMap.FLOAT, "DOUBLE PRECISION");
		registerType(TypeMap.REAL, "REAL");
		registerType(TypeMap.DOUBLE, "DOUBLE PRECISION");
		registerType(TypeMap.NUMERIC, "NUMERIC");
		registerType(TypeMap.DECIMAL, "NUMERIC");
		registerType(TypeMap.CHAR, "CHAR", true);
		registerType(TypeMap.VARCHAR, "VARCHAR", true);
		registerType(TypeMap.LONGVARCHAR, "TEXT");
		registerType(TypeMap.DATE, "DATE");
		registerType(TypeMap.TIME, "TIME");
		registerType(TypeMap.TIMESTAMP, "TIMESTAMP");
		registerType(TypeMap.BINARY, "BYTEA", true);
		registerType(TypeMap.VARBINARY, "BYTEA", true);
		registerType(TypeMap.LONGVARBINARY, "BYTEA", true);
		registerType(TypeMap.BLOB, "BYTEA");
		registerType(TypeMap.CLOB, "TEXT");
	}

	public boolean supports(String databaseProductName, 
			int majorVersion, int minorVersion) {

		return "PostgreSQL".equals(databaseProductName) && majorVersion < 8;
	}
		
	public Script renameTable(String name, String renameTo) {
		return alterTable(name).append("RENAME TO").append(quote(renameTo));
	}

	public Script createIndex(String table, Index index) {
		Script sql = new Script("CREATE INDEX").append(index.getName())
				.append("ON").append(quote(table));

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
				sql.append("SET DEFAULT").append(convertQuotes(column.getDefaultValue()));
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
		throw new OperationNotSupportedException("The column type can't be" +
				" changed in PostgreSQL < 8.0");
	}

	protected void addAlterColumn(Script sql, String table, Column column) {
		sql.append("ALTER TABLE").append(quote(table))
				.append("ALTER COLUMN").append(quote(column));
	}

	public Script renameColumn(String table, String name, String renameTo) {
		return alterTable(table).append("RENAME COLUMN").append(quote(name))
				.append("TO").append(quote(renameTo));
	}
	
	public Script createAutoIncrementSequence(String name) {
		return new Script("CREATE SEQUENCE").append(name);
	}

}
