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
public class MySqlDialect extends Sql92Dialect {

	protected void registerTypes() {
		registerType(TypeMap.BIT, "TINYINT(1)");
		registerType(TypeMap.TINYINT, "SMALLINT");
		registerType(TypeMap.SMALLINT, "SMALLINT");
		registerType(TypeMap.INTEGER, "INTEGER");
		registerType(TypeMap.BIGINT, "BIGINT");
		registerType(TypeMap.FLOAT, "FLOAT");
		registerType(TypeMap.REAL, "REAL");
		registerType(TypeMap.DOUBLE, "DOUBLE");
		registerType(TypeMap.NUMERIC, "NUMERIC");
		registerType(TypeMap.DECIMAL, "DECIMAL");
		registerType(TypeMap.CHAR, "CHAR", true);
		registerType(TypeMap.VARCHAR, "VARCHAR", true);
		registerType(TypeMap.LONGVARCHAR, "TEXT");
		registerType(TypeMap.DATE, "DATE");
		registerType(TypeMap.TIME, "TIME");
		registerType(TypeMap.TIMESTAMP, "TIMESTAMP");
		registerType(TypeMap.BINARY, "BINARY", true);
		registerType(TypeMap.VARBINARY, "VARBINARY", true);
		registerType(TypeMap.LONGVARBINARY, "VARBINARY");
		registerType(TypeMap.BLOB, "BLOB");
		registerType(TypeMap.CLOB, "TEXT");
	}

	public boolean supports(String databaseProductName, 
			int majorVersion, int minorVersion) {

		return "MySQL".equals(databaseProductName);
	}
	
	protected void addColumnDefinition(Script sql, Column column) {
		super.addColumnDefinition(sql, column);
		if (column.isAutoIncrement()) {
			sql.append("AUTO_INCREMENT");
		}
	}
	
	public Script createAutoIncrementSequence(String name) {
		return null;
	}
	
	public Script renameTable(String name, String renameTo) {
		return alterTable(name).append("RENAME TO").append(quote(renameTo));
	}

	public Script renameColumn(String table, String name, String renameTo) {
		return alterTable(table).append("CHANGE COLUMN").append(name)
				.append(renameTo);
	}

	public Script modifyColumn(String table, Column column) {
		Script sql = alterTable(table).append("CHANGE COLUMN");
		addColumnDefinition(sql, column);
		return sql;
	}

	public Script createIndex(String table, Index index) {
		Script sql = alterTable(table).append("ADD INDEX").append(index.getName());
		addColumnNames(sql, index.getColumns());
		return sql;
	}

	public Script dropIndex(String table, String name) {
		return alterTable(table).append("DROP INDEX").append(name);
	}
	
	@Override
	public Script dropForeignKey(String table, String name) {
		return alterTable(table).append("DROP FOREIGN KEY").append(name);
	}
	
	protected String convertBackticksToIdentifierDelimiter(String s) {
		return s;
	}
}
