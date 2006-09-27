package org.riotfamily.riot.form.element.chooser;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.GotoUrlResult;
import org.riotfamily.riot.list.command.support.AbstractCommand;
import org.riotfamily.riot.list.ui.render.RenderContext;
import org.springframework.util.Assert;

public class DescendCommand extends AbstractCommand {

	private static final String DEFAULT_ID ="descend";
		
	public DescendCommand() {
		setId(DEFAULT_ID);
	}
	
	public boolean isEnabled(RenderContext context) {
		HttpServletRequest request = context.getRequest();
		ListDefinition nextList = (ListDefinition) request.getAttribute(
				ChooserListController.NEXT_LIST_ATTR);
		
		return nextList != null;
	}
	
	public CommandResult execute(CommandContext context) {
		HttpServletRequest request = context.getRequest();
		EditorDefinition target = (EditorDefinition) request.getAttribute(
				ChooserListController.TARGET_EDITOR_ATTR);
		
		Assert.notNull(target, "No targetEditor attribute in context");
		
		ListDefinition nextList = (ListDefinition) request.getAttribute(
				ChooserListController.NEXT_LIST_ATTR);
		
		Assert.notNull(nextList, "No nextList attribute set in context");
		
		ChooserListController controller = (ChooserListController) 
				request.getAttribute(ChooserListController.CONTROLLER_ATTR);
		
		Assert.notNull(controller, "No ChooserListController in context");
		
		String url = controller.getUrl(target.getId(), nextList.getId(), 
				context.getObjectId());
		
		return new GotoUrlResult(url);
	}
}
