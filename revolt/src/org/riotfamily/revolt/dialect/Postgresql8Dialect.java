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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   flx
 * 
 * ***** END LICENSE BLOCK ***** */
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
