package org.riotfamily.riot.list.command.core;

import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.form.command.FormCommand;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.GotoUrlResult;
import org.riotfamily.riot.list.command.support.AbstractCommand;
import org.riotfamily.riot.list.ui.render.RenderContext;

/**
 * Command that deletes an item. To prevent accidental deletion a confirmation
 * message is displayed.
 */
public class DeleteCommand extends AbstractCommand implements FormCommand {

	public boolean isEnabled(RenderContext context) {
		return context.getObjectId() != null;
	}
	
	public String getConfirmationMessage(CommandContext context) {
		
		Class clazz = context.getBeanClass();
		Object item = context.getItem();
		
		String type = context.getMessageResolver().getClassLabel(null, clazz);
		String label = context.getEditorDefinition().getLabel(item);
		
		Object[] args = new Object[] {label, type, context.getObjectId()};
		
		return context.getMessageResolver().getMessage("confirm.delete", args, 
				"Do you really want to delete this element?");
	}
	
	public CommandResult execute(CommandContext context) {

		ListDefinition listDef = context.getListDefinition();
		String parentId = context.getParentId();
		Object parent = EditorDefinitionUtils.loadParent(listDef, parentId);
		
		Object item = context.getItem();
		listDef.getListConfig().getDao().delete(item, parent);
		
		String url = context.getListDefinition().getEditorUrl(null, parentId);
		return new GotoUrlResult(url);
	}

}
