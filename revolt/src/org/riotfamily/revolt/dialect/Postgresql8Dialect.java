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

/**
 * Dialect for PostgreSQL &gt;= 8.0 that supports changing the type of a column.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class Postgresql8Dialect extends PostgresqlDialect {

	public boolean supports(String databaseProductName, 
			int majorVersion, int minorVersion) {

		return "PostgreSQL".equals(databaseProductName) && majorVersion >= 8;
	}
	
	protected Script modifyColumnType(String table, Column column) {
		Script sql = new Script();
		addAlterColumn(sql, table, column);
		sql.append("TYPE").append(getColumnType(column));
		return sql;
	}
}
