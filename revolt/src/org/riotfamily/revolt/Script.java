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
package org.riotfamily.revolt;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class Script {

	private Logger log = LoggerFactory.getLogger(Script.class);
	
	private List<String> statements = new ArrayList<String>();

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
			statements.addAll(script.getStatements());
			manualExecutionOnly |= script.isManualExecutionOnly();
		}
		return this;
	}

	public void newStatement() {
		if (buffer != null && buffer.length() > 0) {
			statements.add(buffer.toString());
		}
		buffer = new StringBuffer();
	}

	public boolean isManualExecutionOnly() {
		return manualExecutionOnly;
	}

	public void forceManualExecution() {
		manualExecutionOnly = true;
	}

	public List<String> getStatements() {
		newStatement();
		return statements;
	}

	public void execute(DataSource dataSource) {
		Assert.state(manualExecutionOnly == false, 
				"This script must be manually executed.");
		
		JdbcTemplate template = new JdbcTemplate(dataSource);
		for (String statement : getStatements()) {
			log.info(statement);
			template.execute(statement);
		}
	}
	
	public String getSql() {
		StringBuffer sql = new StringBuffer();
		for (String statement : getStatements()) {
			sql.append(statement).append(";\n");
		}
		return sql.toString();
	}

}
