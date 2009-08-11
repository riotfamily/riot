package org.riotfamily.revolt.refactor;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.Script;
import org.riotfamily.revolt.definition.ForeignKey;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class AddForeignKey implements Refactoring {

	private String table;

	private ForeignKey foreignKey;

	public AddForeignKey() {
	}

	public AddForeignKey(String table, ForeignKey foreignKey) {
		this.table = table;
		this.foreignKey = foreignKey;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public void setForeignKey(ForeignKey foreignKey) {
		this.foreignKey = foreignKey;
	}

	public Script getScript(Dialect dialect, SimpleJdbcTemplate template) {
		return dialect.addForeignKey(table, foreignKey);
	}

}
