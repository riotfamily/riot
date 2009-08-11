package org.riotfamily.revolt.refactor;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.Script;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class DropColumn implements Refactoring {

	private String table;

	private String column;

	public DropColumn() {
	}

	public DropColumn(String table, String column) {
		this.table = table;
		this.column = column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public Script getScript(Dialect dialect, SimpleJdbcTemplate template) {
		return dialect.dropColumn(table, column);
	}

}
