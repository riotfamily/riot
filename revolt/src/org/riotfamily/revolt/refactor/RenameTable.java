package org.riotfamily.revolt.refactor;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.Script;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class RenameTable implements Refactoring {

	private String table;

	private String renameTo;

	
	public RenameTable() {
	}

	public RenameTable(String table, String renameTo) {
		this.table = table;
		this.renameTo = renameTo;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public void setRenameTo(String renameTo) {
		this.renameTo = renameTo;
	}

	public Script getScript(Dialect dialect, SimpleJdbcTemplate template) {
		return dialect.renameTable(table, renameTo);
	}

}
