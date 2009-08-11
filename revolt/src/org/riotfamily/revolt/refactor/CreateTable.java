package org.riotfamily.revolt.refactor;

import java.util.List;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.Script;
import org.riotfamily.revolt.definition.Column;
import org.riotfamily.revolt.definition.Table;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class CreateTable implements Refactoring {

	private String name;
	
	private List<Column> columns;

	public CreateTable() {
	}
	
	public CreateTable(String name, List<Column> columns) {
		this.name = name;
		this.columns = columns;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Script getScript(Dialect dialect, SimpleJdbcTemplate template) {
		return dialect.createTable(new Table(name, columns));
	}

}
