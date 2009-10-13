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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.dialect.HsqlDialect;
import org.riotfamily.revolt.dialect.MySqlDialect;
import org.riotfamily.revolt.dialect.Postgresql8Dialect;
import org.riotfamily.revolt.dialect.PostgresqlDialect;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Picks a suitable dialect form a list of implementations based on the 
 * product name and version returned by the JDBC driver.
 *   
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class DialectResolver {

	private List<Dialect> dialects;

	public DialectResolver() {
		dialects = new ArrayList<Dialect>();
		dialects.add(new PostgresqlDialect());
		dialects.add(new Postgresql8Dialect());
		dialects.add(new MySqlDialect());
		dialects.add(new HsqlDialect());
	}

	public Dialect getDialect(DataSource dataSource) 
			throws DatabaseNotSupportedException {
		
		JdbcTemplate template = new JdbcTemplate(dataSource);
		return (Dialect) template.execute(new ConnectionCallback() {
			public Object doInConnection(Connection connection) 
					throws SQLException, DataAccessException {
				
				DatabaseMetaData metaData = connection.getMetaData();
				String productName = metaData.getDatabaseProductName();
				int major = metaData.getDatabaseMajorVersion();
				int minor = metaData.getDatabaseMinorVersion();
				return getDialect(productName, major, minor);
			}
		});
	}

	protected Dialect getDialect(String productName, int major, int minor) {
		for (Dialect dialect : dialects) {
			if (dialect.supports(productName, major, minor)) {
				return dialect;
			}
		}
		throw new DatabaseNotSupportedException(productName, major, minor);
	}

}
