package org.riotfamily.riot.list.command.result;

import org.riotfamily.riot.list.command.CommandContext;

public class ShowListResult extends GotoUrlResult {

	public ShowListResult(CommandContext context) {
		super(context.getListDefinition().getEditorUrl(
				null, context.getParentId()));
	}

}
