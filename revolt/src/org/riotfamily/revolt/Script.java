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
package org.riotfamily.revolt;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.riotfamily.common.log.RiotLog;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class Script {

	private List<SqlCallback> callbacks = new ArrayList<SqlCallback>();

	private StringBuffer buffer;

	private boolean nospace;
	
	private boolean manualExecutionOnly;
	
	public Script() {
	}

	public Script(String sql) {
		append(sql);
	}

	public Script append(String sql) {
		if (buffer == null) {
			newStatement();
		}
		else if (!nospace) {
			buffer.append(' ');
		}
		nospace = false;
		buffer.append(sql);
		return this;
	}
	
	public Script append(char c) {
		if (buffer == null) {
			newStatement();
		}
		else if (c == '(') {
			buffer.append(' ');
			nospace = true;
		}
		buffer.append(c);
		return this;
	}

	public Script append(Script script) {
		if (script != null) {
			newStatement();
			callbacks.addAll(script.getCallbacks());
			manualExecutionOnly |= script.isManualExecutionOnly();
		}
		return this;
	}

	public void newStatement() {
		if (buffer != null && buffer.length() > 0) {
			callbacks.add(new SqlCallback(buffer.toString()));
		}
		buffer = new StringBuffer();
	}

	public boolean isManualExecutionOnly() {
		return manualExecutionOnly;
	}

	public void forceManualExecution() {
		manualExecutionOnly = true;
	}

	public List<SqlCallback> getCallbacks() {
		newStatement();
		return callbacks;
	}

	public void execute(DataSource dataSource) {
		Assert.state(manualExecutionOnly == false, 
				"This script must be manually executed.");
		
		JdbcTemplate template = new JdbcTemplate(dataSource);
		for (StatementCallback callback : getCallbacks()) { 
			template.execute(callback);
		}
	}
	
	public String getSql() {
		StringBuffer sql = new StringBuffer();
		for (SqlProvider provider : callbacks) {
			sql.append(provider.getSql()).append(";\n");
		}
		return sql.toString();
	}

	public static class SqlCallback implements StatementCallback, SqlProvider {

		private RiotLog log = RiotLog.get(SqlCallback.class);
		
		private String sql;

		public SqlCallback(String sql) {
			this.sql = sql;
		}

		public String getSql() {
			return sql;
		}

		public Object doInStatement(Statement statement) 
				throws SQLException, DataAccessException {
			
			log.info("Revolt: " + sql);
			statement.execute(sql);
			return null;
		}
	}
}
