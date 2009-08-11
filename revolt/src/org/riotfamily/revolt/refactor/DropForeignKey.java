package org.riotfamily.revolt.refactor;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.Script;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class DropForeignKey implements Refactoring {

	private String table;

	private String constraint;

	
	public DropForeignKey() {
	}

	public DropForeignKey(String table, String constraint) {
		this.table = table;
		this.constraint = constraint;
	}
	
	public void setTable(String table) {
		this.table = table;
	}

	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}

	public Script getScript(Dialect dialect, SimpleJdbcTemplate template) {
		return dialect.dropForeignKey(table, constraint);
	}

}
