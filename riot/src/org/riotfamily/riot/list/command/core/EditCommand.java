package org.riotfamily.riot.list.command.core;

import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.GotoUrlResult;
import org.riotfamily.riot.list.command.support.AbstractCommand;
import org.springframework.util.Assert;


/**
 * Command that displays the editor associated with the current list.
 */
public class EditCommand extends AbstractCommand {
	
	public CommandResult execute(CommandContext context) {
		EditorDefinition editorDefinition = context.getEditorDefinition();
		Assert.notNull(editorDefinition, "An EditorDefinition must be set");

		String url = editorDefinition.getEditorUrl(
				context.getObjectId(), context.getParentId());
		
		return new GotoUrlResult(url);
	}
}
