package org.riotfamily.riot.form.element.chooser;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.editor.TreeDefinition;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.ScriptResult;
import org.riotfamily.riot.list.command.support.AbstractCommand;
import org.riotfamily.riot.list.ui.render.RenderContext;

public class ChooseCommand extends AbstractCommand {

	private static final String DEFAULT_ID = "choose";
	
	public ChooseCommand() {
		setId(DEFAULT_ID);
	}
	
	public boolean isEnabled(RenderContext context) {
		HttpServletRequest request = context.getRequest();
		EditorDefinition target = (EditorDefinition) request.getAttribute(
				ChooserListController.TARGET_EDITOR_ATTR);
		
		ListDefinition targetList = EditorDefinitionUtils.getListDefinition(target);
		
		//FIXME This is kind of ugly:
		if (targetList instanceof TreeDefinition) {
			targetList = ((TreeDefinition) targetList).getNodeListDefinition();
		}
		
		if (context.getListDefinition().equals(targetList)) {
			return target.getBeanClass().isInstance(context.getItem());
		}
		return false;
	}
	
	public CommandResult execute(CommandContext context) {
		return new ScriptResult("parent.chosen('" + 
				context.getObjectId() + "')");
	}

}
