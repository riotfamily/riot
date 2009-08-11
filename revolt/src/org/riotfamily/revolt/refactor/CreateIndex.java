package org.riotfamily.revolt.refactor;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.Script;
import org.riotfamily.revolt.definition.Index;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class CreateIndex implements Refactoring {

	private String table;

	private Index index;
	
	public CreateIndex() {
	}

	public CreateIndex(String table, Index index) {
		this.table = table;
		this.index = index;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public void setIndex(Index index) {
		this.index = index;
	}

	public Script getScript(Dialect dialect, SimpleJdbcTemplate template) {
		return dialect.createIndex(table, index);
	}

}
