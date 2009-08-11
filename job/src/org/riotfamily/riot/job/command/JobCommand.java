package org.riotfamily.riot.job.command;

import java.util.Map;

import org.riotfamily.common.util.Generics;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;

public class JobCommand extends AbstractCommand {

	private String jobType;

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	
	@Override
	protected String getAction(CommandContext context) {
		return jobType;
	}
	
	public CommandResult execute(CommandContext context, Selection selection) {
		/*
		String objectId = selection.getSingleObjectId() != null
				? selection.getSingleObjectId() 
				: context.getParentId();

		Map<String, String> attributes = Generics.newHashMap();
		attributes.put("type", jobType);
		attributes.put("objectId", objectId);
		//String url = getRuntime().getUrlForHandler("jobUIController", attributes);
		return new GotoUrlResult(context, ServletUtils.addParameter(url, 
				"title", getLabel(context.getMessageResolver())));
		*/
		return null;
	}
}
