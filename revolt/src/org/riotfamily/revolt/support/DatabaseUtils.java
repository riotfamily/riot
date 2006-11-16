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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.revolt.definition.Column;
import org.riotfamily.revolt.definition.Database;
import org.riotfamily.revolt.definition.Table;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Felix Gnass <fgnass@neteye.de>
 *
 */
public class DatabaseUtils {

	private static Log log = LogFactory.getLog(DatabaseUtils.class);
	
	public static boolean databaseMatchesModel(
			DataSource dataSource, final Database model) {
		
		JdbcTemplate template = new JdbcTemplate(dataSource);
		Boolean result = (Boolean) template.execute(new ConnectionCallback() {
			public Object doInConnection(Connection connection) 
					throws SQLException, DataAccessException {
				
				DatabaseMetaData metaData = connection.getMetaData();
				return Boolean.valueOf(metaDataMatchesModel(metaData, model));
			}
		});
		return result.booleanValue();
	}
	
	private static boolean metaDataMatchesModel(
			DatabaseMetaData metaData, Database model) 
			throws SQLException {
		
		Iterator it = model.getTables().iterator();
		while (it.hasNext()) {
			Table table = (Table) it.next();
			ResultSet rs = metaData.getColumns(null, null, 
					table.getName(), null);
			
			ArrayList columns = new ArrayList();
			while (rs.next()) {
				columns.add(new Column(rs.getString("COLUMN_NAME")));
				log.info("database column name:" + rs.getString("COLUMN_NAME"));
			}
			if(!columns.containsAll(table.getColumns())) {
				return false;
			}
		}
		
		return true;
	}
}
