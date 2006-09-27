package org.riotfamily.pages.page;

import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.ReloadResult;
import org.riotfamily.riot.list.command.support.AbstractCommand;

/**
 * List command to toggle the publish-state of a page. 
 */
public class PublishPageCommand extends AbstractCommand {
			
	public String getConfirmationMessage(CommandContext context) {
		
		Class clazz = context.getBeanClass();
		Page page = (Page) context.getItem();
		
		String type = context.getMessageResolver().getClassLabel(null, clazz);
		String label = context.getEditorDefinition().getLabel(page);
		
		Object[] args = new Object[] {label, type, context.getObjectId()};
		
		if (page.isPublished()) {
			return context.getMessageResolver().getMessage("confirm.unpublish", 
					args, "Do you really want to unpublish this page?");
		}
		else {
			return context.getMessageResolver().getMessage("confirm.publish", 
					args, "Do you really want to publish this page?");
		}
	}
	
	public CommandResult execute(CommandContext context) {
		Page page = (Page) context.getItem();
		page.setPublished(!page.isPublished());
		context.getDao().update(page);
		return new ReloadResult();
	}

	public String getAction(CommandContext context) {
		Page page = (Page) context.getItem();
		return page.isPublished() ? "unpublishPage" : "publishPage"; 
	}
	
	

}
