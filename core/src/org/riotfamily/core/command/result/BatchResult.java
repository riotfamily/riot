package org.riotfamily.core.command.result;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.riotfamily.common.util.Generics;
import org.riotfamily.core.command.CommandResult;

public class BatchResult implements CommandResult {

	public static final String ACTION = "batch";
	
	private LinkedHashSet<CommandResult> batch = Generics.newLinkedHashSet();
	
	public String getAction() {
		return ACTION;
	}

	public BatchResult() {
	}
	
	public BatchResult(Collection<CommandResult> results) {
		this.batch.addAll(results);
	}
	
	public BatchResult(CommandResult... results) {
		for (CommandResult result : results) {
			add(result);
		}
	}

	public void add(CommandResult result) {
		batch.add(result);
	}
	
	public CommandResult[] getBatch() {
		CommandResult[] results = new CommandResult[batch.size()];
		return batch.toArray(results);
	}
	
}
