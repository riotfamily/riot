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
package org.riotfamily.revolt;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.StatementCallback;

/**
 * @author Felix Gnass <fgnass@neteye.de>
 * 
 */
public class Script {

	private List callbacks = new ArrayList();

	private StringBuffer buffer;

	private boolean nospace;
	
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
		newStatement();
		callbacks.addAll(script.getCallbacks());
		return this;
	}

	public void newStatement() {
		if (buffer != null && buffer.length() > 0) {
			callbacks.add(new SqlCallback(buffer.toString()));
		}
		buffer = new StringBuffer();
	}

	public List getCallbacks() {
		newStatement();
		return callbacks;
	}

	public void execute(DataSource dataSource) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		Iterator it = getCallbacks().iterator();
		while (it.hasNext()) {
			StatementCallback callback = (StatementCallback) it.next();
			template.execute(callback);
		}
	}
	
	public String getSql() {
		StringBuffer sql = new StringBuffer();
		Iterator it = getCallbacks().iterator();
		while (it.hasNext()) {
			SqlProvider provider = (SqlProvider) it.next();
			sql.append(provider.getSql()).append(";\n");
		}
		return sql.toString();
	}

	public static class SqlCallback implements StatementCallback, SqlProvider {

		private static Log log = LogFactory.getLog(SqlCallback.class);
		
		private String sql;

		public SqlCallback(String sql) {
			this.sql = sql;
		}

		public String getSql() {
			return sql;
		}

		public Object doInStatement(Statement statement) 
				throws SQLException, DataAccessException {
			
			log.debug(sql);
			statement.execute(sql);
			return null;
		}
	}
}
