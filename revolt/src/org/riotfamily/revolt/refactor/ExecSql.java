package org.riotfamily.revolt.refactor;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.Script;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.util.StringUtils;

public class ExecSql implements Refactoring {

	private String sql;

	public ExecSql(String sql) {
		this.sql = sql;
	}

	public Script getScript(Dialect dialect, SimpleJdbcTemplate template)
			throws Exception {
	
		Script script = new Script();
		String[] statements = StringUtils.delimitedListToStringArray(sql, ";");
		for (String s : statements) {
			script.append(s.trim());
			script.newStatement();
		}
		return script;
	}

}
