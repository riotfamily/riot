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
