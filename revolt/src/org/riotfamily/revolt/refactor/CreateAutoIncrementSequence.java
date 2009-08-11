package org.riotfamily.revolt.refactor;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.Script;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class CreateAutoIncrementSequence implements Refactoring {

	private String name;

	public CreateAutoIncrementSequence() {
	}

	public CreateAutoIncrementSequence(String name) {
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Script getScript(Dialect dialect, SimpleJdbcTemplate template) {
		return dialect.createAutoIncrementSequence(name);
	}

}
