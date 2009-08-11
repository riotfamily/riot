package org.riotfamily.revolt;

import java.util.List;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class ChangeSet implements Refactoring {

	private EvolutionHistory history;
	
	private String id;

	private int sequenceNumber;
	
	private List<Refactoring> refactorings;

	
	public ChangeSet(String id, List<Refactoring> refactorings) {
		this.id = id;
		this.refactorings = refactorings; 
	}

	public String getId() {
		return this.id;
	}

	public int getSequenceNumber() {
		return this.sequenceNumber;
	}

	public void setHistory(EvolutionHistory history) {
		this.history = history;
	}

	public String getModuleName() {
		return history.getModuleName();
	}
	
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public Script getScript(Dialect dialect, SimpleJdbcTemplate template) {
		try {
			Script script = new Script();
			for (Refactoring refactoring : refactorings) {
				Script s = refactoring.getScript(dialect, template);
				if (s != null) {
					script.append(s);
				}
			}
			return script;
		}
		catch (Exception e) {
			throw new EvolutionException(e);
		}
	}
	
}
