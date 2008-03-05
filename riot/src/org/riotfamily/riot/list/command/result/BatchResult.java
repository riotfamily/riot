package org.riotfamily.riot.list.command.result;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.riotfamily.riot.list.command.CommandResult;

public class BatchResult implements CommandResult {

	public static final String ACTION = "batch";
	
	private LinkedHashSet batch = new LinkedHashSet();
	
	public String getAction() {
		return ACTION;
	}

	public BatchResult() {
	}
	
	public BatchResult(Collection results) {
		this.batch.addAll(results);
	}
	
	public BatchResult(CommandResult first, CommandResult second) {
		add(first);
		add(second);
	}

	public void add(CommandResult result) {
		batch.add(result);
	}
	
	public CommandResult[] getBatch() {
		CommandResult[] results = new CommandResult[batch.size()];
		Iterator it = batch.iterator();
		for (int i = 0; it.hasNext(); i++) {
			results[i] = (CommandResult) it.next();
		}
		return results;
	}
	
}
