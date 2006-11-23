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
package org.riotfamily.revolt.support;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.dialect.HsqlDialect;
import org.riotfamily.revolt.dialect.MySqlDialect;
import org.riotfamily.revolt.dialect.PostgresqlDialect;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Picks a suitable dialect form a list of implementations based on the 
 * product name and version returned by the JDBC driver.
 *   
 * @author Felix Gnass <fgnass@neteye.de>
 */
public class DialectResolver {

	private List dialects;

	public DialectResolver() {
		dialects = new ArrayList();
		dialects.add(new PostgresqlDialect());
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
		Iterator it = dialects.iterator();
		while (it.hasNext()) {
			Dialect dialect = (Dialect) it.next();
			if (dialect.supports(productName, major, minor)) {
				return dialect;
			}
		}
		throw new DatabaseNotSupportedException(productName, major, minor);
	}

}
