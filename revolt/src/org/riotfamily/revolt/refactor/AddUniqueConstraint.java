package org.riotfamily.revolt.refactor;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.Script;
import org.riotfamily.revolt.definition.UniqueConstraint;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class AddUniqueConstraint implements Refactoring {

	private String table;

	private UniqueConstraint constraint;
	
	public AddUniqueConstraint() {
	}

	public AddUniqueConstraint(String table, UniqueConstraint constraint) {
		this.table = table;
		this.constraint = constraint;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public void setConstraint(UniqueConstraint constraint) {
		this.constraint = constraint;
	}
	
	public Script getScript(Dialect dialect, SimpleJdbcTemplate template) {
		return dialect.addUniqueConstraint(table, constraint);
	}

}
