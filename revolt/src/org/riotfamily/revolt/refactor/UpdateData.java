package org.riotfamily.revolt.refactor;

import java.util.Collection;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.EvolutionException;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.Script;
import org.riotfamily.revolt.definition.UpdateStatement;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class UpdateData implements Refactoring {

	private Collection<UpdateStatement> statements;
	
	public UpdateData(Collection<UpdateStatement> statements) {
		this.statements = statements;
	}

	public Script getScript(Dialect dialect, SimpleJdbcTemplate template) {
		for (UpdateStatement statement : statements) {
			if (statement.supports(dialect)) {
				return new Script(statement.getSql());
			}
		}
		throw new EvolutionException("No update statement for dialect " 
				+ dialect.getName());
	}
	
}
