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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

import javax.sql.DataSource;

import org.riotfamily.revolt.ChangeSet;
import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.EvolutionException;
import org.riotfamily.revolt.Script;
import org.riotfamily.revolt.definition.Column;
import org.riotfamily.revolt.definition.Table;
import org.riotfamily.revolt.refactor.InsertData;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 *
 */
public class LogTable {

	private static final String TABLE_NAME = "revolt_change_log";
	
	private DataSource dataSource;
	
	private Dialect dialect;
	
	private Table table;
	
	private boolean exists;
	
	public LogTable(DataSource dataSource, Dialect dialect) {
		this.dataSource = dataSource;
		this.dialect = dialect;
		
		table = new Table(TABLE_NAME);
		table.addColumn(new Column("change_set_id", TypeMap.VARCHAR, 255));
		table.addColumn(new Column("module", TypeMap.VARCHAR, 255));
		table.addColumn(new Column("seq_nr", TypeMap.INTEGER));
		exists = DatabaseUtils.tableExists(dataSource, table);
	}
	
	public boolean exists() {
		return exists;
	}
	
	public boolean tableExists(String tableName) {
		return DatabaseUtils.tableExists(dataSource, new Table(tableName));
	}
	
	public Collection<String> getAppliedChangeSetIds(final String moduleName) {
		if (!exists) {
			return Collections.emptyList();
		}
		SimpleJdbcTemplate template = new SimpleJdbcTemplate(dataSource);
		return template.query("select change_set_id, seq_nr from " 
				+ TABLE_NAME + " where module = ? order by seq_nr asc", 
				new ParameterizedRowMapper<String>() {
					public String mapRow(ResultSet rs, int rowNumber) 
							throws SQLException {
						
						String changeSetId = rs.getString(1);
						int sequenceNumber = rs.getInt(2);
						
						if (sequenceNumber != rowNumber) {
							throw new EvolutionException(
									"Expected a ChangeSet with sequence number "
									+ rowNumber + " for module '" + moduleName 
									+ "' but found ChangeSet [" + changeSetId
									+ "] which has the number " 
									+ sequenceNumber);
						}
						return changeSetId;
					}
				}, 
				moduleName);
	}

	public Script getCreateTableScript() {
		Script script = dialect.createTable(table);
		if (DatabaseUtils.anyTablesExist(dataSource)) {
			script.forceManualExecution();
		}
		return script;
	}

	public Script getInsertScript(ChangeSet changeSet) {
		InsertData insert = new InsertData(TABLE_NAME);
		insert.addEntry("module", changeSet.getModuleName());
		insert.addEntry("change_set_id", changeSet.getId());
		insert.addEntry("seq_nr", new Integer(changeSet.getSequenceNumber()));
		return insert.getScript(dialect);
	}

}
