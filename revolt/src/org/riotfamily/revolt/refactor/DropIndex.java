package org.riotfamily.revolt.refactor;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.Script;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class DropIndex implements Refactoring {

	private String table;

	private String index;
	
	public DropIndex() {
	}

	public DropIndex(String table, String index) {
		this.table = table;
		this.index = index;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public Script getScript(Dialect dialect, SimpleJdbcTemplate template) {
		return dialect.dropIndex(table, index);
	}

}
