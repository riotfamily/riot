package org.riotfamily.core.screen.list.command.result;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;
import org.riotfamily.common.util.Generics;

@DataTransferObject
public class BatchResult implements CommandResult {

	private LinkedHashSet<CommandResult> batch = Generics.newLinkedHashSet();
	
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
	
	@RemoteProperty
	public String getAction() {
		return "batch";
	}
	
	public void add(CommandResult result) {
		batch.add(result);
	}
	
	@RemoteProperty
	public CommandResult[] getBatch() {
		CommandResult[] results = new CommandResult[batch.size()];
		return batch.toArray(results);
	}
	
}
