package org.riotfamily.revolt.refactor;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.Script;
import org.riotfamily.revolt.definition.Column;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class AddColumn implements Refactoring {

	private String table;

	private Column column;

	public AddColumn() {
	}

	public AddColumn(String table, Column column) {
		this.table = table;
		this.column = column;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public Script getScript(Dialect dialect, SimpleJdbcTemplate template) {
		return dialect.addColumn(table, column);
	}

}
