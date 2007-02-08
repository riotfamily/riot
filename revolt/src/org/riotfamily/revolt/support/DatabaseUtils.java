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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.sql.DataSource;

import org.riotfamily.revolt.DatabaseOutOfSyncException;
import org.riotfamily.revolt.definition.Column;
import org.riotfamily.revolt.definition.Database;
import org.riotfamily.revolt.definition.Identifier;
import org.riotfamily.revolt.definition.Table;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

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
	public static boolean anyTablesExist(DataSource dataSource) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		Boolean result = (Boolean) template.execute(new ConnectionCallback() {
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
	public static boolean tableExists(DataSource dataSource, 
			final Identifier table) {
		
		JdbcTemplate template = new JdbcTemplate(dataSource);
		Boolean result = (Boolean) template.execute(new ConnectionCallback() {
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
	
	/**
	 * Checks whether the database schema matches the given model.
	 */
	public static void validate(DataSource dataSource, final Database model) 
			throws DatabaseOutOfSyncException {
		
		JdbcTemplate template = new JdbcTemplate(dataSource);
		template.execute(new ConnectionCallback() {
			public Object doInConnection(Connection connection) 
					throws SQLException, DataAccessException {
				
				DatabaseMetaData metaData = connection.getMetaData();
				validate(metaData, model);
				return null;
			}
		});
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

	private static void validate(DatabaseMetaData metaData, Database model) 
			throws SQLException, DatabaseOutOfSyncException {
		
		Iterator it = model.getTables().iterator();
		while (it.hasNext()) {
			Table table = (Table) it.next();
			String pattern = getSearchPattern(metaData, table);
			ResultSet rs = metaData.getColumns(null, null, pattern, "%");
			ArrayList columns = new ArrayList();
			while (rs.next()) {
				columns.add(new Column(rs.getString("COLUMN_NAME")));
			}
			if (columns.isEmpty()) {
				throw new DatabaseOutOfSyncException("Table " 
						+ table.getName() + " does not exist");
			}
			if (!columns.containsAll(table.getColumns())) {
				ArrayList missing = new ArrayList(table.getColumns());
				missing.removeAll(columns);
				throw new DatabaseOutOfSyncException("Table "+ table.getName() 
						+ " does not have the following column(s): " 
						+ StringUtils.collectionToCommaDelimitedString(missing));
			}
		}
	}
}
