package org.riotfamily.revolt.refactor;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.Script;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class RenameColumn implements Refactoring {

	private String table;

	private String column;

	private String renameTo;

	
	public RenameColumn() {
	}

	public RenameColumn(String table, String column, String renameTo) {
		this.table = table;
		this.column = column;
		this.renameTo = renameTo;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public void setRenameTo(String renameTo) {
		this.renameTo = renameTo;
	}

	public void setTable(String table) {
		this.table = table;
	}
	
	public Script getScript(Dialect dialect, SimpleJdbcTemplate template) {
		return dialect.renameColumn(table, column, renameTo);
	}

}
