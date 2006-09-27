package org.riotfamily.riot.list.command.core;

import javax.servlet.http.HttpSession;

import org.riotfamily.riot.dao.ParentChildDao;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.SetRowStyleResult;
import org.riotfamily.riot.list.command.result.ShowListResult;
import org.riotfamily.riot.list.command.support.AbstractCommand;
import org.riotfamily.riot.list.ui.render.RenderContext;

public class CutCommand extends AbstractCommand {

	public static final String OBJECT_ID_ATTRIBUTE = 
			CutCommand.class.getName() + ".objectId";
	
	public static final String PARENT_ID_ATTRIBUTE = 
			CutCommand.class.getName() + ".parentId";
	
	public static final String LIST_DEFINITION_ATTRIBUTE = 
			CutCommand.class.getName() + ".listDefinition";
	
	private static final String CUT_ROW_STYLE = "cut";
	
	public boolean isEnabled(RenderContext context) {
		if (context.getDao() instanceof ParentChildDao) {
			HttpSession session = context.getRequest().getSession();
			if (context.getObjectId().equals(
					session.getAttribute(OBJECT_ID_ATTRIBUTE))) {
				
				context.addRowStyle(CUT_ROW_STYLE);
			}
			return true;	
		}
		return false;
	}
	
	public CommandResult execute(CommandContext context) {
		HttpSession session = context.getRequest().getSession();
		String previousObjectId = (String) session.getAttribute(
				OBJECT_ID_ATTRIBUTE);
		
		session.setAttribute(OBJECT_ID_ATTRIBUTE, context.getObjectId());
		session.setAttribute(PARENT_ID_ATTRIBUTE, context.getParentId());
		session.setAttribute(LIST_DEFINITION_ATTRIBUTE, context.getListDefinition());
		
		if (previousObjectId != null) {
			return new ShowListResult(context);
		}
		else {
			return new SetRowStyleResult(context.getObjectId(), CUT_ROW_STYLE);
		}
	}
}
