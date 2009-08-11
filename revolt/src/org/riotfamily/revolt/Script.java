package org.riotfamily.revolt;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.riotfamily.common.util.RiotLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class Script {

	private RiotLog log = RiotLog.get(Script.class);
	
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
