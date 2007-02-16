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
package org.riotfamily.revolt.support;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class TypeMap {

	public final static String BIT = "BIT";

	public final static String TINYINT = "TINYINT";

	public final static String SMALLINT = "SMALLINT";

	public final static String INTEGER = "INTEGER";

	public final static String BIGINT = "BIGINT";

	public final static String FLOAT = "FLOAT";

	public final static String REAL = "REAL";

	public final static String DOUBLE = "DOUBLE";

	public final static String NUMERIC = "NUMERIC";

	public final static String DECIMAL = "DECIMAL";

	public final static String CHAR = "CHAR";

	public final static String VARCHAR = "VARCHAR";

	public final static String LONGVARCHAR = "LONGVARCHAR";

	public final static String DATE = "DATE";

	public final static String TIME = "TIME";

	public final static String TIMESTAMP = "TIMESTAMP";

	public final static String BINARY = "BINARY";

	public final static String VARBINARY = "VARBINARY";

	public final static String LONGVARBINARY = "LONGVARBINARY";

	public final static String BLOB = "BLOB";

	public final static String CLOB = "CLOB";

	private static HashMap jdbcTypes = new HashMap();

	static {
		jdbcTypes.put(BIT, new Integer(Types.BIT));
		jdbcTypes.put(TINYINT, new Integer(Types.TINYINT));
		jdbcTypes.put(SMALLINT, new Integer(Types.SMALLINT));
		jdbcTypes.put(INTEGER, new Integer(Types.INTEGER));
		jdbcTypes.put(BIGINT, new Integer(Types.BIGINT));
		jdbcTypes.put(FLOAT, new Integer(Types.FLOAT));
		jdbcTypes.put(REAL, new Integer(Types.REAL));
		jdbcTypes.put(DOUBLE, new Integer(Types.DOUBLE));
		jdbcTypes.put(NUMERIC, new Integer(Types.NUMERIC));
		jdbcTypes.put(DECIMAL, new Integer(Types.DECIMAL));
		jdbcTypes.put(CHAR, new Integer(Types.CHAR));
		jdbcTypes.put(VARCHAR, new Integer(Types.VARCHAR));
		jdbcTypes.put(LONGVARCHAR, new Integer(Types.LONGVARCHAR));
		jdbcTypes.put(DATE, new Integer(Types.DATE));
		jdbcTypes.put(TIME, new Integer(Types.TIME));
		jdbcTypes.put(TIMESTAMP, new Integer(Types.TIMESTAMP));
		jdbcTypes.put(BINARY, new Integer(Types.BINARY));
		jdbcTypes.put(VARBINARY, new Integer(Types.VARBINARY));
		jdbcTypes.put(LONGVARBINARY, new Integer(Types.LONGVARBINARY));
		jdbcTypes.put(BLOB, new Integer(Types.BLOB));
		jdbcTypes.put(CLOB, new Integer(Types.CLOB));
	}

	public static int getJdbcType(String name) {
		Integer i = (Integer) jdbcTypes.get(name.toUpperCase());
		if (i != null) {
			return i.intValue();
		}
		return Types.NULL;
	}
	
	public static boolean isComplete(Map map) {
		return map.keySet().containsAll(jdbcTypes.keySet());
	}
}
