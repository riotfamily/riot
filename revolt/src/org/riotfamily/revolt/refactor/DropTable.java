package org.riotfamily.revolt.refactor;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.Script;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class DropTable implements Refactoring {

	private String table;
	
	private boolean cascade;
	
	public DropTable() {
	}

	public DropTable(String table, boolean cascade) {
		this.table = table;
		this.cascade = cascade;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public boolean isCascade() {
		return cascade;
	}

	public void setCascade(boolean cascade) {
		this.cascade = cascade;
	}

	public Script getScript(Dialect dialect, SimpleJdbcTemplate template) {
		return dialect.dropTable(table, cascade);
	}

}
