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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.revolt.DatabaseOutOfSyncException;
import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.definition.Column;
import org.riotfamily.revolt.definition.Table;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass <fgnass@neteye.de>
 *
 */
public class LogTable {

	private static Log log = LogFactory.getLog(LogTable.class);
	
	private static final String TABLE_NAME = "revolt_change_log";
	
	private DataSource dataSource;
	
	private Dialect dialect;
	
	private String moduleName;
	
	private List appliedChanges;
	
	public LogTable(DataSource dataSource, Dialect dialect, String moduleName) {
		this.dataSource = dataSource;
		this.dialect = dialect;
		this.moduleName = moduleName;
		init();
	}

	private void init() {
		if (!exists()) {
			create();
		}
		JdbcTemplate template = new JdbcTemplate(dataSource);
		
		appliedChanges = template.query("select change_set_id, seq_nr from " 
				+ TABLE_NAME + " where module = ? order by seq_nr asc", 
				new Object[] { moduleName }, new RowMapper() {
					public Object mapRow(ResultSet rs, int rowNumber) 
							throws SQLException {
						
						if (rs.getInt(2) != rowNumber) {
							throw new DatabaseOutOfSyncException();
						}
						return rs.getString(1);
					}
				}
		);
	}
	
	public boolean hasEntries() {
		return !appliedChanges.isEmpty();
	}
	
	public void addChangeSet(String id, int sequenceNumber) {
		Assert.state(sequenceNumber == appliedChanges.size());
		appliedChanges.add(id);
		JdbcTemplate template = new JdbcTemplate(dataSource);
		template.update("insert into " + TABLE_NAME + 
				" (module, change_set_id, seq_nr) values(?,?,?)", new Object[] { 
				moduleName, id, new Integer(sequenceNumber) });
	}

	public boolean containsChangeSet(String id, int sequenceNumber) {
		if (appliedChanges.size() > sequenceNumber) {
			String appliedId = (String) appliedChanges.get(sequenceNumber);
			log.info("Applied ChangeSetId: " + appliedId);
			if (appliedId.equals(id)) {
				return true;
			}
			throw new DatabaseOutOfSyncException();
		}
		return false;
	}
	
	protected boolean exists() {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		Boolean exists = (Boolean) template.execute(new ConnectionCallback() {
			public Object doInConnection(Connection connection) 
					throws SQLException, DataAccessException {

				return Boolean.valueOf(connection.getMetaData().getTables(
						null, null, TABLE_NAME, null).next());
			}
		});
		return exists.booleanValue();
	}

	protected void create() {
		Table table = new Table(TABLE_NAME);
		table.addColumn(new Column("change_set_id", TypeMap.VARCHAR, 255));
		table.addColumn(new Column("module", TypeMap.VARCHAR, 255));
		table.addColumn(new Column("seq_nr", TypeMap.INTEGER));
		dialect.createTable(table).execute(dataSource);
	}
	
}
