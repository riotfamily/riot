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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.riotfamily.revolt.definition.Identifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Utility class that retieves database meta data via JDBC.
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class DatabaseUtils {

	/**
	 * Returns the JDBC-URL for the given DataSource.
	 */
	public static String getUrl(DataSource dataSource) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		return (String) template.execute(new ConnectionCallback() {
			public Object doInConnection(Connection connection)
					throws SQLException, DataAccessException {

				DatabaseMetaData metaData = connection.getMetaData();
				return metaData.getURL();
			}
		});
	}

	/**
	 * Returns whether the DataSource contains any tables.
	 */
	public static boolean anyTablesExist(JdbcOperations operations) {
		Boolean result = (Boolean) operations.execute(new ConnectionCallback() {
			public Object doInConnection(Connection connection)
					throws SQLException, DataAccessException {

				DatabaseMetaData metaData = connection.getMetaData();
				ResultSet rs = metaData.getTables(null, null, "_", null);
				return Boolean.valueOf(rs.next());
			}
		});
		return result.booleanValue();
	}

	/**
	 * Returns whether the specified table exists.
	 */
	public static boolean tableExists(JdbcOperations operations,
			final Identifier table) {

		Boolean result = (Boolean) operations.execute(new ConnectionCallback() {
			public Object doInConnection(Connection connection)
					throws SQLException, DataAccessException {

				DatabaseMetaData metaData = connection.getMetaData();
				String pattern = getSearchPattern(metaData, table);
				ResultSet rs = metaData.getTables(null, null, pattern, null);
				return Boolean.valueOf(rs.next());
			}
		});
		return result.booleanValue();
	}

	private static String getSearchPattern(DatabaseMetaData metaData,
			Identifier identifier) throws SQLException {

		String escape = metaData.getSearchStringEscape();
		String pattern = identifier.getName().replaceAll("_", escape + "_");

		if (identifier.isQuoted()) {
			if (metaData.storesUpperCaseQuotedIdentifiers()) {
				pattern = pattern.toUpperCase();
			}
			else if (metaData.storesLowerCaseQuotedIdentifiers()) {
				pattern = pattern.toLowerCase();
			}
		}
		else {
			if (metaData.storesUpperCaseIdentifiers()) {
				pattern = pattern.toUpperCase();
			}
			else if (metaData.storesLowerCaseIdentifiers()) {
				pattern = pattern.toLowerCase();
			}
		}
		return pattern;
	}

}
