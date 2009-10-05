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

	private static HashMap<String, Integer> jdbcTypes = new HashMap<String, Integer>();

	static {
		jdbcTypes.put(BIT, Types.BIT);
		jdbcTypes.put(TINYINT, Types.TINYINT);
		jdbcTypes.put(SMALLINT, Types.SMALLINT);
		jdbcTypes.put(INTEGER, Types.INTEGER);
		jdbcTypes.put(BIGINT, Types.BIGINT);
		jdbcTypes.put(FLOAT, Types.FLOAT);
		jdbcTypes.put(REAL, Types.REAL);
		jdbcTypes.put(DOUBLE, Types.DOUBLE);
		jdbcTypes.put(NUMERIC, Types.NUMERIC);
		jdbcTypes.put(DECIMAL, Types.DECIMAL);
		jdbcTypes.put(CHAR, Types.CHAR);
		jdbcTypes.put(VARCHAR, Types.VARCHAR);
		jdbcTypes.put(LONGVARCHAR, Types.LONGVARCHAR);
		jdbcTypes.put(DATE, Types.DATE);
		jdbcTypes.put(TIME, Types.TIME);
		jdbcTypes.put(TIMESTAMP, Types.TIMESTAMP);
		jdbcTypes.put(BINARY, Types.BINARY);
		jdbcTypes.put(VARBINARY, Types.VARBINARY);
		jdbcTypes.put(LONGVARBINARY, Types.LONGVARBINARY);
		jdbcTypes.put(BLOB, Types.BLOB);
		jdbcTypes.put(CLOB, Types.CLOB);
	}

	public static int getJdbcType(String name) {
		Integer i = (Integer) jdbcTypes.get(name.toUpperCase());
		if (i != null) {
			return i.intValue();
		}
		return Types.NULL;
	}
	
	public static boolean isComplete(Map<String, ?> map) {
		return map.keySet().containsAll(jdbcTypes.keySet());
	}
}
